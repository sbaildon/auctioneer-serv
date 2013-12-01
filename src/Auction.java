import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

public interface Auction extends Remote {

    public int bid(int itemID, double bidAmount, User user)
            throws RemoteException;

    public int bid(int itemID, double bidAmount, String email, SealedObject user)
            throws RemoteException;

    public boolean addItem(Item item)
            throws RemoteException;

    public boolean addItem(String email, SealedObject item)
            throws RemoteException;

    public int closeAuction(int id, User user)
            throws RemoteException;

    public int closeAuction(int id, String email, SealedObject user)
            throws RemoteException;

    public SecretKey addUser(User user)
            throws RemoteException;

    public boolean login(User user)
            throws RemoteException;

    public boolean login(String email, SealedObject user)
            throws RemoteException;

    public HashMap getAvailableAuctions()
            throws RemoteException;

    public HashMap getSoldAuctions(User user)
            throws RemoteException;

    public HashMap getSoldAuctions(String email, SealedObject user)
            throws RemoteException;

    public String getAuctionWinner(int id)
            throws RemoteException;
}
