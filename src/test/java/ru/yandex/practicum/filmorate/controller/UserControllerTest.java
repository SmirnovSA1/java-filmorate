package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class userServiceTest {
    private UserService userService;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;


    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
        user1 = new User(1,"test@test.ru",
                "testLogin", "Test-name",
                LocalDate.of(2015, 11, 10));
        user2 = new User(2,"test2@test2.ru",
                "testLogin2", "Test-name2",
                LocalDate.of(1994, 5, 12));
        user3 = new User(3,"test3@test3.ru",
                "testLogin3", "Test-name3",
                LocalDate.of(1993, 1, 16));
        user4 = new User(4,"test4@test4.ru",
                "testLogin4", "Test-name4",
                LocalDate.of(1978, 7, 23));
        user5 = new User(5,"test5@test5.ru",
                "testLogin5", "Test-name5",
                LocalDate.of(1996, 11, 20));
        user6 = new User(6,"test6@test6.ru",
                "testLogin6", "Test-name6",
                LocalDate.of(1969, 6, 16));
    }

    @AfterEach
    void clean() {
        userService.deleteAllUsers();
        User.resetCount();
    }

    @Test
    void createUserValid() throws ValidationException {
        userService.validate(user1, "добавить");
    }

    @Test
    void createUserInvalid() {
        final User user = new User();
        Exception exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testingtest.com");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testing@test.com");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test login");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test-login");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());

        user.setBirthday(LocalDate.of(2025, 5, 2));
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "добавить"));
        assertEquals("Не удалось добавить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());
    }

    @Test
    void getUserByIdValid() {
        userService.createUser(user1);
        final User otherUser = userService.getUserById(1);
        assertNotNull(otherUser);
    }

    @Test
    void getUserByIdInvalid() {
        userService.createUser(user1);

        Exception exc = assertThrows(NotFoundException.class, () -> userService.getUserById(2));
        assertEquals("Пользователь с id: 2 не найден", exc.getMessage());

        final List<User> userList = userService.getUsers();
        assertNotEquals(userList.size(), 2);
        assertFalse(userList.contains(user2));
    }

    @Test
    void getUsers() {
        assertEquals(userService.getUsers().size(), 0);
        userService.createUser(user1);
        assertEquals(userService.getUsers().size(), 1);
    }

    @Test
    void updateUserValid() {
        userService.createUser(user1);
        user1.setName("New name");
        userService.updateUser(user1);
        assertEquals("New name", user1.getName());
    }

    @Test
    void updateUserInvalid() {
        final User user = new User();
        Exception exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testingtest.com");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. email не заполнено или указано некорректно",
                exc.getMessage());

        user.setEmail("testing@test.com");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test login");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. логин пустой или содержит пробелы.",
                exc.getMessage());

        user.setLogin("Test-login");
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());

        user.setBirthday(LocalDate.of(2025, 5, 2));
        exc = assertThrows(ValidationException.class, () -> userService.validate(user,
                "обновить"));
        assertEquals("Не удалось обновить пользователя, т.к. дата рождения не может быть позже текущей даты.",
                exc.getMessage());
    }

    @Test
    void deleteUserByIdValid() {
        userService.createUser(user1);
        Map<String, String> response = userService.deleteUserById(1);
        assertEquals(response, Map.of("info", String.format("Пользователь по id: 1 успешно удален")));
        assertEquals(userService.getUsers().size(), 0);
    }

    @Test
    void deleteUserByIdInvalid() {
        Exception exc = assertThrows(NotFoundException.class, () -> userService.getUserById(1));
        assertEquals("Пользователь с id: 1 не найден", exc.getMessage());
        assertFalse(userService.getUsers().remove(user1));
    }

    @Test
    void deleteAllUsers() {
        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);
        Map<String, String> response = userService.deleteAllUsers();
        assertEquals(response, Map.of("info", String.format("Все пользователи успешно удалены")));
        assertEquals(userService.getUsers().size(), 0);
    }

    @Test
    void addFriend() {
        userService.createUser(user1);
        userService.createUser(user2);
        userService.addFriend(1, 2);

        assertEquals(user1.getFriends().size(), 1);
        assertFalse(user1.getFriends().isEmpty());
        assertEquals(user1.getFriends(), Set.of(2));
        assertEquals(user2.getFriends().size(), 1);
        assertFalse(user2.getFriends().isEmpty());
        assertEquals(user2.getFriends(), Set.of(1));
    }

    @Test
    void deleteFriend() {
        userService.createUser(user1);
        userService.createUser(user2);
        userService.addFriend(1, 2);
        userService.deleteFriend(1, 2);

        assertEquals(user1.getFriends().size(), 0);
        assertTrue(user1.getFriends().isEmpty());
        assertEquals(user1.getFriends(), Set.of());
        assertEquals(user2.getFriends().size(), 0);
        assertTrue(user2.getFriends().isEmpty());
        assertEquals(user2.getFriends(), Set.of());
    }

    @Test
    void getFriendList() {
        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);
        userService.createUser(user4);
        userService.createUser(user5);
        userService.createUser(user6);

        userService.addFriend(1, 3);
        userService.addFriend(1, 5);
        userService.addFriend(1, 6);

        userService.addFriend(2, 1);
        userService.addFriend(2, 3);
        userService.addFriend(2, 6);

        userService.addFriend(3, 5);

        assertEquals(userService.getFriendList(1).size(), 4);
        assertFalse(userService.getFriendList(1).isEmpty());
        assertEquals(userService.getFriendList(1), List.of(user2, user3, user5, user6));

        assertEquals(userService.getFriendList(2).size(), 3);
        assertFalse(userService.getFriendList(2).isEmpty());
        assertEquals(userService.getFriendList(2), List.of(user1, user3, user6));

        assertEquals(userService.getFriendList(3).size(), 3);
        assertFalse(userService.getFriendList(3).isEmpty());
        assertEquals(userService.getFriendList(3), List.of(user1, user2, user5));

        assertEquals(userService.getFriendList(4).size(), 0);
        assertTrue(userService.getFriendList(4).isEmpty());
        assertEquals(userService.getFriendList(4), List.of());

        assertEquals(userService.getFriendList(5).size(), 2);
        assertFalse(userService.getFriendList(5).isEmpty());
        assertEquals(userService.getFriendList(5), List.of(user1, user3));

        assertEquals(userService.getFriendList(6).size(), 2);
        assertFalse(userService.getFriendList(6).isEmpty());
        assertEquals(userService.getFriendList(6), List.of(user1, user2));
    }

    @Test
    void getCommonFriendList() {
        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);
        userService.createUser(user4);
        userService.createUser(user5);
        userService.createUser(user6);

        userService.addFriend(1, 3);
        userService.addFriend(1, 5);
        userService.addFriend(1, 6);

        userService.addFriend(2, 1);
        userService.addFriend(2, 3);
        userService.addFriend(2, 6);

        userService.addFriend(3, 5);

        assertEquals(userService.getCommonFriendList(1, 2).size(), 2);
        assertFalse(userService.getCommonFriendList(1, 2).isEmpty());
        assertEquals(userService.getCommonFriendList(1, 2),
                userService.getCommonFriendList(2, 1));

        assertEquals(userService.getCommonFriendList(1, 4).size(), 0);
        assertTrue(userService.getCommonFriendList(1, 4).isEmpty());
        assertEquals(userService.getCommonFriendList(1, 4),
                userService.getCommonFriendList(4, 1));

        assertEquals(userService.getCommonFriendList(3, 5).size(), 1);
        assertFalse(userService.getCommonFriendList(3, 5).isEmpty());
        assertEquals(userService.getCommonFriendList(3, 5),
                userService.getCommonFriendList(5, 3));
    }
}