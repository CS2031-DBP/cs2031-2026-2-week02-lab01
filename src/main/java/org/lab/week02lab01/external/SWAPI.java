package org.lab.week02lab01.external;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SWAPI {
    private final RestTemplate restTemplate;

    public SWAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SWMovie getMovieInfo(long id) throws Exception {
        String url = String.format("https://swapi.info/api/films/%s", id);
        return restTemplate.getForEntity(url, SWMovie.class).getBody();
    }
}
