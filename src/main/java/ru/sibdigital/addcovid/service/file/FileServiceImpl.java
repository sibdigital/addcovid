package ru.sibdigital.addcovid.service.file;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.utils.FileUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Log4j2
@Service
public class FileServiceImpl implements FileService{

    @Override
    public String getFileHash(File file){
        return FileUtils.getFileHash(file);
//        String result = "NOT";
//        try {
//            final byte[] bytes = Files.readAllBytes(file.toPath());
//            byte[] hash = MessageDigest.getInstance("MD5").digest(bytes);
//            result = DatatypeConverter.printHexBinary(hash);
//        } catch (IOException ex) {
//            log.error(ex.getMessage());
//        } catch (NoSuchAlgorithmException ex) {
//            log.error(ex.getMessage());
//        }
//        return result;
    }

    @Override
    public String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

}
