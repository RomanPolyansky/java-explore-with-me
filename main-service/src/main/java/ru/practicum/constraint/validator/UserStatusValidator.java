package ru.practicum.constraint.validator;

import ru.practicum.event.event.model.StateAction;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserStatusValidator  implements ConstraintValidator<AllowedStatusForUsers, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equalsIgnoreCase(StateAction.PENDING_EVENT.name()) ||
                value.equalsIgnoreCase(StateAction.CANCEL_REVIEW.name()) ||
                value.equalsIgnoreCase(StateAction.SEND_TO_REVIEW.name());
    }
}
