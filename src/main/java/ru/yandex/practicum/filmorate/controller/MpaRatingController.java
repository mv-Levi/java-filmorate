package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingDbStorage;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaRatingController {
    private final MpaRatingDbStorage mpaStorage;

    @Autowired
    public MpaRatingController(MpaRatingDbStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping
    public Collection<MpaRating> getAllMpaRatings() {
        return mpaStorage.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MpaRating> getMpaRatingById(@PathVariable Long id) {
        return mpaStorage.getMpaRatingById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
