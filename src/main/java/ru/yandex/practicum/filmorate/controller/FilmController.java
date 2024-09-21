package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    public ResponseEntity<Film> create(@RequestBody Film film) {
        log.info("Создание нового фильма: {}", film);
        Film createdFilm = filmService.add(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);  // Возвращаем статус 201 Created
    }

    @PutMapping
    public ResponseEntity<Film> update(@RequestBody Film newFilm) {
        log.info("Обновление фильма: {}", newFilm);
        Film updatedFilm = filmService.update(newFilm);
        return ResponseEntity.ok(updatedFilm);  // Возвращаем статус 200 OK
    }

    @PutMapping("{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь с id: {} ставит лайк фильму с id: {}", userId, id);
        try {
            filmService.addLike(id, userId);
            return ResponseEntity.ok().build();  // Возвращаем статус 200 OK
        } catch (NotFoundException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Возвращаем статус 404 Not Found
        }
    }

    @DeleteMapping("{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь с id: {} удаляет лайк с фильма с id: {}", userId, id);
        try {
            filmService.removeLike(id, userId);
            return ResponseEntity.ok().build();  // Возвращаем статус 200 OK
        } catch (NotFoundException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Возвращаем статус 404 Not Found
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> get(@PathVariable long id) {
        log.info("Получение фильма с id: {}", id);
        try {
            Film film = filmService.getById(id);
            return ResponseEntity.ok(film);  // Возвращаем статус 200 OK
        } catch (NotFoundException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Возвращаем статус 404 Not Found
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getMostPopular(@RequestParam(defaultValue = "10") int size) {
        log.info("Получение {} самых популярных фильмов", size);
        List<Film> popularFilms = filmService.getMostPopular(size);
        return ResponseEntity.ok(popularFilms);  // Возвращаем статус 200 OK
    }

}
