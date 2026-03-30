package org.lab.week02lab01;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lab.week02lab01.controller.MovieController;
import org.lab.week02lab01.external.SWMovie;
import org.lab.week02lab01.model.Movie;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class Week02Lab01ApplicationTests {

    @Autowired
    private MovieController controller;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void contextLoads() throws Exception {
        Assertions.assertNotNull(controller);
        Assertions.assertNotNull(ReflectionTestUtils.getField(controller, "movieService"));
        Assertions.assertNotNull(ReflectionTestUtils.getField(ReflectionTestUtils.getField(controller, "movieService"), "movieRepository"));

        var response = controller.getAllMovies();
        Assertions.assertEquals(0, response.getBody().size());
    }

    @Test
    void getsMovieWithoutExternalId() {
        var newMovie = new Movie();
        newMovie.setId(1L);
        newMovie.setTitle("The Shining");
        controller.createMovie(newMovie);

        var response = controller.getMovieById(1L);
        var movie = response.getBody();
        Assertions.assertNotNull(movie);
        Assertions.assertEquals("The Shining", movie.getTitle());
    }

    @Test
    void getsMovieWithExternalId() {
        // NOTE: to demonstrate external APIs need to be mocked
        var swMovie = new SWMovie();
        swMovie.setTitle("XX");
        swMovie.setDirector("The Director X");
        Mockito.when(restTemplate.getForEntity(
                eq("https://swapi.info/api/films/999"),
                eq(SWMovie.class)
        )).thenReturn(ResponseEntity.ok(swMovie));

        var newMovie = new Movie();
        newMovie.setId(1L);
        newMovie.setTitle("Star Wars: A New Hope");
        newMovie.setExternalId(999L);
        controller.createMovie(newMovie);

        var response = controller.getMovieById(1L);
        var movie = response.getBody();
        Assertions.assertNotNull(movie);
        Assertions.assertEquals("Star Wars: A New Hope", movie.getTitle());
        Assertions.assertEquals("The Director X", movie.getDirectorName());
    }
}
