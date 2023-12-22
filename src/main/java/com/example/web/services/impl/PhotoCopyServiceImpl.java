package com.example.web.services.impl;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PhotoCopyServiceImpl {
    private static final String SOURCE_FOLDER = "src\\main\\resources\\static\\img\\userImage";
    private static final String TARGET_FOLDER = "target\\classes\\static\\img\\userImage";

    public void copyPhotoToTargetFolder(String fileName) throws IOException {
        Path sourcePath = Paths.get(SOURCE_FOLDER, fileName);
        Path targetPath = Paths.get(TARGET_FOLDER, fileName);
        Files.createDirectories(targetPath.getParent()); // Create parent directories if they don't exist

        if (Files.exists(targetPath)) {
            Files.delete(targetPath);
        }

        Files.copy(sourcePath, targetPath);
    }
}
