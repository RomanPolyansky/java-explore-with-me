package ru.practicum.constraint.validator;

import ru.practicum.event.event.model.SortField;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SortEventsValidator implements ConstraintValidator<SortMethod, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        SortField.valueOf(value);
        return true;
    }
}