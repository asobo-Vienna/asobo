package at.msm.asobo.dto.search;

import java.util.List;

public class GlobalSearchResponse {
    private List<EventSearchResult> events;
    private List<UserSearchResult> users;
    private int totalResults;

    public GlobalSearchResponse() {
    }

    public List<EventSearchResult> getEvents() {
        return events;
    }

    public void setEvents(List<EventSearchResult> events) {
        this.events = events;
    }

    public List<UserSearchResult> getUsers() {
        return users;
    }

    public void setUsers(List<UserSearchResult> users) {
        this.users = users;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
