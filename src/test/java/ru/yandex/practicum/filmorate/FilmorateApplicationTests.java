package ru.yandex.practicum.filmorate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmorateApplicationTests {
    @Autowired
    private FilmDbStorage filmStorage;

    private final UserDbStorage userStorage;

    @Test
    public void testCreateAndFindFilm() {
        Film film = new Film();
        film.setName("New Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(120);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(1L);
        mpaRating.setName("PG-13");
        film.setMpaRating(mpaRating);

        filmStorage.add(film);

        Optional<Film> filmOptional = filmStorage.getById(film.getId());
        assertThat(filmOptional).isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", "New Film")
                );
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(120);
        MpaRating mpaRating = new MpaRating(1L, "PG");
        film.setMpaRating(mpaRating);

        Film savedFilm = filmStorage.add(film);

        // Обновляем информацию о фильме
        savedFilm.setName("Updated Film");
        savedFilm.setDescription("Updated Description");

        filmStorage.update(savedFilm);

        Optional<Film> updatedFilm = filmStorage.getById(savedFilm.getId());

        assertTrue(updatedFilm.isPresent(), "Обновленный фильм должен быть найден");
        assertEquals("Updated Film", updatedFilm.get().getName());
        assertEquals("Updated Description", updatedFilm.get().getDescription());
    }

    @Test
    public void testGetAllFilms() {
        // Добавляем первый фильм
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2024, 1, 1));
        film1.setDuration(120);
        MpaRating mpaRating1 = new MpaRating(1L, "PG");
        film1.setMpaRating(mpaRating1);
        filmStorage.add(film1);

        // Добавляем второй фильм
        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2024, 2, 1));
        film2.setDuration(90);
        MpaRating mpaRating2 = new MpaRating(2L, "R");
        film2.setMpaRating(mpaRating2);
        filmStorage.add(film2);

        // Получаем все фильмы
        Collection<Film> films = filmStorage.getAll();

        // Проверяем, что возвращены все добавленные фильмы
        assertNotNull(films);
        assertEquals(2, films.size());

        // Проверяем содержимое коллекции
        List<String> filmNames = films.stream().map(Film::getName).toList();
        assertTrue(filmNames.contains("Film 1"));
        assertTrue(filmNames.contains("Film 2"));
    }

    @Test
    public void testDeleteFilm() {
        // Добавляем фильм
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(120);

        MpaRating mpaRating = new MpaRating(1L, "PG");
        film.setMpaRating(mpaRating);

        Film addedFilm = filmStorage.add(film);

        // Удаляем фильм
        filmStorage.delete(addedFilm.getId());

        // Проверяем, что фильм действительно удален
        Optional<Film> deletedFilm = filmStorage.getById(addedFilm.getId());
        assertFalse(deletedFilm.isPresent(), "Фильм должен быть удален");
    }
    
    @Test
    public void testAddUser() {
        // Создаем нового пользователя
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userStorage.add(user);

        // Проверяем, что пользователь успешно добавлен
        assertNotNull(addedUser);
        assertEquals("test@example.com", addedUser.getEmail());
        assertEquals("testlogin", addedUser.getLogin());
        assertEquals("Test User", addedUser.getName());
        assertEquals(LocalDate.of(1990, 1, 1), addedUser.getBirthday());
    }

    @Test
    public void testUpdateUser() {
        // Сначала добавляем пользователя
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userStorage.add(user);

        // Изменяем данные пользователя
        addedUser.setEmail("updated@example.com");
        addedUser.setName("Updated User");

        User updatedUser = userStorage.update(addedUser);

        // Проверяем, что данные обновлены
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("Updated User", updatedUser.getName());
    }

    @Test
    public void testGetUserById() {
        // Добавляем нового пользователя
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userStorage.add(user);

        // Получаем пользователя по его ID
        Optional<User> foundUser = userStorage.getById(addedUser.getId());

        // Проверяем, что пользователь найден и данные совпадают
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
        assertEquals("Test User", foundUser.get().getName());
    }

    @Test
    public void testGetAllUsers() {
        // Добавляем нескольких пользователей
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1login");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.add(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2login");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1992, 2, 2));
        userStorage.add(user2);

        // Получаем всех пользователей
        Collection<User> users = userStorage.getAll();

        // Проверяем, что вернулись все добавленные пользователи
        assertNotNull(users);
        assertEquals(2, users.size());

        // Проверяем, что данные пользователей корректны
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("user1@example.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("user2@example.com")));
    }

    @Test
    public void testDeleteUser() {
        // Добавляем пользователя
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userStorage.add(user);

        // Удаляем пользователя
        userStorage.delete(addedUser.getId());

        // Проверяем, что пользователя больше нет
        Optional<User> deletedUser = userStorage.getById(addedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

}

