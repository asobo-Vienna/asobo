package at.msm.asobo.services.files;

import at.msm.asobo.exceptions.files.InvalidFileUploadException;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileValidationService {

    private final MultipartProperties multipartProperties;

    public FileValidationService(MultipartProperties multipartProperties) {
        this.multipartProperties = multipartProperties;
    }

    public void validateImage(MultipartFile file) {
        validateFileSize(file);
        validateImageContentType(file);
    }

    private void validateFileSize(MultipartFile file) {
        long maxBytes = multipartProperties.getMaxFileSize().toBytes();
        if (file.getSize() > maxBytes) {
            throw new InvalidFileUploadException("File size must be less than " + multipartProperties.getMaxFileSize());
        }
    }

    private void validateImageContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileUploadException("Only image files are allowed");
        }
    }
}
