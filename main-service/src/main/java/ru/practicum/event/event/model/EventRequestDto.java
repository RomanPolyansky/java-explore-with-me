package ru.practicum.event.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.constraint.UpdateUser;
import ru.practicum.constraint.validator.AllowedStatusForAdmin;
import ru.practicum.constraint.validator.AllowedStatusForUsers;
import ru.practicum.constraint.validator.MinAfterOneHour;
import ru.practicum.constraint.validator.MinAfterTwoHours;
import ru.practicum.constraint.Create;
import ru.practicum.constraint.Update;
import ru.practicum.location.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class EventRequestDto {
    @NotBlank(groups = {Create.class}, message = "'title' should not be blank")
    @Max(value = 50, message = "'title' should not be more than 50 symbols")
    private String title;
    @NotBlank(groups = {Create.class}, message = "'annotation' should not be blank")
    @Max(value = 255, message = "'annotation' should not be more than 255 symbols")
    private String annotation;
    @NotBlank(groups = {Create.class}, message = "'description' should not be blank")
    private String description;
    @NotNull(groups = {Create.class}, message = "'participants_limit' should not be null")
    @Positive(groups = {Create.class}, message = "'participants_limit' should be more than 0")
    private Integer participantLimit;
    private Boolean requestModeration = true;
    @MinAfterTwoHours(groups = {Create.class, UpdateUser.class}, message = "'event_date' should be more in at least after 2 hours")
    @MinAfterOneHour(groups = {Update.class}, message = "'event_date' should be more in at least after 1 hours")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull(groups = {Create.class}, message = "'location' should not be null")
    private Location location;
    @NotNull(groups = {Create.class}, message = "'category' should not be null")
    private Long category;
    @NotNull(groups = {Create.class}, message = "'paid' should not be null")
    private Boolean paid;
    @AllowedStatusForUsers(groups = {UpdateUser.class}, message = "'stateAction' is not valid")
    @AllowedStatusForAdmin(groups = {Update.class}, message = "'stateAction' is not valid")
    private String stateAction;
}
