package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User add(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getById(long id) {
        return findUserByIdOrThrow(id);
    }

    public void addFriend(long userId, long friendId) {
        User user = findUserByIdOrThrow(userId);
        User friend = findFriendByIdOrThrow(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь с id: {} добавил в друзья пользователя с id: {}", userId, friendId);
        userStorage.update(user);
        userStorage.update(friend);
    }

    public void removeFriend(long userId, long friendId) {
        User user = findUserByIdOrThrow(userId);
        User friend = findFriendByIdOrThrow(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь с id: {} удалил из друзей пользователя с id: {}", userId, friendId);
        userStorage.update(user);
        userStorage.update(friend);
    }

    public List<User> getAllFriends(long userId) {
        User user = findUserByIdOrThrow(userId);
        log.info("Получение списка друзей пользователя с id: {}", userId);
        return user.getFriends().stream()
                .map(friendId -> findFriendByIdOrThrow(friendId))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        User user = findUserByIdOrThrow(userId);
        User otherUser = findFriendByIdOrThrow(otherUserId);
        Set<Long> commonFriendsIds = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toSet());
        log.info("Получение списка общих друзей пользователя с id: {} и пользователя с id: {}", userId, otherUser);
        return commonFriendsIds.stream()
                .map(friendId -> findUserByIdOrThrow(friendId))
                .collect(Collectors.toList());
    }

    private User findUserByIdOrThrow(long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", id)));
    }

    private User findFriendByIdOrThrow(long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Друг с id: %d не найден", id)));
    }
}
