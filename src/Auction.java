import javax.crypto.SealedObject;
import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

public interface Auction extends Remote {

    public int bid(int itemID, double bidAmount, User user)
            throws RemoteException;

    public int bid(int itemID, double bidAmount, SealedObject user)
            throws RemoteException;

    public boolean addItem(Item item)
            throws RemoteException;

    public boolean addItem(SealedObject item)
            throws RemoteException;

    public int closeAuction(int id, User user)
            throws RemoteException;

    public int closeAuction(int id, SealedObject user)
            throws RemoteException;

    public boolean addUser(User user)
            throws RemoteException;

    public boolean addUser(SealedObject user)
            throws RemoteException;

    public boolean login(User user)
            throws RemoteException;

    public boolean login(SealedObject user)
            throws RemoteException;

    public HashMap getAvailableAuctions()
            throws RemoteException;

    public HashMap getSoldAuctions(User user)
            throws RemoteException;

    public HashMap getSoldAuctions(SealedObject user)
            throws RemoteException;

    public String getAuctionWinner(int id)
            throws RemoteException;
}
