package com.salaboy.conferences.agenda.rest;

import com.salaboy.conferences.agenda.rest.repository.AgendaItemRepository;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0")
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public abstract class ContractVerifierBase {

    @LocalServerPort
    int port;

    @MockBean
    AgendaItemRepository agendaItemRepository;

    @BeforeAll
    public void setup() {
        log.info("Setup ContractVerifierBase");
        MockitoAnnotations.openMocks(this);
        RestAssured.baseURI = "http://localhost:" + this.port;
    }


}