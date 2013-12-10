import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class AuctioneerServer {

    int serverID = 3;
    Auction a;

    public AuctioneerServer() {

        a = new Auctioneer();

        try {
            try {
                LocateRegistry.getRegistry("localhost", 2020);
            } catch (Exception e) {
                System.out.println("Registry not available");
                return;
            }

            Auction stub = (Auction) UnicastRemoteObject.exportObject(a, 0);
            Naming.rebind("//localhost:2020/AuctioneerService" + serverID, stub);


        } catch (Exception e) {
            System.out.println("Couldn't bind remote object\n\n" + e);
            return;
        }

        System.out.println("Server running with ID " + serverID);

        char input;
        Scanner inputScanner = new Scanner(System.in);

        do {
            input = inputScanner.next().charAt(0);
        } while (input != 'q');

        inputScanner.close();

        try {
            Naming.unbind("//localhost:2020/AuctioneerService" + serverID);
            UnicastRemoteObject.unexportObject(a, false);
        } catch (Exception e) {
            System.out.println("Couldn't unbind remote object");
        }

    }

    public static void main(String args[]){
        new AuctioneerServer();
    }
}
