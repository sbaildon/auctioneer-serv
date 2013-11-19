import java.io.Serializable;
import java.util.Random;

public class Item implements Serializable {
    double reserve;
    double currentPrice;
    String name;
    User owner;
    User bidder;

    public Item(User user, String name, double startPrice, double reserve) {
        this.name = name;
        this.reserve = reserve;
        this.currentPrice = startPrice;
        this.owner = user;
        this.bidder = user;
    }

    protected void setPrice(double price) {
        this.currentPrice = price;
    }

    protected double getPrice() {
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

    protected double getReserve() {
        return this.reserve;
    }

    protected String getName() {
        return this.name;
    }


}