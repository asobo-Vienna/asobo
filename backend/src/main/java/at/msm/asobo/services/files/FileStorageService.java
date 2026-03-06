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
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  private final FileStorageProperties fileStorageProperties;
  private final FileValidationService fileValidationService;
  private final String baseStoragePath;
  private final String bucketBasePath;

  @Value("${app.file-storage.bucket-secret-key}")
  private String bucketSecretKey;

  public FileStorageService(
      FileStorageProperties fileStorageProperties, FileValidationService fileValidationService)
      throws IOException {
    this.fileStorageProperties = fileStorageProperties;
    this.fileValidationService = fileValidationService;
    this.baseStoragePath = fileStorageProperties.getBasePath();
    this.bucketBasePath = fileStorageProperties.getBucketBasePath();
    createDirectories(Path.of(baseStoragePath));
  }

  public void deleteFileFromBucket(String filename) {
    if (filename == null) {
      throw new InvalidFilenameException("Filename must not be null");
    }

    String cleanFilename = this.cleanFilename(filename);
    String fileUrl = this.bucketBasePath + "/" + cleanFilename;

    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(fileUrl))
              .header("apikey", bucketSecretKey)
              .header("Authorization", "Bearer " + bucketSecretKey)
              .DELETE()
              .build();

      HttpResponse<String> response =
          HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

      if (!Set.of(200, 204).contains(response.statusCode())) {
        throw new FileDeletionException("Failed to delete file: " + response.body());
      }
    } catch (IOException | InterruptedException e) {
      throw new FileDeletionException("Failed to delete file: " + filename);
    }
  }

  public String storeFileInBucket(MultipartFile file, String subFolderName, String filePrefix) {
    String filename = this.generateAndSanitizeFilename(file, filePrefix);
    String destinationPath = this.bucketBasePath;

    if (subFolderName != null && !subFolderName.isBlank()) {
      destinationPath += "/" + subFolderName;
    }
    destinationPath += "/" + filename;

    // TODO: thumbnailator resize & compress images

    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(destinationPath))
              .header("Content-Type", file.getContentType())
              .header("apikey", this.bucketSecretKey)
              .header("Authorization", "Bearer " + this.bucketSecretKey)
              .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
              .build();

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (!Set.of(200, 201).contains(response.statusCode())) {
        throw new RuntimeException("Upload failed: " + response.body());
      }

    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("File upload failed", e);
    }

    String encodedFilename =
        URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

    return "/uploads/" + subFolderName + "/" + encodedFilename;
  }

  public Resource loadFileFromBucket(String filename) {
    String cleanFilename = this.cleanFilename(filename);

    String fileUrl = this.bucketBasePath + "/" + cleanFilename;

    try {
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(fileUrl)).GET().build();

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

      if (response.statusCode() == 200) {
        return this.bytesToResource(response.body(), cleanFilename);
      } else if (response.statusCode() == 404) {
        throw new FileNotFoundException("File not found in bucket: " + fileUrl);
      } else {
        throw new RuntimeException("Failed to fetch file: " + response.statusCode());
      }

    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("File retrieval failed", e);
    }
  }

  private void handlePictureUpdate(MultipartFile picture, PictureEntity entity, String subfolder) {
    if (picture == null || picture.isEmpty()) {
      return;
    }

    this.fileValidationService.validateImage(picture);

    String oldUri = entity.getPictureURI();

    if (oldUri != null) {
      try {
        this.deleteFileFromBucket(oldUri);
      } catch (Exception e) {
        System.out.printf("Failed to delete old picture with URI %s\n", oldUri);
      }
    }

    String newUri = this.storeFileInBucket(picture, subfolder, entity.getId().toString());
    entity.setPictureURI(newUri);
  }

  public void handleProfilePictureUpdate(MultipartFile picture, User user) {
    handlePictureUpdate(picture, user, this.fileStorageProperties.getProfilePictureSubfolder());
  }

  public void handleEventPictureUpdate(MultipartFile picture, Event event) {
    handlePictureUpdate(picture, event, this.fileStorageProperties.getEventCoverPictureSubfolder());
  }

  private String generateAndSanitizeFilename(MultipartFile file, String filePrefix) {
    String sanitizedFilename = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    String filename = filePrefix + "_" + sanitizedFilename;
    return filename;
  }

  private Resource bytesToResource(byte[] fileBytes, String path) {
    String filename = Paths.get(path).getFileName().toString();

    return new ByteArrayResource(fileBytes) {
      @Override
      public String getFilename() {
        return filename;
      }
    };
  }

  public void clearPicture(PictureEntity entity) {
    String uri = entity.getPictureURI();
    if (uri == null) {
      return;
    }
    try {
      this.deleteFileFromBucket(uri);
    } catch (Exception e) {
      System.out.printf("Failed to delete picture with URI %s\n", uri);
    }
    entity.setPictureURI(null);
  }

  private String cleanFilename(String filename) {
    if (filename.startsWith("/uploads/")) {
      return filename.substring("/uploads/".length());
    }
    if (filename.startsWith("/")) {
      return filename.substring(1);
    }
    return filename;
  }

  // METHODS FOR OFFLINE FILE STORAGE
  public Resource loadFileAsResource(String filename, UUID userId) {
    try {
      String cleanFilename = this.cleanFilename(filename);

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

  public String store(MultipartFile file, String subFolderName, String filePrefix) {
    String filename = this.generateAndSanitizeFilename(file, filePrefix);

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
}
