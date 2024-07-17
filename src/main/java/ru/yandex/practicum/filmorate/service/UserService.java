package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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

    public void addFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        User friend = userStorage.getUserById(friendId).orElseThrow(() -> new NotFoundException("Друг не найден"));
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь с id: {} добавил в друзья пользователя с id: {}", userId, friendId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        User friend = userStorage.getUserById(friendId).orElseThrow(() -> new NotFoundException("Друг не найден"));
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь с id: {} удалил из друзей пользователя с id: {}", userId, friendId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public List<User> getAllFriends(long userId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Получение списка друзей пользователя с id: {}", userId);
        return user.getFriends().stream()
                .map(friendId -> userStorage.getUserById(friendId).orElseThrow(() -> new NotFoundException("Друг не найден")))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        User otherUser = userStorage.getUserById(otherUserId).orElseThrow(() -> new NotFoundException("Друг не найден"));
        Set<Long> commonFriendsIds = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toSet());
        log.info("Получение списка общих друзей пользователя с id: {} и пользователя с id: {}", userId, otherUser);
        return commonFriendsIds.stream()
                .map(friendId -> userStorage.getUserById(friendId).orElseThrow(() -> new NotFoundException("Пользователь не найден")))
                .collect(Collectors.toList());
    }
}
