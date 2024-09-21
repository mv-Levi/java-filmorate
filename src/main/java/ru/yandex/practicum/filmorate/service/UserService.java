package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User add(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }

        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getById(long id) {
        return findUserByIdOrThrow(id);
    }

    public void addFriend(long userId, long friendId) {
        findUserByIdOrThrow(userId);
        findUserByIdOrThrow(friendId);
        userStorage.addFriend(userId, friendId);
        log.info("Пользователь с id: {} добавил в друзья пользователя с id: {}", userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        findUserByIdOrThrow(userId);
        findUserByIdOrThrow(friendId);
        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь с id: {} удалил из друзей пользователя с id: {}", userId, friendId);
    }

    public List<User> getAllFriends(long userId) {
        findUserByIdOrThrow(userId);
        log.info("Получение списка друзей пользователя с id: {}", userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        findUserByIdOrThrow(userId);
        findUserByIdOrThrow(otherUserId);
        Set<Long> userFriends = userStorage.getFriends(userId).stream().map(User::getId).collect(Collectors.toSet());
        Set<Long> otherUserFriends = userStorage.getFriends(otherUserId).stream().map(User::getId).collect(Collectors.toSet());
        userFriends.retainAll(otherUserFriends);
        log.info("Получение списка общих друзей пользователя с id: {} и пользователя с id: {}", userId, otherUserId);
        return userFriends.stream()
                .map(this::findUserByIdOrThrow)
                .collect(Collectors.toList());
    }

    private User findUserByIdOrThrow(long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", id)));
    }
}
