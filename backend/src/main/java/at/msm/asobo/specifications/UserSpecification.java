package at.msm.asobo.specifications;

import at.msm.asobo.dto.filter.UserFilterDTO;
import at.msm.asobo.entities.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> withFilters(UserFilterDTO filterDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterDTO.getUsername() != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("username")),
                        "%" + filterDTO.getUsername().toLowerCase() + "%"
                ));
            }

            if (filterDTO.getEmail() != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("email")),
                        "%" + filterDTO.getEmail().toLowerCase() + "%"
                ));
            }

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
