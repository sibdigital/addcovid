package ru.sibdigital.addcovid.cms;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
}
