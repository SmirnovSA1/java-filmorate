package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private final UserController userController = new UserController();

    @AfterEach
    void clean() {
        userController.users.clear();
    }

    @Test
    void addUserValid() throws ValidationException {
        final User user = new User(1,"test@test.ru",
                "testLogin", "Test-name", LocalDate.of(2015, 11, 10));
        userController.validate(user, "добавить");
    }

    @Test
    void addUserInvalid() {
        final User user = new User();
        Exception exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testingtest.com");
        exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testing@test.com");
        exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test login");
        exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test-login");
        exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());

        user.setBirthday(LocalDate.of(2025, 5, 2));
        exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());
    }

    @Test
    void getUsers() {
        assertEquals(userController.users.size(), 0);

        final User user = new User(1, "test@test.ru",
                "testLogin", "Test-name", LocalDate.of(2015, 11, 10));
        userController.users.put(user.getId(), user);
        assertEquals(userController.users.size(), 1);
    }

    @Test
    void updateUserValid() throws ValidationException {
        final User user = new User(1,"updatedEmail@test.com",
                "Updated_login", "UpdatedName", LocalDate.of(2014, 1, 15));
        userController.users.put(user.getId(), user);

        user.setName("New name");
        userController.users.put(user.getId(), user);

        assertEquals("New name", user.getName());
    }

    @Test
    void updateUserInvalid() {
        final User user = new User();
        Exception exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testingtest.com");
        exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testing@test.com");
        exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test login");
        exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test-login");
        exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());

        user.setBirthday(LocalDate.of(2025, 5, 2));
        exc = assertThrows(ValidationException.class, () -> userController.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());
    }
}