package com.buhoristeca.lector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;


/**
 * Clase para mapear las propiedades de configuración relacionadas con el almacenamiento de archivos.
 */
@Component
@ConfigurationProperties(prefix = "file") // El prefijo "file" se usará en application.properties
@Validated
public class FileStorageProperties {

    /**
     * Directorio donde se almacenarán los archivos subidos.
     * Puede ser una ruta absoluta o relativa al directorio de ejecución de la aplicación.
     */
    @NotBlank
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
