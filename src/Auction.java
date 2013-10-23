import java.rmi.*;

public interface Auction extends Remote {

    public void bid(long itemNumber, long bidAmount)
            throws RemoteException;

    public void addItem(String name, long timeAvailable)
            throws RemoteException;
}
