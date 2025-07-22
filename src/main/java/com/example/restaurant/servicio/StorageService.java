package com.example.restaurant.servicio;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    // La ruta a la carpeta 'static/img' donde se guardarán las imágenes.
    private final Path rootLocation = Paths.get("src/main/resources/static/img");

    public StorageService() {
        try {
            // Crea el directorio si no existe.
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la ubicación de almacenamiento de archivos", e);
        }
    }

    /**
     * Guarda un archivo subido.
     * @param file El archivo MultipartFile del formulario.
     * @return El nombre de archivo único generado.
     */
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            return null; // No se subió archivo, no hacemos nada.
        }

        try {
            // Generamos un nombre de archivo único para evitar colisiones.
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            Path destinationFile = this.rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return filename; // Devolvemos el nombre con el que se guardó.
        } catch (IOException e) {
            throw new RuntimeException("Falló al guardar el archivo.", e);
        }
    }
}