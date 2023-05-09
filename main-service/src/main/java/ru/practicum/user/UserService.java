package ru.practicum.user;

import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsersByIds(List<Integer> ids, int from, int size);

    User addUser(User user);

    void deleteUser(long userId);

    User getUserById(long userId);
}