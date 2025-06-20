package at.msm.asobo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.file-storage")
@Component
public class FileStorageProperties {
    private String basePath;
    private String profilePictureSubfolder;
    private String eventCoverPictureSubfolder;
    private String eventGalleriesSubfolder;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getProfilePictureSubfolder() {
        return profilePictureSubfolder;
    }

    public void setProfilePictureSubfolder(String profilePictureSubfolder) {
        this.profilePictureSubfolder = profilePictureSubfolder;
    }

    public String getEventCoverPictureSubfolder() {
        return eventCoverPictureSubfolder;
    }

    public void setEventCoverPictureSubfolder(String eventPictureSubfolder) {
        this.eventCoverPictureSubfolder = eventPictureSubfolder;
    }

    public String getEventGalleriesSubfolder() {
        return eventGalleriesSubfolder;
    }

    public void setEventGalleriesSubfolder(String eventGalleriesSubfolder) {
        this.eventGalleriesSubfolder = eventGalleriesSubfolder;
    }
}
