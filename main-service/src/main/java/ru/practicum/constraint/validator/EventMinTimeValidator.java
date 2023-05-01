package ru.practicum.constraint.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventMinTimeValidator implements ConstraintValidator<MinAfterTwoHours, LocalDateTime>  {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.isAfter(LocalDateTime.now().plusHours(2)) ||
                value.isEqual(LocalDateTime.now().plusHours(2));
    }
}
