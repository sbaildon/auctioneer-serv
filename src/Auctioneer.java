import java.rmi.RemoteException;
import java.util.ArrayList;

public class Auctioneer implements Auction {
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<Item> items = new ArrayList<Item>();

    public Auctioneer() {
        super();
    }

    public void bid(long itemNumber, long bidAmount) throws RemoteException {
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
            if (items.get(i).winner == null) {
                auctions.add(items.get(i));
            }
        }

        return auctions;
    }

    public ArrayList getWonAuctions(User user) {
        ArrayList<Item> auctions = new ArrayList<Item>();

        int i;
        for (i = 0; i < items.size(); i++) {
            if (items.get(i).winner.email.equalsIgnoreCase(user.email)) {
                auctions.add(items.get(i));
            }
        }

        return auctions;
    }

}
