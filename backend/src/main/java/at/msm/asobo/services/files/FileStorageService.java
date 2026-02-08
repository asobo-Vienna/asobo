package at.msm.asobo.services.files;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.files.FileDeletionException;
import at.msm.asobo.exceptions.files.InvalidFilenameException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;
import static java.nio.file.Files.*;

@Service
public class FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    private final FileValidationService fileValidationService;

    private final String baseStoragePath;

    public FileStorageService(FileStorageProperties fileStorageProperties,
                              FileValidationService fileValidationService) throws IOException {
        this.fileStorageProperties = fileStorageProperties;
        this.fileValidationService = fileValidationService;
        this.baseStoragePath = fileStorageProperties.getBasePath();
        createDirectories(Path.of(baseStoragePath));
    }

    public String store(MultipartFile file) {
        return store(file, "misc");
    }

    public String store(MultipartFile file, String subFolderName) {
        String sanitizedFilename = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String filename = UUID.randomUUID() + "_" + sanitizedFilename;

        String destinationPath = this.baseStoragePath;
        if (subFolderName != null && !subFolderName.isBlank()) {
            destinationPath = destinationPath + "/" + subFolderName;
            try {
                Files.createDirectories(Path.of(destinationPath)); // Ensure subdirectory exists
            } catch (IOException e) {
                throw new RuntimeException("Could not create subfolder: " + subFolderName, e);
            }
        }

        Path targetDir = Paths.get(destinationPath).toAbsolutePath();
        Path targetFile = targetDir.resolve(filename);
        System.out.println("Storing file " + filename + " to " + targetFile);
        System.out.println("basePath: " + baseStoragePath);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);

            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

            return "/uploads/" + subFolderName + "/" + encodedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    public void delete(String filename) {
        if (filename == null) {
            throw new InvalidFilenameException("Filename must not be null");
        }
        // get current directory
        Path targetDir = Paths.get(".").toAbsolutePath().normalize();
        // get rid of "/" at the beginning
        Path deletionPath = targetDir.resolve(filename.substring(1));

        try {
            Files.delete(deletionPath);
        } catch (IOException e) {
            throw new FileDeletionException("Failed to delete file: " + filename);
        }
    }

    public void handleProfilePictureUpdate(MultipartFile picture, User user) {
        if (picture == null || picture.isEmpty()) {
            return;
        }

        this.fileValidationService.validateImage(picture);

        if (user.getPictureURI() != null) {
            this.delete(user.getPictureURI());
        }

        String pictureURI = this.store(picture, this.fileStorageProperties.getProfilePictureSubfolder());
        user.setPictureURI(pictureURI);
    }
}
