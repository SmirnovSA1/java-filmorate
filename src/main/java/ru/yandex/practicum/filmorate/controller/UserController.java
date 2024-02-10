package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends AbstractController<User> {
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Adding user: {}", user);
        user.generateId();

        if (user.getName() == null || user.getName().trim().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User updateUser (@Valid @RequestBody User user) throws ValidationException {
        log.info("Updating user: {}", user);

        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Не удалось обновить пользователя, " +
                    "т.к. пользователь по указанному id не найден.");
        }

        users.put(user.getId(), user);
        return user;
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
