package ru.practicum.constraint.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventMinTimeValidatorForUpdate implements ConstraintValidator<MinAfterOneHour, LocalDateTime>  {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        return value.isAfter(LocalDateTime.now().plusHours(1)) ||
                value.isEqual(LocalDateTime.now().plusHours(1));
    }
}