import org.jgroups.Channel;
import org.jgroups.JChannel;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RpcDispatcher;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class AuctioneerServer {

    String serverID = "1";
    Auction a;
    Channel channel;
    RpcDispatcher disp;

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

            a.addUser(new User("email", "password"));
        } catch (Exception e) {
            System.out.println("Couldn't bind remote object\n\n" + e);
            return;
        }

        try {
            start();
        } catch (Exception e) {
            System.out.print("Couldn't connect to cluster");
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

    private void start() throws Exception {
        System.out.println("started");
        channel = new JChannel();
        channel.connect("AuctioneerServerCluster");
        RpcDispatcher disp =new RpcDispatcher(channel, a);
    }

    public static void main(String args[]){
        new AuctioneerServer();
    }
}
