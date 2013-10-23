import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

public class AuctioneerServer {

    public AuctioneerServer() {
        try {
            Auction a = new Auctioneer();
            Auction stub = (Auction) UnicastRemoteObject.exportObject(a, 0);
            Naming.rebind("rmi://localhost:2020/AuctioneerService", stub);
        } catch (Exception e) {
            System.out.println("Can't create server\n\n" + e);
        }
    }

    public static void main(String args[]) {
        new AuctioneerServer();
    }
}
