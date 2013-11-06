import java.io.Serializable;
import java.util.Random;

public class Item implements Serializable {
    int ID;
    int reserve;
    int currentPrice;
    boolean won;
    String name;
    User owner;
    User bidder;

    public Item(User user, String name, int startPrice, int reserve) {
        this.name = name;
        this.reserve = reserve;
        this.ID = new Random().nextInt(2000);
        this.currentPrice = startPrice;
        this.won = false;
        this.owner = user;
        this.bidder = user;
    }

    public Item(User user,String name, int reserve) {
        this.name = name;
        this.reserve = reserve;
        this.ID = new Random().nextInt(2000);
        this.currentPrice = 0;
        this.won = false;
        this.owner = user;
        this.bidder = user;
    }

}