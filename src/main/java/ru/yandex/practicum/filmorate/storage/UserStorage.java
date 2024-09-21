package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User add(User user);

    User update(User user);

    Collection<User> getAll();

    Optional<User> getById(long id);

    void delete(long id);

    // Добавление друга (односторонняя дружба)
    void addFriend(long userId, long friendId);

    // Удаление друга (односторонняя дружба)
    void removeFriend(long userId, long friendId);

    //Получение всех друзей пользователя
    List<User> getFriends(long userId);
}
