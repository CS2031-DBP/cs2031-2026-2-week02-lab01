package org.lab.week02lab01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Configuration
public class Week02Lab01Application {
    // ----------------
    // entrypoint
    // ----------------
    public static void main(String[] args) {
        SpringApplication.run(Week02Lab01Application.class, args);
    }

    // ----------------
    // configuration
    // ----------------
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
