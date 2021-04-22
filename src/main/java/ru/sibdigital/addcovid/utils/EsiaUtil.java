package ru.sibdigital.addcovid.utils;

import lombok.extern.slf4j.Slf4j;
import ru.CryptoPro.JCP.JCP;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class EsiaUtil {

    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

    public static String getTimestamp() {
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    public static String getState() {
        return UUID.randomUUID().toString();
    }

    public static String getClientSecret(String value, String alias, String password) {
        try {
            Security.addProvider(new JCP());

            final PrivateKey key = CMS.loadKey(alias, password.toCharArray());
            final Certificate cert = CMS.loadCertificate(alias);
            byte[] data = value.getBytes(StandardCharsets.UTF_8);
            byte[] sign = CMS.CMSSignEx(data, key, cert, true, JCP.PROVIDER_NAME);
            String encodedSign = Base64.getEncoder().encodeToString(sign);
            return encodedSign;
        } catch (Exception e) {
            log.error("Client secret not generated: " + e.getMessage(), e);
        }
        return null;
    }
}
