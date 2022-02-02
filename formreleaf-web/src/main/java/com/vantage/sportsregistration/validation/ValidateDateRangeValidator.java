package com.vantage.sportsregistration.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/13/15.
 */
public class ValidateDateRangeValidator implements ConstraintValidator<ValidateDateRange, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateDateRangeValidator.class);

    private String startDateFieldName;
    private String endDateFieldName;

    @Override
    public void initialize(ValidateDateRange validateDateRange) {
        startDateFieldName = validateDateRange.start();
        endDateFieldName = validateDateRange.end();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        try {

            Date starDate = (Date) ValidatorUtil.getFieldValue(value, startDateFieldName);
            Date endDate = (Date) ValidatorUtil.getFieldValue(value, endDateFieldName);

            if (starDate != null && endDate != null && endDate.before(starDate)) {
                ValidatorUtil.addValidationError(startDateFieldName, context);
                ValidatorUtil.addValidationError(endDateFieldName, context);

                return false;
            }
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            LOGGER.error("Unable to determine start date or endDate", e);

            return false;
        }

        return true;
    }
}
