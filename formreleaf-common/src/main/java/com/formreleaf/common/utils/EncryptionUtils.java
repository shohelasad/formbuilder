package com.formreleaf.common.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/13/15.
 */
public class EncryptionUtils {
    private static final String ALGO = "AES";
    private static final int KEYSZ = 128;// 128 default; 192 and 256 also possible

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static EncryptionUtils instance;

    public static EncryptionUtils getInstance() {
        if (instance == null) {
            instance = new EncryptionUtils();
        }

        return instance;
    }

    private EncryptionUtils() {
    }

    public SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGO);
        keyGenerator.init(KEYSZ);

        return keyGenerator.generateKey();
    }

    public String getKeyAsString(SecretKey key) {
        byte[] encoded = key.getEncoded();
        char[] hex = Hex.encodeHex(encoded);

        return String.valueOf(hex);
    }

    public SecretKey getKey(String data) throws DecoderException {
        char[] hex = data.toCharArray();

        byte[] encoded = Hex.decodeHex(hex);

        return new SecretKeySpec(encoded, ALGO);
    }

    public String encrypt(String fileName, InputStream inputStream, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");  //getting cipher for AES

        cipher.init(Cipher.ENCRYPT_MODE, key);  //initializing cipher for encryption with key
        final AlgorithmParameters params = cipher.getParameters();
        final byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

        long size = 0;
        //creating file output stream to write to file
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            //creating cipher output stream to write encrypted contents
            try (CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
                int read;
                byte buf[] = new byte[4096];

                while ((read = inputStream.read(buf)) != -1) {  /*reading from file*/
                    cos.write(buf, 0, read);  //encrypting and writing to file
                }
            }
        }
        char[] chars = Hex.encodeHex(iv);

        return String.valueOf(chars);
    }

    public void decrypt(String fileName, String key, String iv, OutputStream outputStream) throws Exception {
        SecretKey originalKey = EncryptionUtils.getInstance().getKey(key);

        //creating file input stream to read from file
        try (FileInputStream fis = new FileInputStream(fileName)) {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");   //getting cipher for AES
            byte[] bytes = Hex.decodeHex(iv.toCharArray());
            cipher.init(Cipher.DECRYPT_MODE, originalKey, new IvParameterSpec(bytes));  //initializing cipher for decryption with key
            //creating cipher input stream to read encrypted contents
            try (CipherInputStream cis = new CipherInputStream(fis, cipher)) {
                int read;
                byte buf[] = new byte[4096];
                while ((read = cis.read(buf)) != -1) {  /*reading from file*/
                    outputStream.write(buf, 0, read);  //decrypting and writing to file
                }
            }
        }
    }
}
