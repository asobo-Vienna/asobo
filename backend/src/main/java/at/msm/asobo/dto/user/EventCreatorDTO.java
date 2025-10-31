package at.msm.asobo.dto.user;

import java.util.UUID;

public class EventCreatorDTO {

    private UUID id;
    private String username;
    private String pictureURI;
    private UUID eventId;

    public EventCreatorDTO() {}

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPictureURI() {
        return this.pictureURI;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }
}
