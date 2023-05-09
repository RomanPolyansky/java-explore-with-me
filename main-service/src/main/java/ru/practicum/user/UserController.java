package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.constraint.Create;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserDto;
import ru.practicum.user.model.UserMapper;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@RestControllerAdvice
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/users")
    public List<UserDto> getUsersByIds(@RequestParam (value = "ids", defaultValue = "") List<Integer> ids,
                                      @PositiveOrZero @RequestParam (value = "from", defaultValue = "0") int from,
                                      @Positive @RequestParam (value = "size", defaultValue = "10") int size) {
        log.info("GET /admin/users ids: {}; from: {}; size: {}", ids, from, size);
        List<User> usersList = userService.getUsersByIds(ids, from, size);
        return usersList.stream()
                .map(UserMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/users")
    public UserDto addUser(@RequestBody @Validated(Create.class) UserDto userDto) {
        User user = UserMapper.convertToEntity(userDto);
        log.info("POST /admin/users of: {}", user);
        return UserMapper.convertToDto(userService.addUser(user));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/users/{userId}")
    public void deleteUser(@PathVariable(value = "userId") long userId) {
        log.info("DELETE of: {}", userId);
        userService.deleteUser(userId);
    }
}