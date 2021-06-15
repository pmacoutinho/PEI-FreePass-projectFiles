package com.example.freepass;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DataEncryption {

    static final String alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static String mainSalt;

    public static String main(String website, String username) throws InvalidKeySpecException, NoSuchAlgorithmException {

        String password = website + username;
    
        String pepper = genPepper();
        byte[] hash = genHash(password, pepper);
        
        String res = toHex(hash) + "--//--" + mainSalt;
        return res;
    }

    public static boolean decryption(String website, String username, String data) throws InvalidKeySpecException, NoSuchAlgorithmException {

        String array[] = data.split("--//--", 2);

        String password = website + username;
        boolean pepper_check = true;
        int i = 0;

        while(pepper_check){
            if( i == alpha.length() - 1 ) break;
            byte[] hash = desHash( password + alpha.charAt(i), array[1] );
            if( array[0].equals(toHex(hash))) return true;
            i++;
        }
        return false;
    }

    private static String genPepper(){
        return "" + alpha.charAt((int)(Math.random() * (alpha.length() + 1) ));
    }

    private static byte[] genHash(String master, String pepper)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
                
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        mainSalt = Base64.getEncoder().encodeToString(salt);

        String password = master + pepper;

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 28 * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        return factory.generateSecret(spec).getEncoded();
    }

    private static byte[] desHash(String master, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
                
        byte[] newsalt = Base64.getDecoder().decode(salt);


        PBEKeySpec spec = new PBEKeySpec(master.toCharArray(), newsalt, 10000, 28 * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        return factory.generateSecret(spec).getEncoded();
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }

}
