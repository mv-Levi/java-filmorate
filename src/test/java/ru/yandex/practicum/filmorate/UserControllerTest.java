package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private UserController userController;

    private UserStorage userStorage;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @Test
    void testCreateUserWithInvalidEmail() {
        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> {
            user.setEmail("invalidEmail");
            userController.create(user);
        });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void testCreateUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> {
            user.setLogin("");
            userController.create(user);
        });

        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void testCreateUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.now().plusDays(1));

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> {
            userController.create(user);
        });

        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void testCreateValidUser() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);
        assertEquals(1, createdUser.getId());
        assertEquals("email@example.com", createdUser.getEmail());
        assertEquals("login", createdUser.getLogin());
        assertEquals("name", createdUser.getName());
        assertEquals(LocalDate.of(2000, 1, 1), createdUser.getBirthday());
    }
}
