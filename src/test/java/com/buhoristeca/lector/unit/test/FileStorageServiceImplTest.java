package com.buhoristeca.lector.unit.test;

import com.buhoristeca.lector.config.FileStorageProperties;
import com.buhoristeca.lector.exceptions.FileStorageException;
import com.buhoristeca.lector.exceptions.ResourceNotFoundException;
import com.buhoristeca.lector.service.impl.FileStorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceImplTest {

    @Mock
    private FileStorageProperties fileStorageProperties;

    @TempDir // JUnit 5 crea un directorio temporal para cada test y lo limpia después
    Path tempDir;

    private FileStorageServiceImpl fileStorageService;

    @BeforeEach
    void setUp() {
        // Configurar el mock para que devuelva el path del directorio temporal
        when(fileStorageProperties.getUploadDir()).thenReturn(tempDir.toString());
        // Crear la instancia del servicio DESPUÉS de mockear las propiedades
        fileStorageService = new FileStorageServiceImpl(fileStorageProperties);
        fileStorageService.init();
    }

    @Test
    @DisplayName("Store File Exitosamente")
    void storeFile_archivoValido_deberiaGuardarArchivoYRetornarNombre() throws IOException {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "test data".getBytes());
        String preferredPrefix = "libro_1";

        // Act
        String storedFileName = fileStorageService.storeFile(mockFile, preferredPrefix);

        // Assert
        assertNotNull(storedFileName);
        assertTrue(storedFileName.startsWith(preferredPrefix + "_"));
        assertTrue(storedFileName.endsWith(".pdf"));
        assertTrue(Files.exists(tempDir.resolve(storedFileName)));
    }

    @Test
    @DisplayName("Store File Falla si Archivo Vacío")
    void storeFile_archivoVacio_deberiaLanzarFileStorageException() {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]);

        // Act & Assert
        assertThrows(FileStorageException.class, () -> {
            fileStorageService.storeFile(mockFile, "test");
        });
    }

    @Test
    @DisplayName("Store File Falla si Extensión No es PDF")
    void storeFile_extensionInvalida_deberiaLanzarFileStorageException() {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test data".getBytes());

        // Act & Assert
        FileStorageException exception = assertThrows(FileStorageException.class, () -> {
            fileStorageService.storeFile(mockFile, "test");
        });
        assertTrue(exception.getMessage().contains("Formato de archivo no permitido"));
    }


    @Test
    @DisplayName("Load As Resource Exitosamente")
    void loadAsResource_archivoExistente_deberiaRetornarResource() throws MalformedURLException {
        // Arrange
        String fileName = "testfile.pdf";
        Path filePath = tempDir.resolve(fileName);
        try {
            Files.write(filePath, "dummy content".getBytes()); // Crear archivo de prueba
        } catch (IOException e) {
            fail("No se pudo crear el archivo de prueba: " + e.getMessage());
        }

        // Act
        Resource resource = fileStorageService.loadAsResource(fileName);

        // Assert
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
        assertEquals(fileName, resource.getFilename());
    }

    @Test
    @DisplayName("Load As Resource Falla si Archivo No Existe")
    void loadAsResource_archivoNoExistente_deberiaLanzarResourceNotFoundException() {
        // Arrange
        String fileName = "nonexistent.pdf";

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            fileStorageService.loadAsResource(fileName);
        });
    }

    @Test
    @DisplayName("Delete File Exitosamente")
    void deleteFile_archivoExistente_deberiaEliminarArchivo() throws IOException {
        // Arrange
        String fileName = "to_delete.pdf";
        Path filePath = tempDir.resolve(fileName);
        Files.createFile(filePath); // Crear un archivo para eliminar
        assertTrue(Files.exists(filePath));

        // Act
        fileStorageService.deleteFile(fileName);

        // Assert
        assertFalse(Files.exists(filePath));
    }

    @Test
    @DisplayName("Delete File No Lanza Excepción si Archivo No Existe")
    void deleteFile_archivoNoExistente_noDeberiaLanzarExcepcion() {
        // Arrange
        String fileName = "non_existent_to_delete.pdf";

        // Act & Assert
        assertDoesNotThrow(() -> fileStorageService.deleteFile(fileName));
    }
}