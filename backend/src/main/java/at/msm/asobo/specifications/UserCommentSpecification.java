package at.msm.asobo.specifications;

import at.msm.asobo.dto.filter.UserCommentFilterDTO;
import at.msm.asobo.entities.UserComment;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class UserCommentSpecification {

  public static Specification<UserComment> withFilters(UserCommentFilterDTO filterDTO) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (filterDTO.getAuthorId() != null) {
        predicates.add(cb.equal(root.get("author").get("id"), filterDTO.getAuthorId()));
      }
      if (filterDTO.getEventId() != null) {
        predicates.add(cb.equal(root.get("event").get("id"), filterDTO.getEventId()));
      }
      if (filterDTO.getDateFrom() != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("date"), filterDTO.getDateFrom()));
      }
      if (filterDTO.getDateTo() != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("date"), filterDTO.getDateTo()));
      }

      // Add JOIN FETCH for eager loading
      assert query != null;
      query.distinct(true);
      root.fetch("author", JoinType.INNER);
      root.fetch("event", JoinType.INNER);

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
