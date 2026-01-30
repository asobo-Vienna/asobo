package at.msm.asobo.dto.search;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventSearchResult {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime date;
    private String location;
    private String pictureURI;
    private String creatorName;
    private int participantCount;
    private boolean isPrivateEvent;
    private String type = "EVENT";

    public EventSearchResult() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public boolean isPrivateEvent() {
        return isPrivateEvent;
    }

    public void setPrivateEvent(boolean privateEvent) {
        isPrivateEvent = privateEvent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

