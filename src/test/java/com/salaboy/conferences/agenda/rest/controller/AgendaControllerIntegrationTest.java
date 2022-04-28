package com.salaboy.conferences.agenda.rest.controller;

import com.salaboy.conferences.agenda.rest.AgendaServiceApplication;
import com.salaboy.conferences.agenda.rest.model.AgendaItem;
import com.salaboy.conferences.agenda.rest.repository.AgendaItemRepository;
import com.salaboy.conferences.agenda.rest.util.AgendaItemCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = AgendaServiceApplication.class)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("dev")
@SpringBootTest(classes = TestRedisConfiguration.class)
public class AgendaControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AgendaItemRepository agendaItemRepository;

//    @Autowired
//    private ReactiveMongoTemplate reactiveMongoTemplate;
//
//    @After
//    public void after() {
//        reactiveMongoTemplate.dropCollection(AgendaItem.class)
//            .subscribe();
//    }




    @Test
    public void getAll_ShouldReturnsAll() {

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
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isEqualTo("Agenda Item Added to Agenda");
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
            assertThat(i.getDay()).isEqualTo(AgendaItemCreator.DAY);
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
