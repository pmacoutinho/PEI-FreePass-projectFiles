
import java.math.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
    private static int newLength ; 


    public static String main(String site, String user, String master, int length, int count, String workbench,
                              boolean lowerCase_checked, boolean upperCase_checked, boolean number_checked, boolean symbol_checked, 
                              int minLowerCase_checked, int minUpperCase_checked, int minNumber_checked, int minSymbol_checked, boolean first_call)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String alpha = "";
        String res = "";
        
        loop = false;
        
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
            if(first_call){
                min_cnt = 0;
                finalRes= "";
            }
        }       
        
        numberStrings = length / 10;
        if( length < 10) numberStrings = length;
        tmp = numberStrings;
        newLength = minLowerCase_checked / numberStrings +  minUpperCase_checked / numberStrings +
                         minNumber_checked / numberStrings + minSymbol_checked / numberStrings;
        if( newLength == 0) newLength = 1;
        lastStringLength =  length - newLength * tmp ;
        System.out.println(numberStrings + "\t" + newLength + "\t" + lastStringLength);

        String pepper = genPepper(workbench, count); 
        byte[] hash = genHash(user, site, master, pepper, length);
        BigInteger value = new BigInteger(toHex(hash), 16);
        res = generate("", value , alpha,  length);

        while (loop){
            pepper = genPepper(workbench, count);
            hash = genHash(user + min_cnt, site + min_cnt, master + min_cnt, pepper + min_cnt, newLength);
            value = new BigInteger(toHex(hash), 16);
            res = generate("", value , alpha,  newLength);
            if (checkMin(res, minLowerCase_checked /  numberStrings, minUpperCase_checked /  numberStrings,
                            minNumber_checked / numberStrings, minSymbol_checked / numberStrings)){
                if ( tmp == 0){
                    while(true){
                        if( lastStringLength == 0) break;
                        pepper = genPepper(workbench, count);
                        hash = genHash(user + min_cnt, site + min_cnt, master + min_cnt, pepper + min_cnt, lastStringLength);
                        value = new BigInteger(toHex(hash), 16);
                        res = generate("", value , alpha,  lastStringLength);
                        min_cnt += 1;
                        if(checkMin(res,minLowerCase_checked % numberStrings , minUpperCase_checked % numberStrings ,
                                         minNumber_checked % numberStrings , minSymbol_checked % numberStrings )){
                                            finalRes += res;
                                            break;
                                         } 
                    }
                    res = finalRes;
                    break;
                }
                tmp --;
                finalRes += res;
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
            spec = new PBEKeySpec(password.toCharArray(), salt, 100, length * 8);
            
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

    public static boolean checkMin(String res, int minLowerCase_checked, int minUpperCase_checked, int minNumber_checked, int minSymbol_checked){
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
        if(upper >= minUpperCase_checked)
            if(lower >= minLowerCase_checked)
                if(number >= minNumber_checked)
                    if(special >= minSymbol_checked)
                        return true;
        return false;
    }
}
