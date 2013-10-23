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

    public void addItem(Item item) throws RemoteException {
        items.add(item);
    }

    public boolean addUser(User user) throws RemoteException {
        int i;
        for(i = 0; i < users.size(); i++) {
            if (users.get(i).email == user.email) {
                return false;
            }
        }
        users.add(user);
        return true;
    }

    public boolean login(User user) throws RemoteException {
        int i;
        for(i = 0; i < users.size(); i++) {
            if (users.get(i).userName == user.userName && users.get(i).email == user.email) {
                return true;
            }
        }
        return false;
    }

}
