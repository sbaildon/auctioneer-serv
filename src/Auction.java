import java.rmi.*;
import java.util.ArrayList;

public interface Auction extends Remote {

    public void bid(long itemNumber, long bidAmount)
            throws RemoteException;

    public boolean addItem(Item item)
            throws RemoteException;

    public boolean closeAuction(int id, User user)
            throws RemoteException;

    public boolean addUser(User user)
            throws RemoteException;

    public boolean login(User user)
            throws RemoteException;

    public ArrayList getAvailableAuctions()
            throws RemoteException;

    public ArrayList getWonAuctions(User user)
            throws RemoteException;
}
