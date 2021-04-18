package ru.sibdigital.addcovid.utils;

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

public class EsiaUtil {

    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

    public static String getTimestamp() {
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    public static String getState() {
        return UUID.randomUUID().toString();
    }

    public static String getClientSecret(String clientId, String scope, String state, String timestamp) throws Exception {
        Security.addProvider(new JCP());

        final PrivateKey key = CMS.loadKey("ХамархановаИК 2021-02-05 10-31-59", "123456".toCharArray());
        final Certificate cert = CMS.loadCertificate("ХамархановаИК 2021-02-05 10-31-59");
        byte[] data = (scope + timestamp + clientId + state).getBytes(StandardCharsets.UTF_8);
        byte[] sign = CMS.CMSSignEx(data, key, cert, true, JCP.PROVIDER_NAME);
        String encodedSign = Base64.getEncoder().encodeToString(sign);
        return encodedSign;
    }
}
