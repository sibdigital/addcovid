package ru.sibdigital.addcovid.cms;

import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CertificateInfo {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private String issuer;
    private String subject;
    private String publicKeyAlgorithm;
    private LocalDate notBefore;
    private LocalDate notAfter;

    private X509Certificate certificate;
    private boolean isValid;
    private boolean exception = false;

    public CertificateInfo(X509Certificate certificate){
        try {
            this.certificate = certificate;
            issuer = certificate.getIssuerDN().toString();
            subject = certificate.getSubjectDN().toString();
            publicKeyAlgorithm = certificate.getPublicKey().getAlgorithm();
            notBefore = LocalDate.ofInstant(certificate.getNotBefore().toInstant(), ZoneOffset.UTC);
            notAfter = LocalDate.ofInstant(certificate.getNotAfter().toInstant(), ZoneOffset.UTC);
        }catch (Exception ex){
            exception = true;
        }
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

    public String toString(){
        String result = "Ошибка при преобразовании";
        try {
            String dateBefore = getNotBefore() != null ? getNotBefore().format(formatter) : "Неизвестно";
            String dateAfter = getNotAfter() != null ? getNotAfter().format(formatter) : "Неизвестно";
            result = "Владелец: " + getSubject() + "\n"
                    + "Издатетель: " + getIssuer() + "\n"
                    + "Действителен: " + dateBefore + " - " + dateAfter;
        }catch (Exception ex){
            exception = true;
        }
        return result;
    }

    public String toHtmlString(){
        String result = "Ошибка при преобразовании";
        try {
            String dateBefore = getNotBefore() != null ? getNotBefore().format(formatter) : "Неизвестно";
            String dateAfter = getNotAfter() != null ? getNotAfter().format(formatter) : "Неизвестно";
            result = "<b>Владелец:</b> <i>" + getSubject() + "</i><br/>"
                    + "<b>Издатетель:</b> <i>" + getIssuer() + "</i><br/>"
                    + "<b>Действителен:</b> <i>" + dateBefore + " - " + dateAfter + "</i>";
        }catch (Exception ex){
            exception = true;
        }
        return result;
    }
}
