package ru.sibdigital.addcovid.utils;

import java.security.MessageDigest;
import java.util.Arrays;

public class SHA256Generator {
    public static String generate(String... str){
        StringBuilder stringBuilder = new StringBuilder();

        Arrays.stream(str).forEach(s -> {
            stringBuilder.append(s);
            stringBuilder.append(" ");
        });

        String s = stringBuilder.toString();
        return sha256(s);

    }


    private static String sha256(String str) {
        try{
            String base = str;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
