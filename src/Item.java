import java.io.Serializable;
import java.util.Random;

public class Item implements Serializable {
    int ID;
    int reserve;
    int currentPrice;
    String name;
    User owner;
    User winner;

    public Item(User user, String name, int startPrice, int reserve) {
        this.name = name;
        this.reserve = reserve;
        this.ID = new Random().nextInt(2000);
        this.currentPrice = startPrice;
        this.owner = user;
        this.winner = null;
    }

    public Item(User user,String name, int reserve) {
        this.name = name;
        this.reserve = reserve;
        this.ID = new Random().nextInt(2000);
        this.currentPrice = 0;
        this.owner = user;
        this.winner = null;
    }

}