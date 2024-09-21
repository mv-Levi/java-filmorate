package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.*;

public class InMemoryMpaRatingDbStorage extends MpaRatingDbStorage {
    private final Map<Long, MpaRating> mpaRatings = new HashMap<>();

    public InMemoryMpaRatingDbStorage() {
        super(null);
        mpaRatings.put(1L, new MpaRating(1L, "G"));
        mpaRatings.put(2L, new MpaRating(2L, "PG"));
        mpaRatings.put(3L, new MpaRating(3L, "PG-13"));
        mpaRatings.put(4L, new MpaRating(4L, "R"));
        mpaRatings.put(5L, new MpaRating(5L, "NC-17"));
    }

    @Override
    public Collection<MpaRating> getAllMpaRatings() {
        return mpaRatings.values();
    }

    @Override
    public Optional<MpaRating> getMpaRatingById(Long id) {
        return Optional.ofNullable(mpaRatings.get(id));
    }
}
