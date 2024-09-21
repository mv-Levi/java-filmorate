package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaRatingDbStorage mpaStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("GenreDbStorage") GenreDbStorage genreStorage,
                       @Qualifier("MpaRatingDbStorage") MpaRatingDbStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }


    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film add(Film film) {
        validateFilm(film);
        setFilmGenresAndMpa(film);
        return filmStorage.add(film);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза не может быть пустой");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }



    }

    public Film update(Film film) {
        setFilmGenresAndMpa(film);
        return filmStorage.update(film);
    }

    public Film getById(long id) {
        return findFilmByIdOrThrow(id);
    }

    public void addLike(long filmId, long userId) {
        log.info("Пользователь с id: {} ставит лайк фильму с id: {}", userId, filmId);
        filmStorage.likeFilm(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        log.info("Пользователь с id: {} удаляет лайк с фильма с id: {}", userId, filmId);
        filmStorage.unlikeFilm(filmId, userId);
    }

    public List<Film> getMostPopular(int size) {
        log.info("Получение {} самых популярных фильмов", size);
        if (size <= 0) {
            throw new ConditionsNotMetException("Количество популярных фильмов должно быть положительным");
        }
        return filmStorage.getPopular(size);
    }

    private Film findFilmByIdOrThrow(long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id: %d не найден", id)));
    }

    private User findUserByIdOrThrow(long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", id)));
    }

    private void setFilmGenresAndMpa(Film film) {
        if (film.getMpaRating() != null) {
            Long mpaId = film.getMpaRating().getId();
            if (mpaId == null) {
                throw new ValidationException("MPA рейтинг не может иметь пустой id");
            }
            Optional<MpaRating> mpaOpt = mpaStorage.getMpaRatingById(mpaId);
            if (mpaOpt.isEmpty()) {
                throw new ValidationException("MPA рейтинг с id " + mpaId + " не найден");
            }
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() == null) {
                    throw new ValidationException("Жанр не может иметь пустой id");
                }
                Optional<Genre> genreOpt = genreStorage.getGenreById(genre.getId());
                if (genreOpt.isEmpty()) {
                    throw new ValidationException("Жанр с id " + genre.getId() + " не найден");
                }
            }
        }
    }
}
