package com.onion.book_network.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

    public static byte[] readFileFromLocation(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            log.warn("File path is blank or null");
            return null;
        }
        try {
            Path filePath = Path.of(fileUrl);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.warn("No file found at path: {}", fileUrl);
        }
        return null;
    }
}
