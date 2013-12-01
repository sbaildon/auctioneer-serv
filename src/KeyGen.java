import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Random;

public class KeyGen {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    static public SecretKey generateKey(String fileName) {
        File file;
        OutputStream stream;
        ObjectOutputStream objStream;
        file = new File("keys/" + fileName + ".key");

        try {
            stream = new FileOutputStream(file);
            objStream = new ObjectOutputStream(stream);

            DESKeySpec desKeySpec = new DESKeySpec(generateString().getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey skey = keyFactory.generateSecret(desKeySpec);

            objStream.writeObject(skey);

            stream.close();
            objStream.close();

            return skey;
        } catch (Exception e) {
            System.out.println("[-][skey] Could not generate SecretKey at " + fileName);
        }
            return null;
    }

    static private String generateString() {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(8);
        for( int i = 0; i < 8; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
}