package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private FilmController filmController;
    private FilmStorage filmStorage;
    private FilmService filmService;
    private UserStorage userStorage;

    private GenreDbStorage genreStorage;

    private MpaRatingDbStorage mpaStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        genreStorage = new InMemoryGenreDbStorage();
        mpaStorage = new InMemoryMpaRatingDbStorage();
        filmService = new FilmService(filmStorage, userStorage, genreStorage, mpaStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    void testCreateFilmWithEmptyName() {
        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void testCreateFilmWithTooLongDescription() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Exception exception = assertThrows(ValidationException.class, () -> {
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

        Exception exception = assertThrows(ValidationException.class, () -> {
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

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void testCreateValidFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(1L);
        film.setMpaRating(mpaRating);

        Film createdFilm = filmService.add(film);
        assertEquals(1L, createdFilm.getMpaRating().getId());
    }
}
