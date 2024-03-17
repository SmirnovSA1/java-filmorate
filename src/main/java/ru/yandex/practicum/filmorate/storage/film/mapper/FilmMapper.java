package ru.yandex.practicum.filmorate.storage.film.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
public class FilmMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("film_description"))
                .releaseDate(rs.getDate("film_release_date").toLocalDate())
                .duration(rs.getInt("film_duration"))
                .mpa(new MPA(rs.getInt("film_mpa_id"),
                        rs.getString("mpa_name"),
                        rs.getInt("mpa_min_age")))
                .build();

        makeFilmGenres(film);
        makeFilmLikes(film);

        return film;
    }

    private void makeFilmGenres(Film film) {
        String query = "SELECT g.* " +
                "FROM genres AS g " +
                "JOIN films_genres AS fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> filmGenres = jdbcTemplate.query(query, new GenreMapper(), film.getId());
        film.setGenres(new HashSet<>(filmGenres));
    }

    private void makeFilmLikes(Film film) {
        String query = "SELECT user_id " +
                "FROM film_likes " +
                "WHERE film_id = ?";
        List<Integer> filmLikes = jdbcTemplate.queryForList(query, Integer.class, film.getId());
        film.setLikes(new HashSet<>(filmLikes));
    }
}
