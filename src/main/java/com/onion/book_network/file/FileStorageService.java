package com.onion.book_network.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${application.file.uploads.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull String userId
    ) {
        final String fileUploadSubPath = "users" + File.separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull String fileUploadSubPath
    ) {
        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);

        if (!targetFolder.exists() && !targetFolder.mkdirs()) {
            log.warn("Failed to create target folder: {}", targetFolder.getAbsolutePath());
            return null;
        }

        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFileName = System.currentTimeMillis() + (fileExtension.isEmpty() ? "" : "." + fileExtension);
        Path targetPath = Paths.get(finalUploadPath, targetFileName);

        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved to: {}", targetPath.toAbsolutePath());
            return targetPath.toString();
        } catch (IOException e) {
            log.error("Failed to save file to {}", targetPath.toAbsolutePath(), e);
            return null;
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "";
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
