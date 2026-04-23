package at.msm.asobo.services.events;

import at.msm.asobo.entities.EventCategory;
import at.msm.asobo.repositories.EventCategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventCategoryService {

  private final EventCategoryRepository eventCategoryRepository;

  public EventCategoryService(EventCategoryRepository eventCategoryRepository) {
    this.eventCategoryRepository = eventCategoryRepository;
  }

  public List<EventCategory> getAllEventCategories() {
    return this.eventCategoryRepository.findAll();
  }
}
