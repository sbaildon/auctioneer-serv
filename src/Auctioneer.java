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
                return 0;
            }
        }

        return 1;
    }

    public boolean addItem(Item item) throws RemoteException {
        if (items.add(item)) {
            System.out.println("Added item: " + item.name + " (" + item.ID + ") " + item.owner.userName);
            return true;
        } return false;
    }

    public boolean addUser(User user) throws RemoteException {
        int i;
        for(i = 0; i < users.size(); i++) {
            if (users.get(i).email.equalsIgnoreCase(user.email)) {
                System.out.println("Attempted to add duplicate user: " + user.email);
                return false;
            }
            System.out.println(users.get(i).email);
        }
        users.add(user);
        System.out.println("Added user: " + user.email);
        return true;
    }

    public boolean login(User user) throws RemoteException {
        int i;
        for(i = 0; i < users.size(); i++) {
            if (users.get(i).userName.equalsIgnoreCase(user.userName) && users.get(i).email.equalsIgnoreCase(user.email)) {
                System.out.println("User " + user.userName + " (" + user.email + ") logged in");
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

        int i;
        for (i = 0; i < items.size(); i++) {
            if (items.get(i).won == true) {
                if (items.get(i).bidder.email.equalsIgnoreCase(user.email)) {
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
                    return 2;
                } else
                    return 1;
            }
        }
        return 0;
    }

}
