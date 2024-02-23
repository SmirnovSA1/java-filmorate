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
        log.info("Получение списка пользователей");
        return userStorage.getUsers();
    }

    public User getUserById(Integer userId) {
        log.info("Получение пользователя по id: {}", userId);
        return userStorage.getUserById(userId);
    }

    public User createUser(User user) {
        log.info("Создание пользователя: {}", user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        log.info("Обновление данных пользователя: {}", user);
        return userStorage.updateUser(user);
    }

    public Map<String, String> deleteUserById(Integer userId) {
        log.info("Удаление пользователя по id: {}", userId);
        return userStorage.deleteUserById(userId);
    }

    public Map<String, String> deleteAllUsers() {
        log.info("Удаление всех пользователей");
        return userStorage.deleteAllUsers();
    }

    public User addFriend(Integer userId, Integer friendId) {
        final User user = userStorage.getUserById(userId);
        final User friend = userStorage.getUserById(friendId);
        log.info("Пользователь {} добавляет в друзья пользователя {}", user.getName(), friend.getName());

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи {} и {} стали друзьями", user.getName(), friend.getName());

        userStorage.updateUser(friend);
        return userStorage.updateUser(user);
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        final User user = userStorage.getUserById(userId);
        final User friend = userStorage.getUserById(friendId);
        log.info("Пользователь {} удаляет из друзей пользователя {}", user.getName(), friend.getName());

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователи {} и {} больше не друзья", user.getName(), friend.getName());

        userStorage.updateUser(friend);
        return userStorage.updateUser(user);
    }

    public List<User> getFriendList(Integer userId) {
        final User user = userStorage.getUserById(userId);
        log.info("Получение списка друзей пользователя {}", user.getName());

        if (user.getFriends() == null || user.getFriends().isEmpty()) {
            log.info("Пользователь пока еще ни с кем не подружился");
            return List.of();
        } else {
            List<User> friendList = new ArrayList<>();

            for (Integer id : user.getFriends()) {
                friendList.add(userStorage.getUserById(id));
            }

            log.info("Получен список друзей пользователя {} \n {}", user.getName(), friendList);
            return friendList;
        }
    }

    public List<User> getCommonFriendList(Integer userId, Integer otherId) {
        List<User> commonFriends = new ArrayList<>();

        final User user = userStorage.getUserById(userId);
        final User otherUser = userStorage.getUserById(otherId);

        log.info("Получение списка общих друзей пользователей {} и {}", user.getName(), otherUser.getName());
        final Set<Integer> userFriends = user.getFriends();
        final Set<Integer> otherUserFriends = otherUser.getFriends();

        if ((userFriends == null || userFriends.isEmpty()) || (otherUserFriends == null || otherUserFriends.isEmpty())) {
            log.info("У пользователей {} и {} нет общих друзей", user.getName(), otherUser.getName());
            return commonFriends;
        }

        Set<Integer> otherUserFriendsSet = new HashSet<>(userFriends);
        otherUserFriendsSet.retainAll(otherUserFriends);

        Iterator<Integer> i = otherUserFriendsSet.iterator();
        while (i.hasNext()) {
            commonFriends.add(userStorage.getUserById(i.next()));
        }

        log.info("Получен список общих друзей пользователей {} и {}", user.getName(), otherUser.getName());
        return commonFriends;
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
