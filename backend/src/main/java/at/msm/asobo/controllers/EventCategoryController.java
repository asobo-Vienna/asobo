package at.msm.asobo.controllers;

import at.msm.asobo.entities.EventCategory;
import at.msm.asobo.services.events.EventCategoryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-categories")
public class EventCategoryController {

  private final EventCategoryService eventCategoryService;

  public EventCategoryController(EventCategoryService eventCategoryService) {
    this.eventCategoryService = eventCategoryService;
  }

  @GetMapping
  public List<EventCategory> getAllEventCategories() {
    return this.eventCategoryService.getAllEventCategories();
  }
}
