import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class Auctioneer implements Auction {
    ArrayList<User> users = new ArrayList<User>();
    HashMap<Integer, Item> items = new HashMap<Integer, Item>();
    HashMap<Integer, Item> itemsClosed = new HashMap<Integer, Item>();
    int id = 0;

    public Auctioneer() {
        super();
    }

    public int bid(int ID, int bidAmount, User user) throws RemoteException {
        Item item = items.get(ID);

        if (item == null) {
            System.out.println("[-][item]: someone tried to bid on invalid auction");
            return 3;
        }

        if (item.getBidder().equalsIgnoreCase(user.getEmail())) {
            System.out.println("[-][item]: " + item.name + " was bid on by its owner");
            return 2;
        }
        if (item.getPrice() > bidAmount) {
            System.out.println("[-][item]: bid on " + item.name + " was too little");
            return 1;
        }

        item.setPrice(bidAmount);
        item.setBidder(user);
        System.out.println("[+][item]: bid on " + item.name + " was successful");
        return 0;
    }

    public boolean addItem(Item item) throws RemoteException {
        id = id +  1;
        items.put(id, item);

        System.out.println("[+][item]: " + item.name + " (" + id + ") added to the auction list");
        return true;
    }

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

    public boolean login(User user) throws RemoteException {
        int i;
        for(i = 0; i < users.size(); i++) {
            if (users.get(i).getName().equalsIgnoreCase(user.getName()) && users.get(i).getEmail().equalsIgnoreCase(user.getEmail())) {
                System.out.println("[+][user]: " + user.email + " logged in");
                return true;
            }
        }
        System.out.println("[-][user]: invalid credentials for login");
        return false;
    }

    public HashMap getAvailableAuctions() throws RemoteException {
        return items;
    }

    public ArrayList getSoldAuctions(User user) throws RemoteException {


        return null;
    }

    public int closeAuction(int ID, User user) {
        Item item = items.get(ID);

        if (item == null) {
            System.out.println("[-][item]: someone tried to close an invalid auction");
            return 3;
        }

        if (!item.getBidder().equalsIgnoreCase(user.getEmail())) {
            System.out.println("[-][item]: " + user.getName() + " tried to close auction that was not their own");
            return 2;
        }
        if (item.getReserve() > item.getPrice()) {
            System.out.println("[+][item]: " + item.name + " was closed, but didn't meet reserve");
            items.remove(ID);
            itemsClosed.put(ID, item);
            return 1;
        } else {
            System.out.println("[+][item]: " + item.name + " was closed successfully");
            items.remove(ID);
            itemsClosed.put(ID, item);
            return 0;
        }
    }

}
