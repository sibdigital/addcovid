package ru.sibdigital.addcovid.cms;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class VerifiedData {

    private String signaturePath;
    private String dataPath;

    private byte[] signature;
    private byte[] data;

    private boolean prepared = false;

    public VerifiedData(String signaturePath, String dataPath){
        this.dataPath = dataPath;
        this.signaturePath = signaturePath;
    }

    public boolean prepare(){
        prepared = !isEmptyData() && !isEmptySignature();
        return prepared;
    }

    public boolean isEmptyData(){
        return !(dataPath != null && Files.exists(Path.of(dataPath)));
    }

    public boolean isEmptySignature(){
        return !(signaturePath != null && Files.exists(Path.of(signaturePath)));
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
}
