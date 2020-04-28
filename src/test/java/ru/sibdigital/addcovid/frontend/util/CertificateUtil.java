package ru.sibdigital.addcovid.frontend.util;

import net.lightbody.bmp.mitm.CertificateAndKeySource;
import net.lightbody.bmp.mitm.KeyStoreFileCertificateSource;
import net.lightbody.bmp.mitm.RootCertificateGenerator;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class CertificateUtil {

    @Value("${test.ssl.certifficate.path:./cer}")
    private String certDirectoryPath;
    @Value("${test.ssl.certifficate.open.name:certificate.cer}")
    private String openCertName;
    @Value("${test.ssl.certificate.private.name:private-key.pem}")
    private String privateCertName;
    @Value("${test.ssl.certificate.store.name:keystore.p12}")
    private String storeName;
    @Value("${test.ssl.certificate.store.password:password}")
    private String storePassword;


    private RootCertificateGenerator generateSertificate(){
        // create a CA Root Certificate using default settings
        RootCertificateGenerator rootCertificateGenerator = RootCertificateGenerator.builder().build();
        File cerDirectory = new File(certDirectoryPath);
        if(!cerDirectory.exists()) cerDirectory.mkdirs();
        // save the newly-generated Root Certificate and Private Key -- the .cer file can be imported
        // directly into a browser
        rootCertificateGenerator.saveRootCertificateAsPemFile(new File(certDirectoryPath+"/"+openCertName));
        rootCertificateGenerator.savePrivateKeyAsPemFile(new File(certDirectoryPath+"/"+privateCertName), storePassword);

        // or save the certificate and private key as a PKCS12 keystore, for later use
        rootCertificateGenerator.saveRootCertificateAndKey("PKCS12", new File(certDirectoryPath+"/"+storeName),
                "privateKeyAlias", storePassword);


        return rootCertificateGenerator;
    };


    public ImpersonatingMitmManager getCertificate(){
        ImpersonatingMitmManager.Builder builder = ImpersonatingMitmManager.builder();
        File keyStoredCertificate = new File(certDirectoryPath + "/" + storeName);
        if(keyStoredCertificate.exists()) {
            CertificateAndKeySource existingCertificateSource =
                        new KeyStoreFileCertificateSource("PKCS12",
                                keyStoredCertificate,
                                "privateKeyAlias",
                                storePassword);
                builder.rootCertificateSource(existingCertificateSource);
        } else {
            RootCertificateGenerator rootCertificateGenerator = generateSertificate();
            // tell the ImpersonatingMitmManager  use the RootCertificateGenerator we just configured
            builder.rootCertificateSource(rootCertificateGenerator);
        }


        return builder/*.trustAllServers(true)*/.build();


    }



}
