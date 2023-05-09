package ru.practicum.constraint.validator;

import ru.practicum.event.event.model.constants.StateAction;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserStatusValidator  implements ConstraintValidator<AllowedStatusForUsers, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.equalsIgnoreCase(StateAction.CANCEL_REVIEW.name()) ||
                value.equalsIgnoreCase(StateAction.SEND_TO_REVIEW.name());
    }
}
