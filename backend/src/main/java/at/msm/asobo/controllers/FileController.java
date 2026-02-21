package at.msm.asobo.controllers;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.exceptions.files.FileNotFoundException;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.files.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
public class FileController {
  private final FileStorageService fileStorageService;
  private final FileStorageProperties fileStorageProperties;

  public FileController(
      FileStorageService fileStorageService, FileStorageProperties fileStorageProperties) {
    this.fileStorageService = fileStorageService;
    this.fileStorageProperties = fileStorageProperties;
  }

  @GetMapping("/uploads/**")
  public ResponseEntity<Resource> serveFile(
      @AuthenticationPrincipal UserPrincipal userPrincipal, HttpServletRequest request) {

    // Extract the path after /api/files/uploads/
    String fullPath = request.getRequestURI().substring("/api/files/uploads/".length());
    System.out.println(">>> Trying to serve file: " + fullPath);

    try {
      Resource file = fileStorageService.loadFileAsResource(fullPath);

      String contentType =
          request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
      if (contentType == null) {
        contentType = "application/octet-stream";
      }

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(
              HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
          .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
          .body(file);

    } catch (FileNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
