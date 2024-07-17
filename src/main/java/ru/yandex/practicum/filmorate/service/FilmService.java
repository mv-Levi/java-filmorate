package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (film.getLikes().contains(userId)) {
            throw new ConditionsNotMetException("Пользователь уже поставил лайк этому фильму");
        }
        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, filmId);
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (!film.getLikes().contains(userId)) {
            throw new ConditionsNotMetException("Пользователь не ставил лайк этому фильму");
        }

        log.info("Пользователь с id: {} удалил лайк с фильма с id: {}", userId, filmId);
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getMostPopularFilms(int size) {
        log.info("Получение {} самых популярных фильмов", size);
        return filmStorage.getAllFilms().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(size)
                .collect(Collectors.toList());
    }
}
