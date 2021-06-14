package com.example.freepass;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DataEncryption {

    static final String alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String main(String website, String username) throws InvalidKeySpecException, NoSuchAlgorithmException {

        String pepper = genPepper(website + username + alpha);
        byte[] hash = genHash(website, username, pepper);
        BigInteger value = new BigInteger(toHex(hash), 16);

        return generate("", value , alpha,  28);
    }

    private static String genPepper(String pep) {
        return "" + pep.charAt((pep.length() - 1)) + pep.charAt((pep.length() - 1));
    }

    private static byte[] genHash(String site, String master, String pepper)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = site.getBytes();
        String password = master + pepper;

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 28 * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        return factory.generateSecret(spec).getEncoded();
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
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
