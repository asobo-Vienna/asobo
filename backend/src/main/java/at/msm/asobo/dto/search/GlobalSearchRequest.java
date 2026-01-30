package at.msm.asobo.dto.search;

import java.time.LocalDateTime;

public class GlobalSearchRequest {
    private String query;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Boolean includePrivateEvents = false;

    public GlobalSearchRequest() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getIncludePrivateEvents() {
        return includePrivateEvents;
    }

    public void setIncludePrivateEvents(Boolean includePrivateEvents) {
        this.includePrivateEvents = includePrivateEvents;
    }
}
