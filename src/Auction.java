import java.rmi.*;

public interface Auction extends Remote {

    public void bid(long itemNumber, long bidAmount)
            throws RemoteException;

    public void addItem(Item item)
            throws RemoteException;

    public boolean addUser(User user)
            throws RemoteException;

    public boolean login(User user)
            throws RemoteException;
}
