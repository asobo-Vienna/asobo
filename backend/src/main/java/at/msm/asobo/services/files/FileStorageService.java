package at.msm.asobo.services.files;

import static java.nio.file.Files.createDirectories;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.files.FileDeletionException;
import at.msm.asobo.exceptions.files.FileNotFoundException;
import at.msm.asobo.exceptions.files.InvalidFilenameException;
import at.msm.asobo.interfaces.PictureEntity;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  private final FileStorageProperties fileStorageProperties;
  private final FileValidationService fileValidationService;
  private final String baseStoragePath;

  public FileStorageService(
      FileStorageProperties fileStorageProperties, FileValidationService fileValidationService)
      throws IOException {
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

      String encodedFilename =
          URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

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
      Files.deleteIfExists(deletionPath);
    } catch (IOException e) {
      throw new FileDeletionException("Failed to delete file: " + filename);
    }
  }

  public Resource loadFileAsResource(String filename, UUID userId) {
    try {
      // Remove leading slash or /uploads/ prefix
      String cleanFilename = filename;
      if (cleanFilename.startsWith("/uploads/")) {
        cleanFilename = cleanFilename.substring("/uploads/".length());
      } else if (cleanFilename.startsWith("/")) {
        cleanFilename = cleanFilename.substring(1);
      }

      Path filePath = Paths.get(this.baseStoragePath).resolve(cleanFilename).normalize();
      Resource resource = new UrlResource(filePath.toUri());

      if (resource.exists() && resource.isReadable()) {
        return resource;
      } else {
        throw new FileNotFoundException("File not found: " + filename);
      }
    } catch (MalformedURLException e) {
      throw new FileNotFoundException("File not found: " + filename);
    }
  }

  private void handlePictureUpdate(MultipartFile picture, PictureEntity entity, String subfolder) {
    if (picture == null || picture.isEmpty()) {
      return;
    }

    this.fileValidationService.validateImage(picture);

    String oldUri = entity.getPictureURI();
    String newUri = this.store(picture, subfolder);

    entity.setPictureURI(newUri);

    if (oldUri != null) {
      try {
        this.delete(oldUri);
      } catch (Exception e) {
        System.out.printf("Failed to delete old picture with URI %s\n", oldUri);
      }
    }
  }

  public void handleProfilePictureUpdate(MultipartFile picture, User user) {
    handlePictureUpdate(picture, user, this.fileStorageProperties.getProfilePictureSubfolder());
  }

  public void handleEventPictureUpdate(MultipartFile picture, Event event) {
    handlePictureUpdate(picture, event, this.fileStorageProperties.getEventCoverPictureSubfolder());
  }
}
