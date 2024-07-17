package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получение всех пользователей");
        return userStorage.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Создание нового пользователя: {}", user);
        return userStorage.addUser(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Обновление пользователя: {}", newUser);
        return userStorage.updateUser(newUser);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пользователь с id: {} добавляет в друзья пользователя с id: {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        log.info("Получение пользователя с id: {}", id);
        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пользователь с id: {} удаляет из друзей пользователя с id: {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Получение списка общих друзей пользователя с id: {} и пользователя с id: {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("{id}/friends")
    public List<User> getAllFriends(@PathVariable long id) {
        log.info("Получение списка друзей пользователя с id: {}", id);
        return userService.getAllFriends(id);
    }


}
