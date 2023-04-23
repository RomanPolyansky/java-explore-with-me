package ru.practicum.constraint;

import ru.practicum.constraint.validator.EventMinTimeValidatorForUpdate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = EventMinTimeValidatorForUpdate.class)
@Documented
public @interface MinAfterOneHour {

    String message() default "{EventMinTimeValidator.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
