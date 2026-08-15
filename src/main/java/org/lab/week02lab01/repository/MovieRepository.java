package org.lab.week02lab01.repository;

import org.lab.week02lab01.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class MovieRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MovieRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Movie> findAll() {
        String sqlQuery = "SELECT * FROM movies";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
            Movie movie = new Movie();
            movie.setId(resultSet.getLong("id"));
            movie.setTitle(resultSet.getString("title"));
            movie.setReleaseDate(resultSet.getDate("release_date"));
            Long externalId = resultSet.getObject("external_id", Long.class);
            movie.setExternalId(externalId);
            return movie;
        });
    }

    public Movie findById(long id) {
        String sqlQuery = "SELECT * FROM movies WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, (resultSet, rowNum) -> {
                Movie movie = new Movie();
                movie.setId(resultSet.getLong("id"));
                movie.setTitle(resultSet.getString("title"));
                movie.setReleaseDate(resultSet.getDate("release_date"));

                Long externalId = resultSet.getObject("external_id", Long.class);
                movie.setExternalId(externalId);
                return movie;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int save(Movie movie) {
        String sqlQuery = "INSERT INTO movies (title, release_date, external_id) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sqlQuery,
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getExternalId()
        );
    }

    public Movie updateReleaseDate(long id, Date newReleaseDate) {
        String sqlQuery = "UPDATE movies SET release_date = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sqlQuery, newReleaseDate, id);

        if (rowsAffected > 0) {
            return findById(id);
        } else {
            return null;
        }
    }

    public void deleteById(long id) {
        String sqlQuery = "DELETE FROM movies WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

}
