package com.buhoristeca.lector.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface FileStorageService {
    void init();
    String storeFile(MultipartFile file, String preferredFileName);
    Path load(String filename);
    Resource loadAsResource(String filename);
    void deleteFile(String filename);
    String getFileExtension(String fileName);
}