package ru.sibdigital.addcovid.frontend;

import net.lightbody.bmp.mitm.CertificateAndKeySource;
import net.lightbody.bmp.mitm.KeyStoreFileCertificateSource;
import net.lightbody.bmp.mitm.RootCertificateGenerator;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CertificateUtil {

    @Value("${ssl.certifficate.path:./cer}")
    private String certDirectoryPath;
    @Value("${ssl.certifficate.open.name:certificate.cer}")
    private String openCertName;
    @Value("${ssl.certificate.private.name:private-key.pem}")
    private String privateCertName;
    @Value("${ssl.certificate.store.name:keystore.p12}")
    private String storeName;


    private RootCertificateGenerator generateSertificate(){
        // create a CA Root Certificate using default settings
        RootCertificateGenerator rootCertificateGenerator = RootCertificateGenerator.builder().build();
        File cerDirectory = new File(certDirectoryPath);
        if(!cerDirectory.exists()) cerDirectory.mkdirs();
        // save the newly-generated Root Certificate and Private Key -- the .cer file can be imported
        // directly into a browser
        rootCertificateGenerator.saveRootCertificateAsPemFile(new File(certDirectoryPath+"/"+openCertName));
        rootCertificateGenerator.savePrivateKeyAsPemFile(new File(certDirectoryPath+"/"+privateCertName), "password");

        // or save the certificate and private key as a PKCS12 keystore, for later use
        rootCertificateGenerator.saveRootCertificateAndKey("PKCS12", new File(certDirectoryPath+"/"+storeName),
                "privateKeyAlias", "password");


        return rootCertificateGenerator;
    };


    public ImpersonatingMitmManager getCertificate(){
        ImpersonatingMitmManager.Builder builder = ImpersonatingMitmManager.builder();
        File cerDirectory = new File(certDirectoryPath);
        if(cerDirectory.exists()) {
            CertificateAndKeySource existingCertificateSource =
                    new KeyStoreFileCertificateSource("PKCS12",
                            new File(certDirectoryPath+"/"+storeName),
                            "privateKeyAlias",
                            "password");
            builder.rootCertificateSource(existingCertificateSource)
            ;


        } else {
            RootCertificateGenerator rootCertificateGenerator = generateSertificate();
            builder.rootCertificateSource(rootCertificateGenerator);
        }


        return builder.trustAllServers(true).build();
        // tell the ImpersonatingMitmManager  use the RootCertificateGenerator we just configured

    }



}
