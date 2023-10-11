package zone.yby.lab.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashCode {
    public static String md5Code(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return getString(input, digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encode(String input, String salt) {
        return sha256Code(sha256Code(input + salt) + salt);
    }

    public static String sha1Code(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            return getString(input, digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getString(String input, MessageDigest digest) {
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String sha256Code(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return getString(input, digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //murmurhash
    public static int murmurhash(String key) {
        byte[] data = key.getBytes(StandardCharsets.UTF_8);
        int m = 0x5bd1e995;
        int r = 24;
        int h = 0;
        int len = data.length;
        int len_4 = len >> 2;
        for (int i = 0; i < len_4; i++) {
            int i_4 = i << 2;
            int k = data[i_4 + 3];
            k = k << 8;
            k = k | (data[i_4 + 2] & 0xff);
            k = k << 8;
            k = k | (data[i_4 + 1] & 0xff);
            k = k << 8;
            k = k | (data[i_4] & 0xff);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }
        int len_m = len_4 << 2;
        int left = len - len_m;
        if (left != 0) {
            if (left >= 3) {
                h ^= (int) data[len - 3] << 16;
            }
            if (left >= 2) {
                h ^= (int) data[len - 2] << 8;
            }
            if (left >= 1) {
                h ^= (int) data[len - 1];
            }
            h *= m;
        }
        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;
        return h;
    }
}
