package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    public ResponseEntity<Collection<User>> findAll() {
        log.info("Получение всех пользователей");
        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        log.info("Создание нового пользователя: {}", user);
        User createdUser = userService.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping
    public ResponseEntity<User> update(@RequestBody User newUser) {
        log.info("Обновление пользователя: {}", newUser);
        User updatedUser = userService.update(newUser);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("{id}/friends/{friendId}")
    public ResponseEntity<Void> add(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пользователь с id: {} добавляет в друзья пользователя с id: {}", id, friendId);
        userService.addFriend(id, friendId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public ResponseEntity<Void> remove(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пользователь с id: {} удаляет из друзей пользователя с id: {}", id, friendId);
        userService.removeFriend(id, friendId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable long id) {
        log.info("Получение пользователя с id: {}", id);
        try {
            User user = userService.getById(id);
            return ResponseEntity.ok(user);
        } catch (NotFoundException e) {
            log.error("Ошибка при получении пользователя с id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommon(@PathVariable long id, @PathVariable long otherId) {
        log.info("Получение списка общих друзей пользователя с id: {} и пользователя с id: {}", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        return ResponseEntity.ok(commonFriends);
    }

    @GetMapping("{id}/friends")
    public ResponseEntity<List<User>> getAll(@PathVariable long id) {
        log.info("Получение списка друзей пользователя с id: {}", id);
        List<User> friends = userService.getAllFriends(id);
        return ResponseEntity.ok(friends);
    }


}
