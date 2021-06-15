
import java.math.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
public class Test{
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException{
        String s = PasswordGeneration.main("google", "tone", "tone2000", 2, 1, "String workbench",
        true, true, true, true, 
        1,1, 0, 0, true);
        System.out.println( s.length() );
        int upper = 0, lower = 0, number = 0, special = 0;
        for(int i = 0; i < s.length(); i++){
            char ch = s.charAt(i);
            if (ch >= 'A' && ch <= 'Z')
                upper++;
            else if (ch >= 'a' && ch <= 'z')
                lower++;
            else if (ch >= '0' && ch <= '9')
                number++;
            else
                special++;
        }
        System.out.println(lower + "\t" + upper + "\t" + number + "\t" + special);
        System.out.println(s);
    }
}