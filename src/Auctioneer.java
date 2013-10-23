import java.rmi.RemoteException;

public class Auctioneer implements Auction {

    public Auctioneer() {
        super();
    }

    public void bid(long itemNumber, long bidAmount) throws RemoteException {
        System.out.println(itemNumber);
    }

    public void addItem(String name, long timeAvailable) throws RemoteException {
    }

}
