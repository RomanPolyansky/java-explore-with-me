package ru.practicum.constraint.validator;

import ru.practicum.event.event.model.ParticipationStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParticipantStatusValidator implements ConstraintValidator<ParticipationReqStatus, String>  {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals(ParticipationStatus.CONFIRMED.name()) ||
                value.equals(ParticipationStatus.REJECTED.name());
    }
}