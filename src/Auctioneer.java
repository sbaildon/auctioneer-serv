import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Auctioneer implements Auction {
    ArrayList<User> users = new ArrayList<User>();
    HashMap<Integer, Item> items = new HashMap<Integer, Item>();
    HashMap<Integer, Item> itemsClosed = new HashMap<Integer, Item>();
    KeyGen keyGen = new KeyGen();
    int id = 0;

    public Auctioneer() {
        super();
    }

    /*
     * Please take note of the correct return values, so they can be
     * handled properly at the client end.
     * 3 = fail, 2 = can't bid on own items, 1 = needs more money, 0 = success
     */
    public int bid(int ID, double bidAmount, User user) throws RemoteException {
        Item item = items.get(ID);

        if (item == null) {
            System.out.println("[-][item]: " + user.getEmail() + " tried to bid on invalid auction");
            return 3;
        }

        if (item.getOwner().equalsIgnoreCase(user.getEmail())) {
            System.out.println("[-][item]: " + item.name + " (" + ID + ") was bid on by its owner");
            return 2;
        }
        if (item.getPrice() > bidAmount) {
            System.out.println("[-][item]: bid on " + item.name + " (" + ID + ") was too little");
            return 1;
        }

        System.out.println("[+][item]: bid on " + item.name + " was successful £" + item.getPrice() + " -> £" + bidAmount);
        items.get(ID).setPrice(bidAmount);
        items.get(ID).setBidder(user);
        return 0;
    }

    public int bid(int ID, double bidAmount, SealedObject user) throws RemoteException {
        return bid(ID, bidAmount, (User) unseal(user));
    }

    public boolean addItem(Item item) throws RemoteException {
        id = id +  1;
        items.put(id, item);

        System.out.println("[+][item]: " + item.name + " (" + id + ") added to the auction list");
        return true;
    }

    public boolean addItem(SealedObject item) throws RemoteException {
        return addItem((Item) unseal(item));
    }

    /*
     * Every user must have a unique identifier, in this case
     * it is email. User won't be added unless it's unique
     */
    public boolean addUser(User user) throws RemoteException {
        int i;
        for(i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equalsIgnoreCase(user.getEmail())) {
                System.out.println("[-][user]: attempted to add duplicate user " + user.getEmail());
                return false;
            }
        }
        users.add(user);
        System.out.println("[+][user]: " + user.email + " was registered");
        return true;
    }

    public boolean addUser(SealedObject user) throws RemoteException {
        boolean cont;
        User tmp;

        tmp = (User) unseal(user);
        cont = addUser((User) unseal(user));

        return cont;
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

    public boolean login(SealedObject user) throws RemoteException {
        return login((User) unseal(user));
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

    public HashMap getSoldAuctions(SealedObject user) throws RemoteException {
        return getSoldAuctions((User) unseal(user));
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

    public int closeAuction(int ID, SealedObject user) throws RemoteException {
        return closeAuction(ID, (User) unseal(user));
    }

    public String getAuctionWinner(int ID) {
        Item item = itemsClosed.get(ID);

        if (item == null) {
            return "Shouldn't have found null";
        }

        return item.getBidder();
    }

    private Object unseal(SealedObject obj) {
        try {
            return obj.getObject(getKey());
        } catch (Exception e) {
            System.out.print("failed");
        }

        return null;
    }

    private SecretKey getKey() {
        try {
            FileInputStream fis = new FileInputStream("files/secret.key");
            ObjectInputStream ois = new ObjectInputStream(fis);
            SecretKey obj = (SecretKey) ois.readObject();
            ois.close();
            return obj;
        } catch (Exception e) {
            System.out.println("Failed reading key\n\n" + e);
        }
        return null;
    }

}
