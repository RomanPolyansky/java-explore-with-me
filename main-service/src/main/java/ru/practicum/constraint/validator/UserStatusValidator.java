package ru.practicum.constraint.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserStatusValidator  implements ConstraintValidator<AllowedStatusForUsers, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equalsIgnoreCase("PENDING_EVENT") ||
                value.equalsIgnoreCase("CANCEL_REVIEW");
    }
}
