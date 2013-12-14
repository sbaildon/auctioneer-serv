import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jgroups.Channel;
import org.jgroups.JChannel;
import org.jgroups.*;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.RspList;
import org.jgroups.util.Util;

public class Auctioneer extends ReceiverAdapter implements Auction {
    ArrayList<User> users = new ArrayList<User>();
    HashMap<Integer, Item> items = new HashMap<Integer, Item>();
    HashMap<Integer, Item> itemsClosed = new HashMap<Integer, Item>();
    Channel channel;
    Channel stateChannel;
    RpcDispatcher disp;
    RequestOptions opts;

    HashMap<Integer, Object> state = new HashMap<Integer, Object>();

    int id = 0;

    public Auctioneer() {
        super();
        start();
        startState();
    }



    public void start() {
        try {
            opts = new RequestOptions(ResponseMode.GET_ALL, 1000);
            channel = new JChannel();
            disp = new RpcDispatcher(channel, this);
            channel.setReceiver(this);
            channel.connect("AuctioneerServerCluster", null, 0);
            System.out.println("Connected to cluster");
        } catch (Exception e) {
            System.out.println("Failed to connect to cluster");
        }
    }

    public void startState() {
        try {
            stateChannel = new JChannel();
            stateChannel.setReceiver(this);
            stateChannel.connect("StateCluster", null, 0);
            stateChannel.getState(null, 0);
        } catch (Exception e) {
            System.out.println("Failed to get state");
        }
    }

    public void getState(OutputStream output) {
        synchronized(state) {
            state.clear();
            state.put(1, users);
            state.put(2, items);
            state.put(3, itemsClosed);

            try {
                Util.objectToStream(state, new DataOutputStream(output));
            } catch (Exception e) {
                System.out.println("Couldn't output state");
            }
        }
    }

    public void setState(InputStream input) {
        HashMap<Integer, Object> stateReceived;

        try {
            stateReceived = (HashMap<Integer, Object>) Util.objectFromStream(new DataInputStream(input));
        } catch (Exception e) {
            System.out.println("Couldn't receive state");
            return;
        }
        synchronized (state) {
            state.clear();
            state = stateReceived;
        }

        users = (ArrayList<User>) stateReceived.get(1);
        items = (HashMap<Integer, Item>) stateReceived.get(2);
        itemsClosed = (HashMap<Integer, Item>) stateReceived.get(3);
    }

    public int bid(BidItem bidItem) throws RemoteException {
        int ID = bidItem.getItemId();
        double bidAmount = bidItem.getBidAmount();
        User user = bidItem.getUser();

        Item item = items.get(ID);

        if (item == null) {
            System.out.println("[-][item]: " + user.getEmail() + " tried to bid on invalid auction");
            return 3;
        }

        if (item.getOwner().equalsIgnoreCase(user.getEmail())) {
            System.out.println("[-][item]: " + item.name + " (" + ID + ") was bid on by its owner");
            return 2;
        }
        if (item.getPrice() >= bidAmount) {
            System.out.println("[-][item]: bid on " + item.name + " (" + ID + ") was too little");
            return 1;
        }

        System.out.println("[+][item]: bid on " + item.name + " was successful £" + item.getPrice() + " -> £" + bidAmount);
        items.get(ID).setPrice(bidAmount);
        items.get(ID).setBidder(user);
        return 0;
    }

    public int bidLocal(String email, SealedObject bidItem) throws RemoteException {
        SecretKey tmpKey = getKey(email);
        return bid((BidItem) unseal(bidItem, tmpKey));
    }

    public int bid(String email, SealedObject bidItem) throws RemoteException {
        RspList responses;
        try {
            responses = disp.callRemoteMethods(null, "bidLocal", new Object[] {email, bidItem}, new Class[] {String.class, SealedObject.class}, opts);
        } catch (Exception e) {
            responses = new RspList();
        }

        int response = (Integer) responses.getFirst();
        return response;
    }

    public boolean addItem(Item item) throws RemoteException {
        id = id +  1;
        items.put(id, item);

        System.out.println("[+][item]: " + item.name + " (" + id + ") added to the auction list");
        return true;
    }

    public boolean addItemLocal(String email, SealedObject item) throws RemoteException {
        SecretKey tmpKey = getKey(email);
        return addItem((Item) unseal(item, tmpKey));
    }

    public boolean addItem(String email, SealedObject item) throws RemoteException {
        RspList responses;
        try {
            responses = disp.callRemoteMethods(null, "addItemLocal", new Object[] {email, item}, new Class[] {String.class, SealedObject.class}, opts);
        } catch (Exception e) {
            responses = new RspList();
        }

        boolean bool = (Boolean) responses.getFirst();
        return bool;
    }

    public SecretKey addUserLocal(User user) throws RemoteException {
        int i;
        for(i = 0; i < users.size(); i++) {
                if (users.get(i).getEmail().equalsIgnoreCase(user.getEmail())) {
                System.out.println("[-][user]: attempted to add duplicate user " + user.getEmail());
                return null;
            }
        }
        users.add(user);
        System.out.println("[+][user]: " + user.email + " was registered");
        return KeyGen.generateKey(user.getEmail());
    }

    public SecretKey addUser(User user) throws RemoteException {
        RspList responses;
        try {
            responses = disp.callRemoteMethods(null, "addUserLocal", new Object[] {user}, new Class[]{User.class}, opts);
        } catch (Exception e) {
            responses = null;
        }

        SecretKey skey = (SecretKey) responses.getFirst();
        return skey;
    }

    public boolean login(User user) throws RemoteException {
        int i;
        for(i = 0; i < users.size(); i++) {
            if (users.get(i).getPassword().equals(user.getPassword()) && users.get(i).getEmail().equals(user.getEmail())) {
                System.out.println("[+][user]: " + user.email + " logged in");
                return true;
            }
        }
        System.out.println("[-][user]: invalid credentials for login");
        return false;
    }

    public boolean login(String email, SealedObject user) throws RemoteException {
        SecretKey tmpKey = getKey(email);
        return login((User) unseal(user, tmpKey));
    }

    public HashMap getAvailableAuctions() throws RemoteException {
        return items;
    }

    public HashMap getSoldAuctions(User user) throws RemoteException {
        HashMap<Integer, Item> auctions = new HashMap<Integer, Item>();
        String email = user.getEmail();

        if (itemsClosed.size() == 0) {
            return auctions;
        }

        for (Map.Entry<Integer, Item> e : itemsClosed.entrySet()) {
            if (e.getValue().getBidder().equals(email)) {
                auctions.put(e.getKey(), e.getValue());
            }
        }

        return auctions;
    }

    public HashMap getSoldAuctions(String email, SealedObject user) throws RemoteException {
        SecretKey tmpKey = getKey(email);
        return getSoldAuctions((User) unseal(user, tmpKey));
    }

    public int closeAuction(int ID, User user) throws RemoteException {
        Item item = items.get(ID);

        if (item == null) {
            System.out.println("[-][item]: " + user.getEmail() + " tried to close an invalid auction");
            return 3;
        }

        if (!item.getOwner().equalsIgnoreCase(user.getEmail())) {
            System.out.println("[-][item]: " + user.getEmail() + " tried to close auction that was not their own");
            return 2;
        }
        if (item.getReserve() > item.getPrice()) {
            itemsClosed.put(ID, items.remove(ID));
            System.out.println("[+][item]: " + item.getName() + " (" + ID + ") was closed, but didn't meet reserve");
            return 1;
        } else {
            itemsClosed.put(ID, items.remove(ID));
            System.out.println("[+][item]: " + item.getName() + " (" + ID + ") was closed successfully");
            return 0;
        }
    }

    public int closeAuctionLocal(int ID, String email, SealedObject user) throws RemoteException {
        SecretKey tmpKey = getKey(email);
        return closeAuction(ID, (User) unseal(user, tmpKey));
    }

    public int closeAuction(int id, String email, SealedObject user) throws RemoteException {
        RspList responses;
        try {
            responses = disp.callRemoteMethods(null, "closeAuctionLocal", new Object[] {id, email, user}, new Class[] {int.class, String.class, SealedObject.class}, opts);
        } catch (Exception e) {
            responses = new RspList();
        }

        int i = (Integer) responses.getFirst();
        return i;
    }

    public String getAuctionWinner(int ID) throws RemoteException {
        Item item = itemsClosed.get(ID);

        return item.getBidder();
    }

    private Object unseal(SealedObject obj, SecretKey skey) {
        try {
            return obj.getObject(skey);
        } catch (Exception e) {
            System.out.print("[-][skey] Could not unseal object");
        }

        return null;
    }

    private SecretKey getKey(String fileName) {
        try {
            FileInputStream fis = new FileInputStream("keys/" + fileName + ".key");
            ObjectInputStream ois = new ObjectInputStream(fis);
            SecretKey obj = (SecretKey) ois.readObject();
            ois.close();
            return obj;
        } catch (Exception e) {
            System.out.println("[-][skey] Failed reading key\n\n" + e);
        }
        return null;
    }

    public void closeChannel() {
        stateChannel.close();
        channel.close();
    }
}
