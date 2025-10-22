package org.example.hirehub.util;

import java.security.SecureRandom;

public class TokenUtil {
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMERIC = "0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateToken(int length, boolean alphaNumeric) {
        String chars = alphaNumeric ? ALPHANUMERIC : NUMERIC;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
