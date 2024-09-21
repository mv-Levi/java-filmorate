CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
    rating_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    rating_id BIGINT,
    CONSTRAINT fk_rating FOREIGN KEY (rating_id) REFERENCES mpa_ratings (rating_id)
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films (id),
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id BIGINT,
    friend_id BIGINT,
    status VARCHAR(20),
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);
