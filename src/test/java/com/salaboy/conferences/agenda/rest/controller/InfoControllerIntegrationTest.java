package com.salaboy.conferences.agenda.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class InfoControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getInfo_whenGetInfo_thenReturnServiceInfo() {
        webTestClient
                .get()
                .uri("/info")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name", "Agenda Service (REST)");
    }
}