package ru.practicum.constraint.validator;

import ru.practicum.constraint.MinAfterTwoHours;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventMinTimeValidator implements ConstraintValidator<MinAfterTwoHours, LocalDateTime>  {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        return value.isAfter(LocalDateTime.now().plusHours(2)) ||
                value.isEqual(LocalDateTime.now().plusHours(2));
    }
}
