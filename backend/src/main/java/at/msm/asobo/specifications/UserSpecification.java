package at.msm.asobo.specifications;

import at.msm.asobo.dto.filter.UserFilterDTO;
import at.msm.asobo.entities.User;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
  public static Specification<User> withFilters(UserFilterDTO filterDTO) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (filterDTO.getFirstName() != null) {
        predicates.add(cb.equal(root.get("firstName"), filterDTO.getFirstName()));
      }
      if (filterDTO.getSurname() != null) {
        predicates.add(cb.equal(root.get("surname"), filterDTO.getSurname()));
      }
      if (filterDTO.getLocation() != null && !filterDTO.getLocation().isBlank()) {
        predicates.add(cb.equal(root.get("location"), filterDTO.getLocation()));
      }
      if (filterDTO.getCountry() != null && !filterDTO.getCountry().isBlank()) {
        predicates.add(cb.equal(root.get("country"), filterDTO.getCountry()));
      }
      if (filterDTO.getRoleIds() != null && !filterDTO.getRoleIds().isEmpty()) {
        predicates.add(root.get("roles").get("id").in(filterDTO.getRoleIds()));
      }
      if (filterDTO.getIsActive() != null) {
        predicates.add(cb.equal(root.get("isActive"), filterDTO.getIsActive()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
