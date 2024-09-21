package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
public class InMemoryGenreDbStorage extends GenreDbStorage {
    private final Map<Integer, Genre> genres = new HashMap<>();

    public InMemoryGenreDbStorage() {
        super(null);
        genres.put(1, new Genre(1, "Комедия"));
        genres.put(2, new Genre(2, "Драма"));
        genres.put(3, new Genre(3, "Мультфильм"));
        genres.put(4, new Genre(4, "Триллер"));
        genres.put(5, new Genre(5, "Документальный"));
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return genres.values();
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        return Optional.ofNullable(genres.get(id));
    }

}