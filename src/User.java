import java.io.*;

public class User implements Serializable {
    String email;
    String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    protected String getEmail() {
        return this.email;
    }

    protected String getPassword() {
        return this.password;
    }
}
