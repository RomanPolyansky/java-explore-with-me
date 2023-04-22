package ru.practicum.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.user.model.QUser;
import ru.practicum.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<User> getUsersByIds(List<Integer> ids, int from, int size) {
        List<User> foundUsers;
        if (ids.isEmpty()) {
            foundUsers = IterableUtils.toList(jpaQueryFactory.selectFrom(QUser.user)
                    .orderBy(QUser.user.id.asc())
                    .offset(from)
                    .limit(size)
                    .fetch());
        } else {
            foundUsers = IterableUtils.toList(jpaQueryFactory.selectFrom(QUser.user)
                    .where(QUser.user.id.intValue().in(ids))
                    .orderBy(QUser.user.id.asc())
                    .offset(from)
                    .limit(size)
                    .fetch());
        }
        log.info("UserRepository returned: {}", foundUsers);
        return foundUsers;
    }

    @Override
    public User addUser(User user) {
        User savedUser = userRepository.save(user);
        log.info("UserRepository saved: {}", savedUser);
        return savedUser;
    }

    @Override
    public void deleteUser(long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("User with id=%s was not found", userId))
        );
        log.info("UserRepository deletes: {}", userToDelete);
        userRepository.deleteById(userId);
    }
}
