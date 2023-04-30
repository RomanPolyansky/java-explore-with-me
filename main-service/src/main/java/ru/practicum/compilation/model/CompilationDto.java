package ru.practicum.compilation.model;

import lombok.Data;
import ru.practicum.constraint.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CompilationDto {
    @NotBlank(groups = {Create.class}, message = "'title' should not be blank")
    private String title;
    @NotNull(groups = {Create.class}, message = "'events' should not be null")
    private List<Long> events;
    @NotNull(groups = {Create.class}, message = "'pinned' should not be null")
    private Boolean pinned;
}