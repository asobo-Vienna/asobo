package at.msm.asobo.specifications;

import at.msm.asobo.dto.filter.UserFilterDTO;
import at.msm.asobo.entities.User;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
  private static final Map<String, List<String>> COUNTRY_ALIASES =
      Map.of("GB", List.of("great britain", "britain", "england", "uk"));

  private static Set<String> resolveCountryCodes(String queryLower) {
    Set<String> codes =
        Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2).stream()
            .filter(
                code ->
                    Locale.of("", code)
                        .getDisplayCountry(Locale.ENGLISH)
                        .toLowerCase()
                        .contains(queryLower))
            .collect(Collectors.toCollection(HashSet::new));

    COUNTRY_ALIASES.forEach(
        (code, aliases) -> {
          if (aliases.stream().anyMatch(alias -> alias.contains(queryLower))) {
            codes.add(code);
          }
        });

    return codes;
  }

  public static Specification<User> withFilters(UserFilterDTO filterDTO) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (filterDTO.getQuery() != null && !filterDTO.getQuery().isBlank()) {
        String queryLower = filterDTO.getQuery().toLowerCase();
        String queryPattern = "%" + queryLower + "%";

        Set<String> matchingCountryCodes = resolveCountryCodes(queryLower);

        List<Predicate> orPredicates = new ArrayList<>();
        orPredicates.add(cb.like(cb.lower(root.get("username")), queryPattern));
        orPredicates.add(cb.like(cb.lower(root.get("firstName")), queryPattern));
        orPredicates.add(cb.like(cb.lower(root.get("surname")), queryPattern));
        orPredicates.add(
            cb.like(
                cb.lower(cb.concat(cb.concat(root.get("firstName"), " "), root.get("surname"))),
                queryPattern));
        orPredicates.add(cb.like(cb.lower(root.get("email")), queryPattern));
        orPredicates.add(cb.like(cb.lower(root.get("location")), queryPattern));
        orPredicates.add(cb.like(cb.lower(root.get("country")), queryPattern));
        if (!matchingCountryCodes.isEmpty()) {
          orPredicates.add(root.get("country").in(matchingCountryCodes));
        }
        predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
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
