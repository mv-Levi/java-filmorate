package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("MpaRatingDbStorage")
public class MpaRatingDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<MpaRating> getAllMpaRatings() {
        String sql = "SELECT * FROM mpa_ratings";
        return jdbcTemplate.query(sql, new MpaRatingRowMapper());
    }

    public Optional<MpaRating> getMpaRatingById(Long id) {
        String sql = "SELECT * FROM mpa_ratings WHERE rating_id = ?";
        List<MpaRating> ratings = jdbcTemplate.query(sql, new MpaRatingRowMapper(), id);
        if (ratings.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ratings.get(0));
    }

    private static class MpaRatingRowMapper implements RowMapper<MpaRating> {
        @Override
        public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MpaRating(rs.getLong("rating_id"), rs.getString("name"));
        }
    }
}
