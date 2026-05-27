package com.example.pattatransfer.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class containing security hashing helpers.
 */
public class HashUtility {

    /**
     * Hashes a plain text string into a SHA-256 hexadecimal string.
     * 
     * @param input The plain text password/input
     * @return The 64-character hexadecimal representation of the SHA-256 hash
     */
    public static String hashSHA256(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 hashing algorithm not found in environment", e);
        }
    }
}
