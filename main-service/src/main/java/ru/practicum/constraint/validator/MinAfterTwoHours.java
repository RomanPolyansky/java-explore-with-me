package ru.practicum.constraint.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = EventMinTimeValidator.class)
@Documented
public @interface MinAfterTwoHours {

    String message() default "{EventMinTimeValidator.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
