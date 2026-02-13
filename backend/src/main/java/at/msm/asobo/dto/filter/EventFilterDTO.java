package at.msm.asobo.dto.filter;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventFilterDTO {
    private String location;
    private UUID creatorId;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private boolean isPrivateEvent;

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDateFrom() {
        return this.dateFrom;
    }

    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDateTime getDateTo() {
        return this.dateTo;
    }

    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }

    public UUID getCreatorId() {
        return this.creatorId;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public boolean getIsPrivateEvent() {
        return this.isPrivateEvent;
    }

    public void setIsPrivateEvent(boolean isPrivateEvent) {
        this.isPrivateEvent = isPrivateEvent;
    }
}
