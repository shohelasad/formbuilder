package com.formreleaf.common.utils;

import java.util.Random;

/**
 * @author Bazlur Rahman Rokon
 * @since 7/13/15.
 */
public class LongCipher {
    private static LongCipher ourInstance = new LongCipher();
    private static Random PRNG = new Random();
    private String secret;

    public static LongCipher getInstance() {
        return ourInstance;
    }

    private LongCipher() {
        this.secret = "FormReleaf.com";
    }

    public String encrypt(long value) {
        int salt = PRNG.nextInt();
        long otp = generatePad(salt);
        long cipher = value ^ otp;
        return String.format("%08x%016x", salt, cipher);
    }

    public long decrypt(String ciphertext) {
        if (ciphertext.length() != 24)
            throw new IllegalArgumentException("Invalid cipher text");
        try {
            int salt = (int) Long.parseLong(ciphertext.substring(0, 8), 16);
            long cipher = Long.parseLong(ciphertext.substring(8, 16), 16) << 32
                    | Long.parseLong(ciphertext.substring(16), 16);
            long otp = generatePad(salt);
            return cipher ^ otp;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex value: "
                    + e.getMessage());
        }
    }

    private long generatePad(int salt) {
        String saltString = Integer.toString(salt);
        String lpad = saltString + secret;
        String rpad = secret + saltString;
        return ((long) lpad.hashCode()) << 32 | (long) rpad.hashCode();
    }
}
