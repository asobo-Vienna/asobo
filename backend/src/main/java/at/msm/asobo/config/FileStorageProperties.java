package at.msm.asobo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.file-storage")
@Component
public class FileStorageProperties {
  // application properties: app.file-storage.base-path=uploads
  private String basePath;
  // application properties: app.file-storage.profile-picture-subfolder=profile-pictures
  private String profilePictureSubfolder;
  // application properties: app.file-storage.event-coverpicture-subfolder=event-cover-pictures
  private String eventCoverPictureSubfolder;
  // application properties: app.file-storage.event-galleries-subfolder=event-galleries
  private String eventGalleriesSubfolder;
  // application properties: app.file-storage.bucket-base-path
  private String bucketBasePath;

  public String getBasePath() {
    return this.basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getBucketBasePath() {
    return this.bucketBasePath;
  }

  public void setBucketBasePath(String bucketBasePath) {
    this.bucketBasePath = bucketBasePath;
  }

  public String getProfilePictureSubfolder() {
    return this.profilePictureSubfolder;
  }

  public void setProfilePictureSubfolder(String profilePictureSubfolder) {
    this.profilePictureSubfolder = profilePictureSubfolder;
  }

  public String getEventCoverPictureSubfolder() {
    return this.eventCoverPictureSubfolder;
  }

  public void setEventCoverPictureSubfolder(String eventPictureSubfolder) {
    this.eventCoverPictureSubfolder = eventPictureSubfolder;
  }

  public String getEventGalleriesSubfolder() {
    return this.eventGalleriesSubfolder;
  }

  public void setEventGalleriesSubfolder(String eventGalleriesSubfolder) {
    this.eventGalleriesSubfolder = eventGalleriesSubfolder;
  }
}
