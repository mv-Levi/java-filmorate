package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
<<<<<<< Updated upstream
=======
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
>>>>>>> Stashed changes

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private FilmController filmController;
<<<<<<< Updated upstream

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
=======
    private FilmStorage filmStorage;
    private FilmService filmService;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmStorage, filmService);
>>>>>>> Stashed changes
    }

    @Test
    void testCreateFilmWithEmptyName() {
        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> {
            filmController.create(film);
        });

        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    void testCreateFilmWithTooLongDescription() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> {
            filmController.create(film);
        });

        assertEquals("Описание не может быть длиннее 200 символов", exception.getMessage());
    }

    @Test
    void testCreateFilmWithInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(120);

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> {
            filmController.create(film);
        });

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void testCreateFilmWithNonPositiveDuration() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> {
            filmController.create(film);
        });

        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void testCreateValidFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmController.create(film);
        assertEquals(1, createdFilm.getId());
        assertEquals("Valid Film", createdFilm.getName());
        assertEquals("Description", createdFilm.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), createdFilm.getReleaseDate());
        assertEquals(120, createdFilm.getDuration());
    }
}
