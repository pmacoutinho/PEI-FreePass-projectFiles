
import java.math.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
public class Test{
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException{
        String s = PasswordGeneration.main("google", "tone", "tone2000", 128, 1, "String workbench",
        true, true, true, true, 
        27, 38, 45, 18, true);
        System.out.println(s);
    }
}