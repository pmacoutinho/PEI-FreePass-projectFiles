
import java.math.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

    //TODO: [HIGH PRIORITY] PASSWORDS TAKE WAY TO LONG TO GENERATE, THE PROBLEM IS IN THE genHASH() FUNCTION
    //TODO: [MEDIUM PRIORITY] QUANTITY OF CHAR

public class PasswordGeneration {

    static String lowerCase = "abcdefghijklmnopqrstuvwxyz";
    static String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static String number = "0123456789";
    static String symbol = "!#$%&'()*+,-./:;<=>?@[]^_`{|}~";
    private static int min_cnt;
    private static boolean loop;
    private static String finalRes;
    private static int lastStringLength;
    private static int numberStrings;
    private static int tmp ; 

    public static String main(String site, String user, String master, int length, int count, String workbench,
                              boolean lowerCase_checked, boolean upperCase_checked, boolean number_checked, boolean symbol_checked, 
                              int minLowerCase_checked, int minUpperCase_checked, int minNumber_checked, int minSymbol_checked, boolean first_call)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String alpha = "";
        String res = null;
        loop = false;
        min_cnt = 0;

        //The user can pick if he wants to include lowercase, uppercase, numbers and/or symbols
        if (lowerCase_checked)
            alpha += lowerCase;
        if (upperCase_checked)
            alpha += upperCase;
        if (number_checked)
            alpha += number;
        if (symbol_checked)
            alpha += symbol;
        if (minLowerCase_checked != 0 || minUpperCase_checked != 0 || minNumber_checked != 0 || minSymbol_checked != 0){
            loop = true;
            if (first_call){
                numberStrings = length / 10;
                lastStringLength = length % 10;
                tmp = numberStrings;
            }
        }        

        String pepper = genPepper(workbench, count); 
        byte[] hash = genHash(user, site, master, pepper, length);
        BigInteger value = new BigInteger(toHex(hash), 16);
        res = generate("", value , alpha,  length);

        /*while (loop){
            pepper = genPepper(workbench, count);
            hash = genHash(user + min_cnt, site + min_cnt, master + min_cnt, pepper + min_cnt, length);
            value = new BigInteger(toHex(hash), 16);
            res = generate("", value , alpha,  length);
            if(checkMin(res, minLowerCase_checked, minUpperCase_checked, minNumber_checked, minSymbol_checked))
                break;
            min_cnt += 1;
        }*/
        while (loop){
            pepper = genPepper(workbench, count);
            hash = genHash(user + min_cnt, site + min_cnt, master + min_cnt, pepper + min_cnt, length);
            value = new BigInteger(toHex(hash), 16);
            res = generate("", value , alpha,  length);
            if (checkMin(res, minLowerCase_checked / numberStrings, minUpperCase_checked / numberStrings,
                    minNumber_checked / numberStrings, minSymbol_checked / numberStrings)){
                if ( tmp == 0){
                    do{
                        pepper = genPepper(workbench, count);
                        hash = genHash(user + min_cnt, site + min_cnt, master + min_cnt, pepper + min_cnt, lastStringLength);
                        value = new BigInteger(toHex(hash), 16);
                        res = generate("", value , alpha,  lastStringLength);
                    }while(checkMin(res, minLowerCase_checked % numberStrings, minUpperCase_checked % numberStrings,
                             minNumber_checked % numberStrings, minSymbol_checked % numberStrings));
                    res = finalRes;
                    break;
                }
                tmp --;
                main( site,  user,  master,  10 ,  count,  workbench,
                        lowerCase_checked,  upperCase_checked,  number_checked,  symbol_checked, 
                        minLowerCase_checked / numberStrings,  minUpperCase_checked / numberStrings,
                        minNumber_checked / numberStrings,  minSymbol_checked / numberStrings, false);
            }
            min_cnt += 1;
        }


        return res;
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }

    private static String genPepper(String pep, int count) {
        return "" + pep.charAt((pep.length() - 1) / count) + pep.charAt((pep.length() - 1) % count);
    }

    private static byte[] genHash(String user, String site, String master, String pepper, int length)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = (user + site).getBytes();
        String password = master + pepper;
        PBEKeySpec spec=null;

        if(!loop)
            spec = new PBEKeySpec(password.toCharArray(), salt, 10000, length * 8);
        else 
            /*if( length <= 30)
                spec = new PBEKeySpec(password.toCharArray(), salt, 1000, length * 7);
            else if( length <= 60)
                spec = new PBEKeySpec(password.toCharArray(), salt, 500, length * 7);
            else if( length <= 90)
                spec = new PBEKeySpec(password.toCharArray(), salt, 250, length * 7);
            else 
                spec = new PBEKeySpec(password.toCharArray(), salt, 100, length * 7);*/
            spec = new PBEKeySpec(password.toCharArray(), salt, 1000, length * 8);
            
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        return factory.generateSecret(spec).getEncoded();
    }

    private static String generate(String res, BigInteger quo, String alpha, int max) {
        if (res.length() >= max)
            return res;

        String l = "" + alpha.length();
        BigInteger len = new BigInteger(l);
        BigInteger rem = quo.remainder(len);
        quo = quo.divide( len );
        res += alpha.charAt(Integer.parseInt(rem.toString()));

        return generate(res, quo, alpha, max);
    }

    private static boolean checkMin(String res, int minLowerCase_checked, int minUpperCase_checked, int minNumber_checked, int minSymbol_checked){
        int upper = 0, lower = 0, number = 0, special = 0;
        for(int i = 0; i < res.length(); i++){
            char ch = res.charAt(i);
            if (ch >= 'A' && ch <= 'Z')
                upper++;
            else if (ch >= 'a' && ch <= 'z')
                lower++;
            else if (ch >= '0' && ch <= '9')
                number++;
            else
                special++;
        }
        if(upper >= minUpperCase_checked && lower >= minLowerCase_checked && number >= minNumber_checked && special >= minSymbol_checked){
            finalRes += res;
            return true;
        }
        return false;
    }
}
