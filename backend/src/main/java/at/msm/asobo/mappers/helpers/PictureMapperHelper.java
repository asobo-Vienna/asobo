package at.msm.asobo.mappers.helpers;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.services.files.FileStorageService;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class PictureMapperHelper {
    @Autowired
    private FileStorageService fileStorageService;

    private final FileStorageProperties fileStorageProperties;

    public PictureMapperHelper(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @Named("mapUserPicture")
    public String mapUserPicture(MultipartFile picture) {
        if (picture == null || picture.isEmpty()) {
            return null;
        }
        return fileStorageService.store(picture, this.fileStorageProperties.getProfilePictureSubfolder());
    }

    @Named("mapEventPicture")
    public String mapEventPicture(MultipartFile picture) {
        if (picture == null || picture.isEmpty()) {
            return null;
        }
        return fileStorageService.store(picture, this.fileStorageProperties.getEventCoverPictureSubfolder());
    }

    @Named("mapGalleryPicture")
    public String mapGalleryPicture(MultipartFile picture) {
        if (picture == null || picture.isEmpty()) {
            return null;
        }
        return fileStorageService.store(picture, this.fileStorageProperties.getEventGalleriesSubfolder());
    }

    /*@Named("stringToUri")
    public URI stringToUri(String value) {
        return value == null ? null : URI.create(value);
    }

    @Named("uriToString")
    public String uriToString(URI uri) {
        return uri == null ? null : uri.toString();
    }*/
}
