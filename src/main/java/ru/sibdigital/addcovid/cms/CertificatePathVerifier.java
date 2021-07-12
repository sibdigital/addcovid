package ru.sibdigital.addcovid.cms;

import lombok.extern.slf4j.Slf4j;
import ru.CryptoPro.JCP.Util.JCPInit;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class CertificatePathVerifier {
    private Certificate anchor;
    private Certificate verifableCertificate;

    private boolean pathNotContainsRevocationCertificate = false;
    private boolean pathBuild = false;

    public CertificatePathVerifier(Certificate anchor, Certificate verifableCertificate){
        this.anchor = anchor;
        this.verifableCertificate = verifableCertificate;
    }

    public boolean verify() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CertPathBuilderException, CertPathValidatorException {

        boolean result = false;
        System.setProperty("com.sun.security.enableCRLDP", "true"); // для проверки по CRL DP
        System.setProperty("com.sun.security.enableAIAcaIssuers", "true"); // для загрузки сертификатов по AIA из сети
        System.setProperty("ru.CryptoPro.reprov.enableAIAcaIssuers", "true"); // для загрузки сертификатов по AIA из сети

        JCPInit.initProviders(false);

        final Certificate[] certs = new Certificate[2];
        certs[0] = verifableCertificate;//user;
        certs[1] = anchor;

        final Set<TrustAnchor> trust = new HashSet<TrustAnchor>(1);
        trust.add(new TrustAnchor((X509Certificate) anchor, null));

        final List cert = new ArrayList(0);
        for (int i = 0; i < certs.length; i++) {
            cert.add(certs[i]);
        }

        final PKIXBuilderParameters cpp = new PKIXBuilderParameters(trust, null);
        cpp.setSigProvider(null);

        final CollectionCertStoreParameters par = new CollectionCertStoreParameters(cert);

        final CertStore store = CertStore.getInstance("Collection", par);
        cpp.addCertStore(store);

        final X509CertSelector selector = new X509CertSelector();
        selector.setCertificate((X509Certificate) verifableCertificate);

        cpp.setTargetCertConstraints(selector);
        cpp.setRevocationEnabled(false);

        // Построение цепочки.
        final PKIXCertPathBuilderResult res = (PKIXCertPathBuilderResult) CertPathBuilder.
                getInstance("CPPKIX", "RevCheck").build(cpp);

        final CertPath cp = res.getCertPath();

        log.warn("%%% SIZE: " + cp.getCertificates().size());
        //System.out.println("%%% PATH:\n" + cp);
        log.warn("OK-1");
        pathBuild = true;

        // Проверка цепочки.

        final CertPathValidator cpv = CertPathValidator.getInstance("CPPKIX", "RevCheck");
        cpp.setRevocationEnabled(true);

        final CertPathValidatorResult validate = cpv.validate(cp, cpp);
        log.warn("OK-2");
        result = true;
        //System.out.println(validate);
        pathNotContainsRevocationCertificate = true;
        return result;
    }

    public boolean isPathNotContainsRevocationCertificate() {
        return pathNotContainsRevocationCertificate;
    }

    public boolean isPathBuild() {
        return pathBuild;
    }

    public static CertificatePathVerifier verifyAny(Certificate anchor, List<Certificate> verifableCertificate)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        CertificatePathVerifier result = null;
        for (Certificate cert : verifableCertificate){
            CertificatePathVerifier cpv = new CertificatePathVerifier(anchor, cert);

            boolean isVerify = false;
            try {
                isVerify = cpv.verify();
            } catch (CertPathBuilderException | CertPathValidatorException ex) {
                log.warn(ex.getMessage(),ex);
            }
            if (isVerify){
                result = cpv;
                break;
            }
        }
        return result;
    }

    public static boolean verifyAll(Certificate anchor, List<Certificate> verifableCertificate)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        boolean result = true;
        for (Certificate cert : verifableCertificate){
            CertificatePathVerifier cpv = new CertificatePathVerifier(anchor, cert);
            boolean isVerify = false;
            try {
                isVerify = cpv.verify();
            } catch (CertPathBuilderException | CertPathValidatorException ex) {
                log.warn(ex.getMessage(),ex);
            }
            result &= isVerify;
        }
        return result;
    }

}
