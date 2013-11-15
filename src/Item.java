import java.io.Serializable;
import java.util.Random;

public class Item implements Serializable {
    int reserve;
    int currentPrice;
    String name;
    User owner;
    User bidder;

    public Item(User user, String name, int startPrice, int reserve) {
        this.name = name;
        this.reserve = reserve;
        this.currentPrice = startPrice;
        this.owner = user;
        this.bidder = user;
    }

    protected void setPrice(int price) {
        this.currentPrice = price;
    }

    protected int getPrice() {
        return this.currentPrice;
    }

    protected void setBidder(User user) {
        this.bidder = user;
    }

    protected String getBidder() {
        return this.bidder.email;
    }

    protected String getOwner() {
        return this.owner.email;
    }

    protected int getReserve() {
        return this.reserve;
    }

    protected String getName() {
        return this.name;
    }


}