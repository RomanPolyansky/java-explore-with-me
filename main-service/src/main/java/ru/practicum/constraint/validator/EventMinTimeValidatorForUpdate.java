package ru.practicum.constraint.validator;

import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventMinTimeValidatorForUpdate implements ConstraintValidator<MinAfterOneHour, LocalDateTime>  {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (!(value.isAfter(LocalDateTime.now().plusHours(1)) ||
                value.isEqual(LocalDateTime.now().plusHours(1)))) {
            throw new DataIntegrityViolationException(context.getDefaultConstraintMessageTemplate());
        }
        return true;
    }
}