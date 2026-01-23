package net.korperka.antifraud.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CoordinatesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCoordinates {
    String message() default "Latitude and Longitude must be both present or both null";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}