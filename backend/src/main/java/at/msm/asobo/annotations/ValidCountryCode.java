package at.msm.asobo.annotations;

import at.msm.asobo.validators.CountryCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CountryCodeValidator.class)
public @interface ValidCountryCode {
  String message() default "Invalid country code";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
