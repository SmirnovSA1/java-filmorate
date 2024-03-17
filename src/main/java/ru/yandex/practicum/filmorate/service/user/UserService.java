package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Map<String, String> deleteUserById(Integer userId) {
        return userStorage.deleteUserById(userId);
    }

    public Map<String, String> deleteAllUsers() {
        return userStorage.deleteAllUsers();
    }

    public User addFriend(Integer userId, Integer friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriendList(Integer userId) {
        return userStorage.getFriendList(userId);
    }

    public List<User> getCommonFriendList(Integer userId, Integer otherId) {
        return userStorage.getCommonFriendList(userId, otherId);
    }

    public void validate(User user, String messagePath) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().trim().isBlank() || !user.getEmail().trim().contains("@")) {
            throw new ValidationException("Не удалось " + messagePath + " пользователя, " +
                    "т.к. email не заполнено или указано некорректно");
        }

        if (user.getLogin() == null || user.getLogin().trim().isBlank() || user.getLogin().trim().contains(" ")) {
            throw new ValidationException("Не удалось " + messagePath + " пользователя, " +
                    "т.к. логин пустой или содержит пробелы.");
        }

        if (user.getName() == null || user.getName().trim().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Не удалось " + messagePath + " пользователя, " +
                    "т.к. дата рождения не может быть позже текущей даты.");
        }
    }
}
