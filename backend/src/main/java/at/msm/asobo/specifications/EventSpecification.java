package at.msm.asobo.specifications;

import at.msm.asobo.dto.filter.EventFilterDTO;
import at.msm.asobo.entities.Event;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecification {
  public static Specification<Event> withFilters(EventFilterDTO filterDTO) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (filterDTO.getLocation() != null && !filterDTO.getLocation().isBlank()) {
        predicates.add(cb.equal(root.get("location"), filterDTO.getLocation()));
      }
      if (filterDTO.getCreatorId() != null) {
        predicates.add(cb.equal(root.get("creator").get("id"), filterDTO.getCreatorId()));
      }
      if (filterDTO.getEventAdminIds() != null && !filterDTO.getEventAdminIds().isEmpty()) {
        predicates.add(root.get("eventAdmins").get("id").in(filterDTO.getEventAdminIds()));
      }
      if (filterDTO.getParticipantIds() != null && !filterDTO.getParticipantIds().isEmpty()) {
        predicates.add(root.get("participants").get("id").in(filterDTO.getParticipantIds()));
      }
      if (filterDTO.getDate() != null) {
        LocalDateTime startOfDay = filterDTO.getDate().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startOfDay));
        predicates.add(cb.lessThan(root.get("date"), endOfDay));
      }
      if (filterDTO.getDateFrom() != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("date"), filterDTO.getDateFrom()));
      }
      if (filterDTO.getDateTo() != null) {
        LocalDateTime endOfDay = filterDTO.getDateTo().toLocalDate().atTime(23, 59, 59);
        predicates.add(cb.lessThanOrEqualTo(root.get("date"), endOfDay));
      }
      if (filterDTO.getIsPrivateEvent() != null) {
        predicates.add(cb.equal(root.get("isPrivateEvent"), filterDTO.getIsPrivateEvent()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
