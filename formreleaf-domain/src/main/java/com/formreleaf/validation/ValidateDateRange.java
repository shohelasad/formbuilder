package com.formreleaf.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/13/15.
 */

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {ValidateDateRangeValidator.class})
public @interface ValidateDateRange {
    String message() default "End Date appears before Start Date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String start();

    String end();

}
