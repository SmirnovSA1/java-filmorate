package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        final User user = users.get(id);

        if (user == null) {
            log.error("Произошла ошибка при вызове метода getUserById");
            throw new NotFoundException(String.format("Пользователь с id: %s не найден", id));
        }

        log.info("Получен пользователь: {} \n по id: {}", user, id);
        return user;
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().trim().isBlank()) {
            log.debug("Пользователю присвоено значение логина в качестве имени, т.к. не было указано в запросе");
            user.setName(user.getLogin());
        }

        user.generateId();
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Произошла ошибка при вызове метода updateUser");
            throw new NotFoundException(String.format("Пользователь с id: %s не найден", user.getId()));
        }

        users.put(user.getId(), user);
        log.info("Данные пользователя успешно обновлены: {}", user);
        return user;
    }

    @Override
    public Map<String, String> deleteUserById(Integer userId) {
        getUserById(userId);
        users.remove(userId);
        log.info("Пользователь по id: {} успешно удален", userId);
        return Map.of("info", String.format("Пользователь по id: %s успешно удален", userId));
    }

    @Override
    public Map<String, String> deleteAllUsers() {
        users.clear();
        log.info("Все пользователи успешно удалены");
        return Map.of("info", String.format("Все пользователи успешно удалены"));
    }
}
