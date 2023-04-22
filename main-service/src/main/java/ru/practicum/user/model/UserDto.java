package ru.practicum.user.model;

import lombok.Data;
import ru.practicum.user.constraint.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
public class UserDto {
    @NotBlank(groups = {Create.class}, message = "'email' should not be blank")
    @Email(groups = {Create.class},message = "'email' should be email-like")
    private String email;
    @NotBlank(groups = {Create.class}, message = "'name' should not be blank")
    private String name;
    private Integer id;
}
