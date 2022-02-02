package com.formreleaf.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/19/15.
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
public @interface ValidateDateRangeList {
    ValidateDateRange[] value();
}

