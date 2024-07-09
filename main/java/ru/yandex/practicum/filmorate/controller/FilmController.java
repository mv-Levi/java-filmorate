package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов.");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Не удалось создать фильм: название не может быть пустым.");
            throw new ConditionsNotMetException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Не удалось создать фильм: описание не может быть длиннее 200 символов.");
            throw new ConditionsNotMetException("Описание не может быть длиннее 200 символов");
        }
        if (film.getReleaseDate() != null &&
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Не удалось создать фильм: дата релиза не может быть раньше 28 декабря 1895 года.");
            throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Не удалось создать фильм: продолжительность фильма должна быть положительным числом.");
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с ID {} успешно создан.", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Не удалось обновить фильм: ID не должен быть пустым.");
            throw new ConditionsNotMetException("Id не должен быть пустым");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                log.error("Не удалось обновить фильм: название не может быть пустым.");
                throw new ConditionsNotMetException("Название не может быть пустым");
            }
            if (newFilm.getDescription() != null && newFilm.getDescription().length() > 200) {
                log.error("Не удалось обновить фильм: описание не может быть длиннее 200 символов.");
                throw new ConditionsNotMetException("Описание не может быть длиннее 200 символов");
            }
            if (newFilm.getReleaseDate() != null &&
                    newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.error("Не удалось обновить фильм: дата релиза не может быть раньше 28 декабря 1895 года.");
                throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
            if (newFilm.getDuration() <= 0) {
                log.error("Не удалось обновить фильм: продолжительность фильма должна быть положительным числом.");
                throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
            }

            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм с ID {} успешно обновлен.", newFilm.getId());
            return oldFilm;
        }
        log.error("Не удалось обновить фильм: фильм с ID {} не найден.", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
