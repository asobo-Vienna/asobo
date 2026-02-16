package at.msm.asobo.specifications;

import at.msm.asobo.dto.filter.UserCommentFilterDTO;
import at.msm.asobo.entities.UserComment;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class UserCommentSpecification {

  public static Specification<UserComment> withFilters(UserCommentFilterDTO filterDTO) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      root.join("author", JoinType.LEFT);
      root.join("event", JoinType.LEFT);

      if (filterDTO.getAuthorId() != null) {
        predicates.add(cb.equal(root.get("author").get("id"), filterDTO.getAuthorId()));
      }

      if (filterDTO.getEventId() != null) {
        predicates.add(cb.equal(root.get("event").get("id"), filterDTO.getEventId()));
      }

      if (filterDTO.getDateFrom() != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filterDTO.getDateFrom()));
      }

      if (filterDTO.getDateTo() != null) {
        LocalDateTime endOfDay = filterDTO.getDateTo().toLocalDate().atTime(23, 59, 59);
        predicates.add(cb.lessThanOrEqualTo(root.get("date"), endOfDay));
      }

      assert query != null;
      query.distinct(true);
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
