package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Collection<Film> getAll();

    Optional<Film> getById(long id);

    void delete(long id);

    List<Film> getPopular(int count);

    // Методы для управления лайками
    void likeFilm(long filmId, long userId);
    void unlikeFilm(long filmId, long userId);
}
