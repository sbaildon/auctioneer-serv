import java.io.Serializable;

public class BidItem implements Serializable {
    int itemId;
    double bidAmount;
    User user;

    public BidItem(int itemId, double bidAmount, User user) {
        this.itemId = itemId;
        this.bidAmount = bidAmount;
        this.user = user;
    }

    public int getItemId() {
        return itemId;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public User getUser() {
        return user;
    }
}
