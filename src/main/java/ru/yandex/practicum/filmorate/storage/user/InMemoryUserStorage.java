package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
@Qualifier("inMemoryUserStorage")
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

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        final User user = getUserById(userId);
        final User friend = getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи {} и {} стали друзьями", user.getName(), friend.getName());

        updateUser(friend);
        return updateUser(user);
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        final User user = getUserById(userId);
        final User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователи {} и {} больше не друзья", user.getName(), friend.getName());

        updateUser(friend);
        return updateUser(user);
    }

    @Override
    public List<User> getFriendList(Integer userId) {
        final User user = getUserById(userId);

        if (user.getFriends() == null || user.getFriends().isEmpty()) {
            log.info("Пользователь пока еще ни с кем не подружился");
            return List.of();
        } else {
            List<User> friendList = new ArrayList<>();

            for (Integer id : user.getFriends()) {
                friendList.add(getUserById(id));
            }

            log.info("Получен список друзей пользователя {} \n {}", user.getName(), friendList);
            return friendList;
        }
    }

    @Override
    public List<User> getCommonFriendList(Integer userId, Integer otherId) {
        List<User> commonFriends = new ArrayList<>();

        final User user = getUserById(userId);
        final User otherUser = getUserById(otherId);

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
            commonFriends.add(getUserById(i.next()));
        }

        log.info("Получен список общих друзей пользователей {} и {}", user.getName(), otherUser.getName());
        return commonFriends;
    }
}
