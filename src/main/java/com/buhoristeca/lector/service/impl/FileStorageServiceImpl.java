package com.buhoristeca.lector.service.impl;

import com.buhoristeca.lector.config.FileStorageProperties;
import com.buhoristeca.lector.exceptions.FileStorageException;
import com.buhoristeca.lector.exceptions.ResourceNotFoundException;
import com.buhoristeca.lector.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("No se pudo crear el directorio donde se almacenarán los archivos subidos.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String preferredFileNamePrefix) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);

        if (file.isEmpty()) {
            throw new FileStorageException("Error al almacenar archivo vacío " + originalFileName);
        }
        if (originalFileName.contains("..")) {
            throw new FileStorageException("Nombre de archivo inválido: " + originalFileName);
        }
        if (!".pdf".equalsIgnoreCase(fileExtension)) {
            throw new FileStorageException("Formato de archivo no permitido. Solo se aceptan PDFs. Archivo: " + originalFileName);
        }

        String storedFileName = (preferredFileNamePrefix != null ? preferredFileNamePrefix : "file") +
                "_" + UUID.randomUUID().toString() + fileExtension;

        try (InputStream inputStream = file.getInputStream()) {
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return storedFileName;
        } catch (IOException ex) {
            throw new FileStorageException("No se pudo almacenar el archivo " + originalFileName + ". Por favor, inténtelo de nuevo.", ex);
        }
    }

    @Override
    public String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @Override
    public Path load(String filename) {
        return this.fileStorageLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path filePath = load(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("No se pudo leer el archivo: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("No se pudo leer el archivo: " + filename + ", due to exception: " + ex.getMessage());
        }
    }

    @Override
    public void deleteFile(String filename) {
        if (filename == null || filename.isBlank()) return;
        try {
            Path filePath = load(filename).normalize();
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException ex) {
            System.err.println("No se pudo eliminar el archivo: " + filename + " - " + ex.getMessage());
        }
    }
}
