package at.msm.asobo.controllers;

import at.msm.asobo.exceptions.files.FileNotFoundException;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.files.FileAccessService;
import at.msm.asobo.services.files.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLConnection;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
public class FileController {
  private final FileStorageService fileStorageService;
  private final FileAccessService fileAccessService;

  public FileController(
      FileStorageService fileStorageService, FileAccessService fileAccessService) {
    this.fileStorageService = fileStorageService;
    this.fileAccessService = fileAccessService;
  }

  @GetMapping("/uploads/**")
  public ResponseEntity<Resource> serveFile(
      @AuthenticationPrincipal UserPrincipal userPrincipal, HttpServletRequest request) {

    String fullPath = this.extractFilePath(request);
    UUID userId = userPrincipal != null ? userPrincipal.getUserId() : null;

    if (!this.fileAccessService.canAccess(fullPath, userId)) {
      throw new UserNotAuthorizedException("User is not authorized to access file");
    }

    Resource file = fileStorageService.loadFileFromBucket(fullPath);
    String contentType = this.determineContentType(request, file);
    return buildFileResponse(file, contentType);
  }

  //  @PostMapping()
  //  @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
  //  @ResponseStatus(HttpStatus.NO_CONTENT)
  //  public void uploadFile(@RequestParam("file") MultipartFile file,
  //                         @AuthenticationPrincipal UserPrincipal loggedInUser) {
  //    this.fileStorageService.storeFileInBucket(file);
  //  }

  private String extractFilePath(HttpServletRequest request) {
    return request.getRequestURI().substring("/api/files".length());
  }

  private String determineContentType(HttpServletRequest request, Resource file) {
    String contentType = null;
    if (file.getFilename() != null) {
      contentType = URLConnection.guessContentTypeFromName(file.getFilename());
    }
    if (contentType == null) {
      contentType = "application/octet-stream";
    }
    return contentType;
  }

  private ResponseEntity<Resource> buildFileResponse(Resource file, String contentType) {
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
        .body(file);
  }
}
