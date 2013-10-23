import java.io.Serializable;
import java.util.Random;

public class Item implements Serializable {
    int ID;
    int reserve;
    int currentPrice;
    String name;
    User user;

    public Item(User user, String name, int startPrice, int reserve) {
        this.name = name;
        this.reserve = reserve;
        this.ID = new Random(2000).nextInt();
        this.currentPrice = startPrice;
        this.user = user;
    }

    public Item(User user,String name, int reserve) {
        this.name = name;
        this.reserve = reserve;
        this.ID = new Random(2000).nextInt();
        this.currentPrice = 0;
        this.user = user;
    }

}
