import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
            if (users.get(i).getName().equals(user.getName()) && users.get(i).getEmail().equals(user.getEmail())) {
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

    public String getAuctionWinner(int ID) {
        Item item = itemsClosed.get(ID);

        if (item == null) {
            return "Shouldn't have found null";
        }

        return item.getBidder();
    }

}
