package com.salaboy.conferences.agenda.controller;

import com.salaboy.conferences.agenda.AgendaService;
import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.util.AgendaItemCreator;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AgendaService.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Testcontainers
public class AgendaControllerIntegrationTest {

    private static final int REDIS_PORT = 6379;

    @Autowired
    private WebTestClient webTestClient;

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:6"))
            .withExposedPorts(REDIS_PORT);


    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", () -> redis.getHost());
        registry.add("spring.redis.port", () -> redis.getMappedPort(REDIS_PORT));
    }


    @Test
    public void getAll_ShouldReturnsAll() {
        System.out.println("Adding Dynamic Properties: " + redis.getHost() + " port: " + redis.getMappedPort(REDIS_PORT));
        createAgendaItem(AgendaItemCreator.validWithDefaultDay());
        createAgendaItem(AgendaItemCreator.otherValidWithDefaultDay());

        var responseBody = getAll()
                .expectStatus()
                .isOk()
                .expectBodyList(AgendaItem.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).hasSizeGreaterThan(0);
    }

    @Test
    public void newAgendaItem_shouldBeCreateANewAgendaItem() {

        // arrange
        var agendaItem = AgendaItemCreator.validWithDefaultDay();

        // action, assert
        var responseBody = createAgendaItem(agendaItem)
                .expectStatus()
                .isCreated()
                .expectBody(AgendaItem.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isEqualTo(agendaItem);
        assertThat(responseBody.id()).isNotNull();
    }

    @Test
    public void newAgendaItem_shouldThrowsErrorIfTitleContainsFail() {

        // arrange
        var agendaItem = AgendaItemCreator.withFail();

        // action, assert
        var responseBody = createAgendaItem(agendaItem)
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).contains("Internal Server Error");
    }

    @Test
    public void clearAgendaItems_shouldBeClearAllAgendaItems() {

        // arrange
        deleteAll()
                .expectStatus()
                .isOk()
                .expectBody(Void.class)
                .returnResult().getResponseBody();

        // action, assert
        getAll().expectBodyList(AgendaItem.class).hasSize(0);
    }

    @Test
    public void getAllByDay() {

        // arrange
        var agendaItem = AgendaItemCreator.validWithDefaultDay();
        var otherAgendaItem = AgendaItemCreator.otherValidWithDefaultDay();
        
        createAgendaItem(agendaItem);
        createAgendaItem(otherAgendaItem);

        var responseBody = webTestClient.get()
                .uri("/day/" + AgendaItemCreator.DAY)
                .exchange()
                .expectBodyList(AgendaItem.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).first().isNotNull();

        responseBody.stream().forEach(i -> {
            assertThat(i.day()).isEqualTo(AgendaItemCreator.DAY);
        });
    }

    @Test
    public void getById_ShouldReturnsTheAgendaItemById() {

        createAgendaItem(AgendaItemCreator.validWithDefaultDay());

        var responseBody = getAll().expectBodyList(AgendaItem.class).returnResult().getResponseBody();

        var first = responseBody.stream().findFirst();

        assertThat(first.isPresent()).isTrue();

        var agendaItem = webTestClient.get()
                .uri("/" + first.get().getId())
                .exchange()
                .expectBody(AgendaItem.class)
                .returnResult()
                .getResponseBody();

        assertThat(agendaItem).isNotNull();
        assertThat(agendaItem.getId()).isEqualTo(first.get().getId());
    }

    private WebTestClient.ResponseSpec getAll() {
        return webTestClient.get().uri("/").exchange();
    }

    private WebTestClient.ResponseSpec createAgendaItem(final AgendaItem agendaItem) {

        return webTestClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(agendaItem))
                .exchange();
    }

    private WebTestClient.ResponseSpec deleteAll() {
        return webTestClient.delete().uri("/").exchange();
    }

}
