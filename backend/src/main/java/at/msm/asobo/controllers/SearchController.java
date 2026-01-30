package at.msm.asobo.controllers;

import at.msm.asobo.dto.search.GlobalSearchRequest;
import at.msm.asobo.dto.search.GlobalSearchResponse;
import at.msm.asobo.services.GlobalSearchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final GlobalSearchService searchService;

    public SearchController(GlobalSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public GlobalSearchResponse globalSearch(
            @RequestParam(required = false) String q,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "false") Boolean includePrivate,
            Authentication authentication) {

        GlobalSearchRequest request = new GlobalSearchRequest();
        request.setQuery(q);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setLocation(location);

        // Only authenticated users can search private events
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        request.setIncludePrivateEvents(includePrivate && isAuthenticated);

        return searchService.search(request);
    }
}