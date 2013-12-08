import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jgroups.*;
import org.jgroups.blocks.ReplicatedHashMap;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;

public class Auctioneer implements Auction{
    ArrayList<User> users = new ArrayList<User>();
    HashMap<Integer, Item> items = new HashMap<Integer, Item>();
    HashMap<Integer, Item> itemsClosed = new HashMap<Integer, Item>();
    Channel channel;
    RpcDispatcher disp;
    RequestOptions opts = new RequestOptions(ResponseMode.GET_ALL, 5000);

    int id = 0;

    public Auctioneer() {
        super();
        start();
    }

    private void start() {
        try {
            channel = new JChannel();
            disp = new RpcDispatcher(channel, this);
            channel.connect("AuctioneerServerCluster");
            System.out.println("Connected to cluster");
            User user = new User("sean", "email");

        } catch (Exception e) {}
    }

    /*
     * Please take note of the correct return values, so they can be
     * handled properly at the client end.
     * 3 = fail, 2 = can't bid on own items, 1 = needs more money, 0 = success
     */
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

    public int bid(String email, SealedObject bidItem) throws RemoteException {
        SecretKey tmpKey = getKey(email);
        return bid((BidItem) unseal(bidItem, tmpKey));
    }

    public boolean addItem(Item item) throws RemoteException {
        id = id +  1;
        items.put(id, item);

        System.out.println("[+][item]: " + item.name + " (" + id + ") added to the auction list");
        return true;
    }

    public boolean addItem(String email, SealedObject item) throws RemoteException {
        SecretKey tmpKey = getKey(email);
        return addItem((Item) unseal(item, tmpKey));
    }

    /*
     * Every user must have a unique identifier, in this case
     * it is email. User won't be added unless it's unique
     */
    public SecretKey addUser(User user) throws RemoteException {
        try {
        } catch (Exception e) { System.out.println("Failed"); }
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

    public static void printUser(User user) {
        System.out.println(user.getEmail());
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

    /*
     * Provide a user and find all auctions where this particular
     * user is the winner
     */
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

    /*
     * The user argument user is needed to check that they have the correct
     * privileges to close the specified auction
     */
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

    public int closeAuction(int ID, String email, SealedObject user) throws RemoteException {
        SecretKey tmpKey = getKey(email);
        return closeAuction(ID, (User) unseal(user, tmpKey));
    }

    public String getAuctionWinner(int ID) {
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

}
