package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Component
@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Вложенный класс FilmRowMapper для маппинга результата SQL-запросов
    private class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            // Извлечение MPA рейтинга
            Long mpaRatingId = rs.getLong("rating_id");
            String mpaRatingName = rs.getString("mpa_rating");

            MpaRating mpaRating = null;
            if (!rs.wasNull()) {
                mpaRating = new MpaRating(mpaRatingId, mpaRatingName);
            }

            Film film = new Film(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    new HashSet<>(),  // Пустой Set для likes
                    new HashSet<>(),
                    mpaRating
            );

            // Добавляем запрос для получения жанров
            String genreSql = "SELECT g.genre_id, g.name FROM film_genres fg " +
                    "JOIN genres g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
            List<Genre> genres = jdbcTemplate.query(genreSql, new GenreRowMapper(), film.getId());
            film.setGenres(new HashSet<>(genres)); // Устанавливаем жанры

            return film;
        }
    }

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            if (film.getMpaRating() != null) {
                ps.setLong(5, film.getMpaRating().getId());
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        film.setId(generatedId);

        // Сохранение жанров
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(genreSql, film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpaRating() != null ? film.getMpaRating().getId() : null,
                film.getId()
        );

        if (rowsAffected == 0) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        return film;
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT f.*, m.name AS mpa_rating FROM films f LEFT JOIN mpa_ratings m ON f.rating_id = m.rating_id";
        return jdbcTemplate.query(sql, new FilmRowMapper());
    }

    @Override
    public Optional<Film> getById(long id) {
        String sql = "SELECT f.*, m.name AS mpa_rating FROM films f LEFT JOIN mpa_ratings m ON f.rating_id = m.rating_id WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), id);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(films.get(0));
    }

    @Override
    public void delete(long id) {
        // Сначала проверяем, существует ли фильм с данным ID
        String checkSql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);

        if (count == null || count == 0) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }

        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> getPopular(int size) {
        String sql = "SELECT " +
                "f.id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "m.rating_id AS rating_id, " +
                "m.name AS mpa_rating, " +
                "COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.rating_id = m.rating_id " +
                "LEFT JOIN film_likes l ON f.id = l.film_id " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, m.rating_id, m.name " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new FilmRowMapper(), size);
    }

    @Override
    public void likeFilm(long filmId, long userId) {
        // Проверка существования фильма
        String checkFilmSql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer filmCount = jdbcTemplate.queryForObject(checkFilmSql, Integer.class, filmId);

        if (filmCount == null || filmCount == 0) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }

        // Проверка существования пользователя
        String checkUserSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer userCount = jdbcTemplate.queryForObject(checkUserSql, Integer.class, userId);

        if (userCount == null || userCount == 0) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        // Проверка, что лайк уже не существует
        String checkLikeSql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";
        Integer likeCount = jdbcTemplate.queryForObject(checkLikeSql, Integer.class, filmId, userId);

        if (likeCount != null && likeCount > 0) {
            throw new ConditionsNotMetException("Пользователь с id " + userId + " уже поставил лайк фильму с id " + filmId);
        }

        // Добавление лайка
        String insertLikeSql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(insertLikeSql, filmId, userId);
    }

    @Override
    public void unlikeFilm(long filmId, long userId) {
        // Проверка существования фильма
        String checkFilmSql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer filmCount = jdbcTemplate.queryForObject(checkFilmSql, Integer.class, filmId);

        if (filmCount == null || filmCount == 0) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }

        // Проверка существования пользователя
        String checkUserSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer userCount = jdbcTemplate.queryForObject(checkUserSql, Integer.class, userId);

        if (userCount == null || userCount == 0) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        // Проверка, что лайк существует
        String checkLikeSql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";
        Integer likeCount = jdbcTemplate.queryForObject(checkLikeSql, Integer.class, filmId, userId);

        if (likeCount == null || likeCount == 0) {
            throw new ConditionsNotMetException("Пользователь с id " + userId + " не поставил лайк фильму с id " + filmId);
        }

        // Удаление лайка
        String deleteLikeSql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(deleteLikeSql, filmId, userId);
    }

}