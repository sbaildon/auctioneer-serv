import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class AuctioneerServer {

    public AuctioneerServer() {
        try {
            LocateRegistry.createRegistry(2020);
            Auction a = new Auctioneer();
            Auction stub = (Auction) UnicastRemoteObject.exportObject(a, 0);
            Naming.rebind("//localhost:2020/AuctioneerService", stub);
            System.out.println("Started server...");
        } catch (Exception e) {
            System.out.println("Can't create server\n\n" + e);
        }
    }

    public static void main(String args[]) {
        new AuctioneerServer();
    }
}
