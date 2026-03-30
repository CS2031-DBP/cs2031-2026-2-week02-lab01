package org.lab.week02lab01.service;

import org.lab.week02lab01.model.Movie;
import org.lab.week02lab01.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Integer createMovie(Movie movie) {
        if (movie != null && movie.getTitle() != null && !movie.getTitle().isEmpty())
            return movieRepository.save(movie);
        else
            throw new IllegalArgumentException("Movie title cannot be null or empty");
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public Movie updateReleaseDate(Long id, java.util.Date newReleaseDate) {
        return movieRepository.updateReleaseDate(id, newReleaseDate);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

}
