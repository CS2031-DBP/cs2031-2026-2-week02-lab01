package org.lab.week02lab01.controller;

import org.lab.week02lab01.model.Movie;
import org.lab.week02lab01.service.MovieService;
import org.lab.week02lab01.external.SWAPI;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final MovieService movieService;
    private final SWAPI swapi;

    @Autowired
    public MovieController(MovieService movieService, SWAPI swapi) {
        this.movieService = movieService;
        this.swapi = swapi;
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie newMovie) {
        Integer saved = movieService.createMovie(newMovie);
        return ResponseEntity.status(saved == 1 ? 200 : 500).body(newMovie);
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();

        var index = 0;
        try {
            for (Movie movie : movies) {
                var extId = movie.getExternalId();
                if (extId != null) {
                    var extMovie = this.swapi.getMovieInfo(extId);
                    movie.setDirectorName(extMovie.getDirector());
                }
                index++;
            }
        } catch (Exception e) {
            logger.warn("Error while calling SWAPI for item {}: {}", index, e.getMessage());
        }

        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);

        if (movie != null) {
            var extId = movie.getExternalId();
            if (extId != null) {
                try {
                    var extMovie = this.swapi.getMovieInfo(extId);
                    movie.setDirectorName(extMovie.getDirector());
                } catch (Exception e) {
                    logger.warn("Error while calling SWAPI: {}", e.getMessage());
                }
            }
            return ResponseEntity.ok(movie);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/release-date")
    public ResponseEntity<Movie> updateReleaseDate(@PathVariable Long id, @RequestParam("date") String newDate) {
        Date newReleaseDate = Date.valueOf(newDate);
        Movie updatedMovie = movieService.updateReleaseDate(id, newReleaseDate);

        if (updatedMovie != null) return ResponseEntity.ok(updatedMovie);
        else return ResponseEntity.notFound().build();
    }

}
