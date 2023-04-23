package ru.practicum.compilation.model;

import lombok.Data;
import ru.practicum.constraint.Create;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CompilationDto {

    private Long id;
    @NotBlank(groups = {Create.class}, message = "'name' should not be blank")
    private String name;
    @NotBlank(groups = {Create.class}, message = "'description' should not be blank")
    private String description;

    private List<Long> events;
}