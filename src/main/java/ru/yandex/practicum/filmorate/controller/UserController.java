package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получение всех пользователей");
        return userService.getAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Создание нового пользователя: {}", user);
        return userService.add(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Обновление пользователя: {}", newUser);
        return userService.update(newUser);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void add(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пользователь с id: {} добавляет в друзья пользователя с id: {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable long id) {
        log.info("Получение пользователя с id: {}", id);
        return userService.getById(id);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void remove(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пользователь с id: {} удаляет из друзей пользователя с id: {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommon(@PathVariable long id, @PathVariable long otherId) {
        log.info("Получение списка общих друзей пользователя с id: {} и пользователя с id: {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("{id}/friends")
    public List<User> getAll(@PathVariable long id) {
        log.info("Получение списка друзей пользователя с id: {}", id);
        return userService.getAllFriends(id);
    }


}
