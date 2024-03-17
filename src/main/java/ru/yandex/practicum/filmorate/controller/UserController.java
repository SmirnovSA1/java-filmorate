package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        log.info("Получение списка пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable(name = "id") Integer id) {
        log.info("Получение пользователя по id: {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновление данных пользователя: {}", user);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteUserById(@PathVariable(name = "id") Integer id) {
        log.info("Удаление пользователя по id: {}", id);
        return userService.deleteUserById(id);
    }

    @DeleteMapping
    public Map<String, String> deleteAllUsers() {
        log.info("Удаление всех пользователей");
        return userService.deleteAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable(name = "id") Integer userId,
                          @PathVariable(name = "friendId") Integer friendId) {
        log.info("Пользователь с id {} добавляет в друзья пользователя с id {}", userId, friendId);
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable(name = "id") Integer userId,
                             @PathVariable(name = "friendId") Integer friendId) {
        log.info("Пользователь с id {} удаляет из друзей пользователя с id {}", userId, friendId);
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable(name = "id") Integer userId) {
        log.info("Получение списка друзей пользователя {}", userId);
        return userService.getFriendList(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendList(@PathVariable(name = "id") Integer userId,
                                          @PathVariable(name = "otherId") Integer otherId) {
        log.info("Получение списка общих друзей пользователя с id {} с пользователем с id {}",
                userId, otherId);
        return userService.getCommonFriendList(userId, otherId);
    }
}
