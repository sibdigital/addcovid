package ru.sibdigital.addcovid.cms;

import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class CertificateInfo {

    private String issuer;
    private String subject;
    private String publicKeyAlgorithm;
    private LocalDate notBefore;
    private LocalDate notAfter;

    private X509Certificate certificate;
    private boolean isValid;

    public CertificateInfo(X509Certificate certificate){
        this.certificate = certificate;
        issuer = certificate.getIssuerDN().toString();
        subject = certificate.getSubjectDN().toString();
        publicKeyAlgorithm = certificate.getPublicKey().getAlgorithm();
        notBefore = LocalDate.ofInstant(certificate.getNotBefore().toInstant(), ZoneOffset.UTC);
        notAfter = LocalDate.ofInstant(certificate.getNotAfter().toInstant(), ZoneOffset.UTC);
    }

    public CertificateInfo(X509Certificate certificate, boolean isValid){
        this(certificate);
        this.isValid = isValid;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getSubject() {
        return subject;
    }

    public String getPublicKeyAlgorithm() {
        return publicKeyAlgorithm;
    }

    public LocalDate getNotBefore() {
        return notBefore;
    }

    public LocalDate getNotAfter() {
        return notAfter;
    }
}
