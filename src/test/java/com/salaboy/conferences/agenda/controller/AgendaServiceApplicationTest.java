package com.salaboy.conferences.agenda.controller;

import com.salaboy.conferences.agenda.AgendaServiceApplication;
import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.util.AgendaItemCreator;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;


import java.io.IOException;

import static com.salaboy.conferences.agenda.util.AgendaItemCreator.MONDAY;
import static com.salaboy.conferences.agenda.util.AgendaItemCreator.TUESDAY;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = AgendaServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "events.enabled=false")
public class AgendaServiceApplicationTest {



    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    public void beforeEach(){
        deleteAll()
                .expectStatus()
                .isOk()
                .expectBody(Void.class)
                .returnResult().getResponseBody();
    }

    @AfterAll
    static void clean() {
        try {
            mockWebServer.shutdown();
        } catch( IOException e){
            e.printStackTrace();
        }
    }

    @Autowired
    private WebTestClient webTestClient;


   


    @Test
    public void getAll_ShouldReturnsAll() {
        AgendaItem agendaItemMonday = AgendaItemCreator.validMonday();
        AgendaItem agendaItemTuesday = AgendaItemCreator.validTuesday();
        createAgendaItem(agendaItemMonday);
        createAgendaItem(agendaItemTuesday);

        var responseBody = getAll()
                .expectStatus()
                .isOk()
                .expectBodyList(AgendaItem.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).hasSize(2).extracting(AgendaItem::day)
                .containsExactlyInAnyOrder(MONDAY, TUESDAY);
        assertThat(responseBody.get(1).id()).isNotNull();
        assertThat(responseBody.get(0).id()).isNotNull();

    }

    @Test
    public void newAgendaItem_shouldBeCreateANewAgendaItem() {

        // arrange
        var agendaItem = AgendaItemCreator.validMonday();

        // action, assert
        var responseBody = createAgendaItem(agendaItem)
                .expectStatus()
                .isCreated()
                .expectBody(AgendaItem.class)
                .returnResult()
                .getResponseBody();


        assertThat(responseBody.id()).isNotNull();
        assertThat(responseBody.title()).isEqualTo(agendaItem.title());
        assertThat(responseBody.proposal()).isEqualTo(agendaItem.proposal());
        assertThat(responseBody.author()).isEqualTo(agendaItem.author());
        assertThat(responseBody.day()).isEqualTo(agendaItem.day());
        assertThat(responseBody.time()).isEqualTo(agendaItem.time());
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
    public void getAgendaItemHighlights_shouldReturnMax4Items() {


        for( int i = 0; i < 5; i ++) { // Let's create 5 and check the highlights
            // action, assert
            var responseBody = createAgendaItem(AgendaItemCreator.validMonday())
                    .expectStatus()
                    .isCreated()
                    .expectBody(AgendaItem.class)
                    .returnResult()
                    .getResponseBody();
            assertThat(responseBody).isNotNull();
        }

        var responseBody = getHighlights()
                .expectStatus()
                .isOk()
                .expectBodyList(AgendaItem.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).hasSize(4);
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
        var agendaItem = AgendaItemCreator.validMonday();
        var otherAgendaItem = AgendaItemCreator.validTuesday();
        
        createAgendaItem(agendaItem);
        createAgendaItem(otherAgendaItem);

        var responseBody = webTestClient.get()
                .uri("/day/" + MONDAY)
                .exchange()
                .expectBodyList(AgendaItem.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).first().isNotNull();

        responseBody.stream().forEach(i -> {
            assertThat(i.day()).isEqualTo(AgendaItemCreator.MONDAY);
        });
    }

    @Test
    public void getById_ShouldReturnsTheAgendaItemById() {

        createAgendaItem(AgendaItemCreator.validMonday());

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

    private WebTestClient.ResponseSpec deleteAllAgendaItems() {
        return webTestClient.delete().uri("/").exchange();
    }

    private WebTestClient.ResponseSpec getHighlights() {
        return webTestClient.get().uri("/highlights").exchange();
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
