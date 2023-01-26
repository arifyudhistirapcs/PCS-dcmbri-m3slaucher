package id.co.pcsindonesia.ia.diagnostic.util;

import android.util.Log;

import id.co.pcsindonesia.ia.diagnostic.BuildConfig;

import org.bouncycastle.util.encoders.Base64;

import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ENC {
    public static String encrypt(int var){
        String str = String.valueOf(var);
        return encf(str);
    }

    public static String encrypt(String var){
        return encf(var);
    }

    public static String encrypt(double var){
        String str = String.valueOf(var);
        return encf(str);
    }

    private static String encf(String strToEncrypt) {
        if(strToEncrypt != null) {
            if (strToEncrypt.length() > 0)
                try {
                    byte[] iv = {0, 1, 2, 3, 5, 7, 3, 0, 1, 4, 6, 3, 4, 9, 5, 3};
                    IvParameterSpec ivspec = new IvParameterSpec(iv);

                    SecretKeyFactory factory = SecretKeyFactory.getInstance(new String(android.util.Base64.decode(BuildConfig.KEY_FAC, android.util.Base64.DEFAULT)));
                    KeySpec spec = new PBEKeySpec(new String(android.util.Base64.decode(BuildConfig.ENC_KEY+"==", android.util.Base64.DEFAULT))
                            .toCharArray(), new String(android.util.Base64.decode(BuildConfig.ENC_SALT, android.util.Base64.DEFAULT))
                            .getBytes(), BuildConfig.APP_ITR, BuildConfig.ENC_LENGTH);
                    SecretKey tmp = factory.generateSecret(spec);
                    SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), new String(android.util.Base64.decode(BuildConfig.ENC, android.util.Base64.DEFAULT)));

                    Cipher cipher = Cipher.getInstance(new String(android.util.Base64.decode(BuildConfig.KEY_MODE, android.util.Base64.DEFAULT)));
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
                    byte[] encode = Base64.encode(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
                    return new String(encode);
                } catch (Exception e) {
                    Log.e("ENC", "Error while encrypting: " + e.toString());
                }
            return "";
        }
        return null;
    }

    public static String decrypt(String var){
        return decf(var);
    }

    private static String decf(String strToDecrypt) {
        if(strToDecrypt != null) {
            if(strToDecrypt.length() > 0)
                try {
                    byte[] iv = {0, 1, 2, 3, 5, 7, 3, 0, 1, 4, 6, 3, 4, 9, 5, 3};
                    IvParameterSpec ivspec = new IvParameterSpec(iv);

                    SecretKeyFactory factory = SecretKeyFactory.getInstance(new String(android.util.Base64.decode(BuildConfig.KEY_FAC, android.util.Base64.DEFAULT)));
                    KeySpec spec = new PBEKeySpec( new String(android.util.Base64.decode(BuildConfig.ENC_KEY+"==", android.util.Base64.DEFAULT))
                            .toCharArray(), new String(android.util.Base64.decode(BuildConfig.ENC_SALT, android.util.Base64.DEFAULT))
                            .getBytes(), BuildConfig.APP_ITR, BuildConfig.ENC_LENGTH);
                    SecretKey tmp = factory.generateSecret(spec);
                    SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(),
                            new String(android.util.Base64.decode(BuildConfig.ENC, android.util.Base64.DEFAULT)));

                    Cipher cipher = Cipher.getInstance(new String(android.util.Base64.decode(BuildConfig.KEY_MODE, android.util.Base64.DEFAULT)));
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
                    byte[] cipherData = cipher.doFinal(Base64.decode(strToDecrypt.getBytes(StandardCharsets.UTF_8)));
                    return new String(cipherData, "UTF-8");
                } catch (Exception e) {
                    Log.e("ENC", "Error while decrypting: " + e.toString());
                }
            return "";
        }
        return null;
    }
}

