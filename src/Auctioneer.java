import java.rmi.RemoteException;
import java.util.ArrayList;

public class Auctioneer implements Auction {
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<Item> items = new ArrayList<Item>();

    public Auctioneer() {
        super();
    }

    public int bid(int ID, int bidAmount) throws RemoteException {
        int i;
        for (i = 0; i < items.size(); i++) {
            if (items.get(i).currentPrice < bidAmount) {
                items.get(i).currentPrice = bidAmount;
                System.out.println("[+][item]: " + items.get(i).name + " (" + items.get(i).ID + ") was bid on");
                return 0;
            }
        }
        System.out.println("[-][item]: failed bid on " + items.get(i).name + " (" + items.get(i).ID + ")");
        return 1;
    }

    public boolean addItem(Item item) throws RemoteException {
        if (items.add(item)) {
            System.out.println("[+][item]: " + item.name + " (" + item.ID + ") added to the auction list");
            return true;
        } return false;
    }

    public boolean addUser(User user) throws RemoteException {
        int i;
        for(i = 0; i < users.size(); i++) {
            if (users.get(i).email.equalsIgnoreCase(user.email)) {
                System.out.println("[-][user]: attempted to add duplicate user " + user.email);
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
            if (users.get(i).userName.equalsIgnoreCase(user.userName) && users.get(i).email.equalsIgnoreCase(user.email)) {
                System.out.println("[+][user]: " + user.email + " logged in");
                return true;
            }
        }
        return false;
    }

    public ArrayList getAvailableAuctions() {
        ArrayList<Item> auctions = new ArrayList<Item>();
        int i;

        for (i = 0; i < items.size(); i++) {
            if (items.get(i).won == false) {
                auctions.add(items.get(i));
            }
        }

        return auctions;
    }

    public ArrayList getWonAuctions(User user) {
        ArrayList<Item> auctions = new ArrayList<Item>();
        String email = user.email;

        int i;
        for (i = 0; i < items.size(); i++) {
            if (items.get(i).won == true) {
                if (items.get(i).bidder.email.equalsIgnoreCase(email)) {
                    auctions.add(items.get(i));
                }
            }
        }

        return auctions;
    }

    public int closeAuction(int id, User user) {
        int i;
        for (i = 0; i < items.size(); i++) {
            if (items.get(i).ID == id && items.get(i).owner.userName.equalsIgnoreCase(user.userName)) {
                items.get(i).won = true;
                if (items.get(i).currentPrice > items.get(i).reserve) {
                    System.out.println("[+][item]: " + items.get(i).name + " (" + items.get(i).ID + ") was closed and beat its reserve");
                    return 2;
                } else
                    System.out.println("[+][item]: " + items.get(i).name + " (" + items.get(i).ID + ") was closed but didn't beat reserve");
                    return 1;
            }
        }
        System.out.println("[-][item]: "  + items.get(i).name + " (" + items.get(i).ID + ") could not be closed");
        return 0;
    }

}
