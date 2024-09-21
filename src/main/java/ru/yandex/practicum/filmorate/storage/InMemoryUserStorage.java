package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>(); // Храним друзей для каждого пользователя

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User add(User user) {
        validateUser(user);

        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>()); // Инициализируем пустой список друзей
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id не должен быть пустым");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            validateUser(newUser);

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName() == null
                    || newUser.getName().isBlank() ? newUser.getLogin() : newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(long id) {
        users.remove(id);
        friends.remove(id);  // Удаляем список друзей, если пользователь удалён
    }

    @Override
    public void addFriend(long userId, long friendId) {
        validateFriendship(userId, friendId);
        friends.get(userId).add(friendId); // Добавляем друга только для одного пользователя
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friendId); // Удаляем друга из списка друзей пользователя
        }
    }

    @Override
    public List<User> getFriends(long userId) {
        if (!friends.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        // Возвращаем список друзей пользователя
        return friends.get(userId).stream()
                .map(friendId -> users.get(friendId))
                .collect(Collectors.toList());
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ConditionsNotMetException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }

    private void validateFriendship(long userId, long friendId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!users.containsKey(friendId)) {
            throw new NotFoundException("Друг с id = " + friendId + " не найден");
        }
        if (userId == friendId) {
            throw new ConditionsNotMetException("Нельзя добавить себя в друзья");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
