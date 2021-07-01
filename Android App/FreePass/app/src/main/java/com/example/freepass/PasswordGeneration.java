package com.example.freepass;

import java.math.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

    //TODO: [MEDIUM PRIORITY] QUANTITY OF CHAR

public class PasswordGeneration {

    static String lowerCase = "abcdefghijklmnopqrstuvwxyz";
    static String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static String number = "0123456789";
    static String symbol = "!#$%&'()*+,-./:;<=>?@[]^_`{|}~";

    public static String main(String site, String user, String master, int length, int count, String workbench,
                              boolean lowerCase_checked, boolean upperCase_checked, boolean number_checked, boolean symbol_checked)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String alpha = "";

        //The user can pick if he wants to include lowercase, uppercase, numbers and/or symbols
        if (lowerCase_checked)
            alpha += lowerCase;
        if (upperCase_checked)
            alpha += upperCase;
        if (number_checked)
            alpha += number;
        if (symbol_checked)
            alpha += symbol;

        String pepper = genPepper(workbench, count);
        byte[] hash = genHash(user, site, master, pepper, length);
        BigInteger value = new BigInteger(toHex(hash), 16);

        return generate("", value , alpha,  length);
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

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, length * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        return factory.generateSecret(spec).getEncoded();
    }

    public static String generate(String res, BigInteger quo, String alpha, int max) {
        if (res.length() >= max)
            return res;

        String l = "" + alpha.length();
        BigInteger len = new BigInteger(l);
        BigInteger rem = quo.remainder(len);
        quo = quo.divide( len );
        res += alpha.charAt(Integer.parseInt(rem.toString()));

        return generate(res, quo, alpha, max);
    }

}
