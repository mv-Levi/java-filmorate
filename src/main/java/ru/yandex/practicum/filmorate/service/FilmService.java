package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getById(long id) {
        return findFilmByIdOrThrow(id);
    }

    public void addLike(long filmId, long userId) {
        Film film = findFilmByIdOrThrow(filmId);
        User user = findUserByIdOrThrow(userId);
        if (film.getLikes().contains(userId)) {
            throw new ConditionsNotMetException("Пользователь уже поставил лайк этому фильму");
        }
        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, filmId);
        film.getLikes().add(userId);
        filmStorage.update(film);
    }

    public void removeLike(long filmId, long userId) {
        Film film = findFilmByIdOrThrow(filmId);
        User user = findUserByIdOrThrow(userId);

        if (!film.getLikes().contains(userId)) {
            throw new ConditionsNotMetException("Пользователь не ставил лайк этому фильму");
        }

        log.info("Пользователь с id: {} удалил лайк с фильма с id: {}", userId, filmId);
        film.getLikes().remove(userId);
        filmStorage.update(film);
    }

    public List<Film> getMostPopular(int size) {
        log.info("Получение {} самых популярных фильмов", size);
        return filmStorage.getAll().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(size)
                .collect(Collectors.toList());
    }

    private Film findFilmByIdOrThrow(long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id: %d не найден", id)));
    }

    private User findUserByIdOrThrow(long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", id)));
    }
}
