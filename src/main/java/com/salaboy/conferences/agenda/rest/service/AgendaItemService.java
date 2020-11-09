package com.salaboy.conferences.agenda.rest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salaboy.cloudevents.helper.CloudEventsHelper;
import com.salaboy.conferences.agenda.rest.model.AgendaItem;
import com.salaboy.conferences.agenda.rest.model.Proposal;
import com.salaboy.conferences.agenda.rest.repository.AgendaItemRepository;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.zeebe.cloudevents.ZeebeCloudEventsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AgendaItemService {

    @Value("${EVENTS_ENABLED:true}")
    private Boolean eventsEnabled;

    @Value("${K_SINK:http://broker-ingress.knative-eventing.svc.cluster.local/default/default}")
    private String K_SINK;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final AgendaItemRepository agendaItemRepository;

    public AgendaItemService(AgendaItemRepository agendaItemRepository) {
        this.agendaItemRepository = agendaItemRepository;
    }

    public Mono<String> createAgenda(AgendaItem agendaItem)  {


        if (Pattern.compile(Pattern.quote("fail"), Pattern.CASE_INSENSITIVE).matcher(agendaItem.getTitle()).find()) {
            throw new IllegalStateException("Something went wrong with adding the Agenda Item: " + agendaItem);
        }




        return agendaItemRepository.save(agendaItem)
                .doOnSuccess(savedAgendaItem -> {
                    log.info("> Agenda Item Added to Agenda: {}", savedAgendaItem);
                    log.info("\t eventsEnabled: " + eventsEnabled);

                    if(eventsEnabled) {
                        Proposal proposal = new Proposal(agendaItem.getProposalId(), agendaItem.getAuthor(), agendaItem.getTitle(), new Date());
                        String proposalString = null;
                        try {
                            proposalString = objectMapper.writeValueAsString(proposal);
                            proposalString = objectMapper.writeValueAsString(proposalString); //needs double quoted ??
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        CloudEventBuilder cloudEventBuilder = CloudEventBuilder.v1()
                                .withId(UUID.randomUUID().toString())
                                .withTime(OffsetDateTime.now().toZonedDateTime()) // bug-> https://github.com/cloudevents/sdk-java/issues/200
                                .withType("Agenda.ItemCreated")
                                .withSource(URI.create("agenda-service.default.svc.cluster.local"))
                                .withData(proposalString.getBytes())
                                .withDataContentType("application/json")
                                .withSubject(savedAgendaItem.getTitle());

                        CloudEvent zeebeCloudEvent = ZeebeCloudEventsHelper
                                .buildZeebeCloudEvent(cloudEventBuilder)
                                .withCorrelationKey(proposal.getId()).build();

                        logCloudEvent(zeebeCloudEvent);
                        WebClient webClient = WebClient.builder().baseUrl(K_SINK).filter(logRequest()).build();

                        WebClient.ResponseSpec postCloudEvent = CloudEventsHelper.createPostCloudEvent(webClient, zeebeCloudEvent);

                        postCloudEvent.bodyToMono(String.class)
                                .doOnError(t -> t.printStackTrace())
                                .doOnSuccess(s -> log.info("Cloud Event Posted to K_SINK -> " + K_SINK + ": Result: " +  s))
                                .subscribe();
                    }
                })
                .doOnError(i -> log.info("> Agenda Item NOT Added to Agenda: {}", i))
                .map(i -> !Strings.isBlank(i.getId()) ? "Agenda Item Added to Agenda" : "Agenda Item Not Added");

    }

    private void logCloudEvent(CloudEvent cloudEvent) {
        EventFormat format = EventFormatProvider
                .getInstance()
                .resolveFormat(JsonFormat.CONTENT_TYPE);

        log.info("Cloud Event: " + new String(format.serialize(cloudEvent)));

    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: " + clientRequest.method() + " - " + clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info(name + "=" + value)));
            return Mono.just(clientRequest);
        });
    }
}