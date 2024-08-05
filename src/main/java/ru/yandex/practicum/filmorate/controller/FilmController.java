package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получение всех фильмов");
        return filmService.getAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Создание нового фильма: {}", film);
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Обновление фильма: {}", newFilm);
        return filmService.update(newFilm);
    }

    @PutMapping("{id}/like/{userId}")
    public void add(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь с id: {} ставит лайк фильму с id: {}", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void remove(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь с id: {} удаляет лайк с фильма с id: {}", userId, id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable long id) {
        log.info("Получение фильма с id: {}", id);
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(@RequestParam(defaultValue = "10") int size) {
        log.info("Получение {} самых популярных фильмов", size);
        return filmService.getMostPopular(size);
    }

}
