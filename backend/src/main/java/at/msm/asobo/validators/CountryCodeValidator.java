package at.msm.asobo.validators;

import at.msm.asobo.annotations.ValidCountryCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.Set;

public class CountryCodeValidator implements ConstraintValidator<ValidCountryCode, String> {
  private Set<String> isoCodes;

  @Override
  public void initialize(ValidCountryCode annotation) {
    isoCodes = Set.of(Locale.getISOCountries());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;
    return isoCodes.contains(value.toUpperCase());
  }
}
