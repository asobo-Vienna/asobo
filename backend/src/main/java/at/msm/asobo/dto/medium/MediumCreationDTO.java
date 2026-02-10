package at.msm.asobo.dto.medium;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public class MediumCreationDTO {

  private UUID eventId;

  @NotNull(message = "URI is mandatory for creating a new medium")
  private MultipartFile mediumFile;

  public MediumCreationDTO() {}

  public UUID getEventId() {
    return eventId;
  }

  public void setEventId(UUID eventId) {
    this.eventId = eventId;
  }

  public void setMediumFile(MultipartFile mediumURI) {
    this.mediumFile = mediumURI;
  }

  public MultipartFile getMediumFile() {
    return this.mediumFile;
  }
}
