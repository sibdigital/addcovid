package ru.sibdigital.addcovid.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FileUtils {


    public static List<File> getAllInDirectory(String directory){
        List<File> files = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            files = paths.filter(Files::isRegularFile)
                    .map(p -> p.toFile())
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        return files;
    }
}
