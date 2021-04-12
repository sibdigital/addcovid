package ru.sibdigital.addcovid.service.file;

import java.io.File;

public interface FileService {

    String getFileHash(File file);
    String getFileExtension(String name);
}
