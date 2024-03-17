package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Primary
@RequiredArgsConstructor
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getUsers() {
        String query = "SELECT * FROM users";

        return jdbcTemplate.query(query, new UserMapper(jdbcTemplate));
    }

    @Override
    public User getUserById(Integer id) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        User foundUser;

        try {
            foundUser = jdbcTemplate.queryForObject(query, new UserMapper(jdbcTemplate), id);
            return foundUser;
        } catch (RuntimeException e) {
            log.info("Пользователь с id {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        int id = simpleJdbcInsert.executeAndReturnKey(
                        Map.of(
                                "user_email", user.getEmail(),
                                "user_login", user.getLogin(),
                                "user_name", user.getName(),
                                "user_birthday", user.getBirthday()))
                .intValue();

        log.info("Создан пользователей с id {} в таблице users", id);

        user.setId(id);
        checkUserFriend(user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        String query = "UPDATE users SET user_email = ?, " +
                "user_login = ?, " +
                "user_name = ?, " +
                "user_birthday = ? " +
                "WHERE user_id = ?;";

        int countUpdatedLines = jdbcTemplate.update(query,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (countUpdatedLines == 0) {
            log.info("Пользователь с id {} не найден", user.getId());
            throw new NotFoundException(String.format("Пользователь с id %d не найден", user.getId()));
        }

        log.info("Пользователь с id {} обновлен", user.getId());

        checkUserFriend(user);

        return user;
    }

    @Override
    public Map<String, String> deleteUserById(Integer id) {
        String query = "DELETE " +
                "FROM users " +
                "WHERE user_id = ?";
        int deleteLine = jdbcTemplate.update(query, id);

        if (deleteLine == 0) {
            log.info("Пользователь для удаления по id {} не найден", id);
            throw new NotFoundException(String.format("Пользователь для удаления по id %d не найден", id));
        }

        return Map.of("info", String.format("Пользователь по id: %d успешно удален", id));
    }

    @Override
    public Map<String, String> deleteAllUsers() {
        String query = "DELETE FROM users";
        int deleteLines = jdbcTemplate.update(query);

        if (deleteLines == 0) {
            log.info("Не найдено ни одного пользователя для удаления не найдено");
            throw new NotFoundException(String.format("Не найдено ни одного пользователя для удаления не найдено"));
        }

        return Map.of("info", String.format("Все пользователи удалены"));
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        final User foundUser = getUserById(userId);
        final User foundFriend = getUserById(friendId);

        // проверим, есть ли лайк фильму от пользователя
        String query = "SELECT " +
                "COUNT(friendship_id) " +
                "FROM friendships " +
                "WHERE user_id = ? AND friend_id = ?;";
        int foundLine = jdbcTemplate.queryForObject(query, Integer.class, userId, friendId);

        if (foundLine > 0) {
            log.info("Пользователь с id {} уже находится в друзьях у пользователя с id {}", friendId, userId);
            throw new AlreadyExistException(String.format(
                    "Пользователь с id %d уже находится в друзьях у пользователя с id %d",
                    friendId, userId));
        }

        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id, friendship_confirmed) VALUES (?, ?, ?);",
                userId, friendId, false);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);

        List<Integer> userFriends = jdbcTemplate.queryForList(
                "SELECT friend_id FROM friendships WHERE user_id = ? AND friend_id = ?;",
                Integer.class, userId, friendId);
        foundUser.setFriends(new HashSet<>(userFriends));

        return foundUser;
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        final User foundUser = getUserById(userId);
        final User foundFriend = getUserById(friendId);

        String query = "DELETE " +
                "FROM friendships " +
                "WHERE user_id = ? AND friend_id = ?;";
        int deletedLine = jdbcTemplate.update(query, userId, friendId);

        if (deletedLine == 0) {
            log.info("Не найдено данных по id {} и {}", userId, friendId);
            throw new NotFoundException(String.format("Не найдено данных по id %d и %d",
                    userId, friendId));
        }

        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);

        List<Integer> userFriends = jdbcTemplate.queryForList(
                "SELECT friend_id FROM friendships WHERE user_id = ? AND friend_id = ?;",
                Integer.class, userId, friendId);
        foundUser.setFriends(new HashSet<>(userFriends));

        return foundUser;
    }

    @Override
    public List<User> getFriendList(Integer userId) {
        String query = "SELECT u.* " +
                "FROM users AS u " +
                "JOIN friendships AS fs ON u.user_id = fs.user_id " +
                "WHERE user_id = ?;";

        return jdbcTemplate.query(query, new UserMapper(jdbcTemplate), userId);
    }

    @Override
    public List<User> getCommonFriendList(Integer userId, Integer otherId) {
        String query = "SELECT * " +
                "FROM users " +
                "WHERE user_id IN (SELECT friend_id " +
                    "FROM friendships " +
                    "WHERE user_id = ?" +
                    "INTERSECT SELECT friend_id " +
                    "FROM friendships " +
                    "WHERE user_id = ?" +
                ");";
        List<User> commonFriends = jdbcTemplate.query(query, new UserMapper(jdbcTemplate), userId, otherId);

        return commonFriends;
    }

    private void checkUserFriend(User user) {
        if (user.getFriends() != null) {
            for (int friend : user.getFriends()) {
                jdbcTemplate.update(
                        "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?);",
                        user.getId(),
                        friend
                );
            }
        }
    }
}
