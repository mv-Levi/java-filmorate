package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей.");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Не удалось создать пользователя: электронная почта не может быть пустой и должна содержать символ @.");
            throw new ConditionsNotMetException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Не удалось создать пользователя: логин не может быть пустым и содержать пробелы.");
            throw new ConditionsNotMetException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Не удалось создать пользователя: дата рождения не может быть в будущем.");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        user.setBirthday(user.getBirthday());
        user.setEmail(user.getEmail());
        user.setLogin(user.getLogin());
        users.put(user.getId(), user);
        log.info("Пользователь с ID {} успешно создан.", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Не удалось обновить пользователя: ID не должен быть пустым.");
            throw new ConditionsNotMetException("Id не должен быть пустым");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
                log.error("Не удалось обновить пользователя: электронная почта не может быть пустой и должна содержать символ @.");
                throw new ConditionsNotMetException("Электронная почта не может быть пустой и должна содержать символ @");
            }
            if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
                log.error("Не удалось обновить пользователя: логин не может быть пустым и содержать пробелы.");
                throw new ConditionsNotMetException("Логин не может быть пустым и содержать пробелы");
            }
            if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(LocalDate.now())) {
                log.error("Не удалось обновить пользователя: дата рождения не может быть в будущем.");
                throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
            }

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName() == null
                    || newUser.getName().isBlank() ? newUser.getLogin() : newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь с ID {} успешно обновлен.", newUser.getId());
            return oldUser;
        }
        log.error("Не удалось обновить пользователя: пользователь с ID {} не найден.", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");

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
