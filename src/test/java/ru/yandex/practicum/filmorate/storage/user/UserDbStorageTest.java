package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserService userService;
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new UserDbStorage(jdbcTemplate);
        userService = new UserService(userStorage);
    }

    @Test
    void getUsers() {
        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(2, "user2@email.ru", "vasya321", "Vasya Ivanov",
                LocalDate.of(1992, 2, 2), new HashSet<>());
        User user3 = new User(3, "user3@email.ru", "bogdan_ultra", "Bogdan Zhukov",
                LocalDate.of(1993, 3, 3), new HashSet<>());
        User user4 = new User(4, "user4@email.ru", "chizhik", "Eugene Kulakov",
                LocalDate.of(1994, 4, 4), new HashSet<>());
        User user5 = new User(5, "user5@email.ru", "lovec_snov", "Gregory Chimushin",
                LocalDate.of(1995, 5, 5), new HashSet<>());

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.createUser(user3);
        userStorage.createUser(user4);
        userStorage.createUser(user5);

        List<User> allUsers = userStorage.getUsers();

        assertThat(allUsers)
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(List.of(user1, user2, user3, user4, user5))
                .hasSize(5)
                .contains(user1, user2, user3, user4, user5);
    }

    @Test
    void getUserById() {
        // Подготавливаем данные для теста
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        userStorage.createUser(newUser);

        // вызываем тестируемый метод
        User savedUser = userStorage.getUserById(1);

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser);        // и сохраненного пользователя - совпадают
    }

    @Test
    void createUser() {
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        userService.validate(newUser, "добавить");
        User createdUser = userStorage.createUser(newUser);

        assertThat(createdUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser)
                .hasSameHashCodeAs(newUser);
    }

    @Test
    void updateUser() {
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        userStorage.createUser(newUser);
        newUser.setLogin("IVAN_test_Petrov");
        userService.validate(newUser, "обновить");
        User updatedUser = userStorage.updateUser(newUser);

        assertThat(updatedUser)
                .isNotNull()
                .matches(user -> user.getLogin().equals("IVAN_test_Petrov"))
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    void deleteUserById() {
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        User createdUser = userStorage.createUser(newUser);

        List<User> allUsers = userStorage.getUsers();

        assertThat(allUsers)
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(List.of(createdUser))
                .hasSize(1)
                .contains(createdUser);

        Map<String, String> response = userStorage.deleteUserById(1);

        assertThat(response)
                .containsKey("info")
                .contains(Map.entry("info","Пользователь по id: 1 успешно удален"))
                .containsEntry("info", "Пользователь по id: 1 успешно удален");
    }

    @Test
    void deleteAllUsers() {
        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(2, "user2@email.ru", "vasya321", "Vasya Ivanov",
                LocalDate.of(1992, 2, 2), new HashSet<>());
        User user3 = new User(3, "user3@email.ru", "bogdan_ultra", "Bogdan Zhukov",
                LocalDate.of(1993, 3, 3), new HashSet<>());
        User user4 = new User(4, "user4@email.ru", "chizhik", "Eugene Kulakov",
                LocalDate.of(1994, 4, 4), new HashSet<>());
        User user5 = new User(5, "user5@email.ru", "lovec_snov", "Gregory Chimushin",
                LocalDate.of(1995, 5, 5), new HashSet<>());

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.createUser(user3);
        userStorage.createUser(user4);
        userStorage.createUser(user5);

        List<User> allUsers = userStorage.getUsers();

        assertThat(allUsers)
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(List.of(user1, user2, user3, user4, user5))
                .hasSize(5)
                .contains(user1, user2, user3, user4, user5);

        Map<String, String> response = userStorage.deleteAllUsers();

        assertThat(response)
                .containsKey("info")
                .contains(Map.entry("info","Все пользователи удалены"))
                .containsEntry("info", "Все пользователи удалены");
    }

    @Test
    void addFriend() {
        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(2, "user2@email.ru", "vasya321", "Vasya Ivanov",
                LocalDate.of(1992, 2, 2), new HashSet<>());

        userStorage.createUser(user1);
        userStorage.createUser(user2);

        Set<Integer> user1Friends = user1.getFriends();

        assertThat(user1Friends)
                .isNotNull()
                .isEmpty();

        User userWhoAddedToFriend = userStorage.addFriend(user1.getId(), user2.getId());
        Set<Integer> updatedUser1Friends = userWhoAddedToFriend.getFriends();

        assertThat(updatedUser1Friends)
                .isNotNull()
                .contains(2)
                .hasSize(1)
                .isNotEmpty();

        List<User> user1FriendList = userStorage.getFriendList(user1.getId());

        assertThat(user1FriendList)
                .isNotEmpty()
                .isNotNull()
                .hasSize(1)
                .hasSameSizeAs(List.of(user2))
                .contains(user2);
    }

    @Test
    void deleteFriend() {
        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(2, "user2@email.ru", "vasya321", "Vasya Ivanov",
                LocalDate.of(1992, 2, 2), new HashSet<>());

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.addFriend(user1.getId(), user2.getId());

        User userWhoDeletedFriend = userStorage.deleteFriend(user1.getId(), user2.getId());
        Set<Integer> updatedUser1Friends = userWhoDeletedFriend.getFriends();

        assertThat(updatedUser1Friends)
                .isNotNull()
                .doesNotContain(2)
                .hasSize(0)
                .isEmpty();

        List<User> user1FriendList = userStorage.getFriendList(userWhoDeletedFriend.getId());

        assertThat(user1FriendList)
                .isNotNull()
                .hasSize(0)
                .hasSameSizeAs(List.of())
                .doesNotContain(user2)
                .isEmpty();
    }

    @Test
    void getFriendList() {
        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(2, "user2@email.ru", "vasya321", "Vasya Ivanov",
                LocalDate.of(1992, 2, 2), new HashSet<>());

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.addFriend(user1.getId(), user2.getId());

        List<User> user1FriendList = userStorage.getFriendList(user1.getId());

        assertThat(user1FriendList)
                .isNotEmpty()
                .isNotNull()
                .hasSize(1)
                .hasSameSizeAs(List.of(user2))
                .contains(user2);
    }

    @Test
    void getCommonFriendList() {
        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(2, "user2@email.ru", "vasya321", "Vasya Ivanov",
                LocalDate.of(1992, 2, 2), new HashSet<>());
        User user3 = new User(3, "user3@email.ru", "bogdan_ultra", "Bogdan Zhukov",
                LocalDate.of(1993, 3, 3), new HashSet<>());
        User user4 = new User(4, "user4@email.ru", "chizhik", "Eugene Kulakov",
                LocalDate.of(1994, 4, 4), new HashSet<>());
        User user5 = new User(5, "user5@email.ru", "lovec_snov", "Gregory Chimushin",
                LocalDate.of(1995, 5, 5), new HashSet<>());

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.createUser(user3);
        userStorage.createUser(user4);
        userStorage.createUser(user5);

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.addFriend(user1.getId(), user3.getId());
        userStorage.addFriend(user1.getId(), user5.getId());
        userStorage.addFriend(user2.getId(), user4.getId());
        userStorage.addFriend(user2.getId(), user5.getId());

        List<User> commonFriends = userStorage.getCommonFriendList(user1.getId(), user2.getId());

        assertThat(commonFriends)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .hasSameSizeAs(List.of(user5))
                .contains(user5);
    }
}