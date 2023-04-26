package ru.practicum.constraint.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AdminStatusValidator implements ConstraintValidator<AllowedStatusForAdmin, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !value.equalsIgnoreCase("CANCEL_REVIEW");
    }
}
