package ru.sibdigital.addcovid.cms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class VerifiedData {

    private String signaturePath;
    private String dataPath;
    private String identificator;
    private String signatureIdentificator;
    private String group;

    private byte[] signature;
    private byte[] data;

    private boolean prepared = false;

    public VerifiedData(String signaturePath, String dataPath){
        this.dataPath = dataPath;
        this.signaturePath = signaturePath;
    }

    public VerifiedData(String signaturePath, String dataPath, String identificator, String signatureIdentificator){
        this(signaturePath, dataPath);
        this.identificator = identificator;
        this.signatureIdentificator = signatureIdentificator;
    }

    public VerifiedData(String signaturePath, String dataPath, long identificator, long signatureIdentificator){
        this(signaturePath, dataPath);
        this.identificator = String.valueOf(identificator);
        this.signatureIdentificator = String.valueOf(signatureIdentificator);
    }

    public VerifiedData(String signaturePath, String dataPath, long identificator, long signatureIdentificator, long group){
        this(signaturePath, dataPath, identificator, signatureIdentificator);
        this.group = String.valueOf(group);
    }

    public boolean prepare(){
        prepared = !isEmptyData() && !isEmptySignature();
        return prepared;
    }

    public boolean isEmptyData(){
        boolean exists = dataPath != null && Files.exists(Path.of(dataPath));
        boolean result = !exists && isEmptyFile(dataPath);
        return result;
    }

    public boolean isEmptySignature(){
        boolean exists = signaturePath != null && Files.exists(Path.of(signaturePath));
        boolean result = !exists && isEmptyFile(signaturePath);
        return result;
    }

    private boolean isEmptyFile(String path){
        boolean result = true;
        try {
            File file = new File(path);
            result = file.length() == 0L;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }

    public byte[] getSignature(boolean refresh){
        if (prepare() && (refresh || signature == null)) {
            try (FileInputStream fisSign = new FileInputStream(signaturePath)) {
                signature = fisSign.readAllBytes();
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return signature;
    }

    public byte[] getSignature(){
        return signature != null ? getSignature(false)
                : getSignature(true);
    }

    public FileInputStream getDataInputStream(){
        FileInputStream fos = null;
        if (prepare()) {
            try {
                fos = new FileInputStream(dataPath);
            }catch (FileNotFoundException ex) {
                log.error(ex.getMessage(), ex);
            }catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return fos;
    }

    public byte[] getData(){
        FileInputStream fos = getDataInputStream();
        byte[] data = null;
        if (fos != null) {
            try {
                data = fos.readAllBytes();
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return data;
    }

    public String toString(){
        return String.format("id: %s, sigid: %s gr: %s, data: %s, sign: %s",
                identificator, signatureIdentificator, group, data, signaturePath);
    }

    public String getIdentificator() {
        return identificator;
    }

    public void setIdentificator(String identificator) {
        this.identificator = identificator;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSignatureIdentificator() {
        return signatureIdentificator;
    }

    public void setSignatureIdentificator(String signatureIdentificator) {
        this.signatureIdentificator = signatureIdentificator;
    }
}
