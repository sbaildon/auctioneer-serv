import java.io.*;

public class User implements Serializable {
    String email;
    String userName;

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    protected String getName() {
        return this.userName;
    }

    protected String getEmail() {
        return this.email;
    }
}
