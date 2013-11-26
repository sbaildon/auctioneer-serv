import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class KeyGen {

    File file;
    OutputStream stream;
    ObjectOutputStream objStream;
    int i;

    public void newKey(String fileName) {
        file = new File(fileName);
        try {
            System.out.println(++i);
            stream = new FileOutputStream(file);
            objStream = new ObjectOutputStream(stream);

            DESKeySpec desKeySpec = new DESKeySpec("xjhg6sa8".getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey skey = keyFactory.generateSecret(desKeySpec);

            objStream.writeObject(skey);

            stream.close();
            objStream.close();
        } catch (Exception e) {
            System.out.println("failed");
        }
    }
}