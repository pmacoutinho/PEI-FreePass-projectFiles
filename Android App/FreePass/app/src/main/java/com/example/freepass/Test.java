/*package com.example.freepass;
import java.math.BigInteger;

import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.macs.HMac;

public class Test {
    public String pbkdf2(String secret, String salt, int iterations, int keyLength) {
        try
        {
            PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
            byte[] secretData = secret.getBytes();
            byte[] saltData = salt.getBytes();
            gen.init(secretData, saltData, iterations);
            byte[] derivedKey = ((KeyParameter)gen.generateDerivedParameters(keyLength * 8)).getKey();
            return toHex(derivedKey);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String hmac(String key) {
        try
        {
            HMac hmac = new HMac(new SHA256Digest());
            KeyParameter secret_key = new KeyParameter(key.getBytes());
            hmac.init(secret_key);
            byte[] result = new byte[hmac.getMacSize()];
            hmac.doFinal(result, 0);
            return toHex(result);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }
}*/

/*package com.example.freepass;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Test2 {

    public static void main(String[] args)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#$%&'()*+,-./:;<=>?@[]^_`{|}~";
        String workbench = "test_workbench";
        String site = "testSite";
        String user = "user";
        String master = "password";
        int length = 15;
        int count = 1;

        String pepper = genPepper(workbench, count);
        byte[] hash = genHash(user, site, master, pepper);
        BigInteger value = new BigInteger(toHex(hash), 16);

        System.out.print(generate("", value , alpha,  length));
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }

    private static String genPepper(String pep, int count) {
        return "" + pep.charAt((pep.length() - 1) / count) + pep.charAt((pep.length() - 1) % count);
    }

    private static byte[] genHash(String user, String site, String master, String pepper)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = (user + site).getBytes();
        String password = master + pepper;

        //TODO: [HIGH PRIO
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 100000, 128);
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
}*/
