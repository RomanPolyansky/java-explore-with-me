package ru.practicum.category.model;

import lombok.Data;
import ru.practicum.constraint.Create;
import ru.practicum.constraint.Update;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDto {

    private Long id;
    @NotBlank(groups = {Create.class, Update.class}, message = "'name' should not be blank")
    private String name;
}
