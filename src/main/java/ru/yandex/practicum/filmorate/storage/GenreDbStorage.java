package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("GenreDbStorage")
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, new GenreRowMapper());
    }

    public Optional<Genre> getGenreById(Integer id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new GenreRowMapper(), id);
        if (genres.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(genres.get(0));
    }

}
