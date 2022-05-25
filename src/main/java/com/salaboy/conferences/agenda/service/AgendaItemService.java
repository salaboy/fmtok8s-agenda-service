package com.salaboy.conferences.agenda.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.repository.AgendaItemRepository;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader;
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AgendaItemService {

    @Value("${EVENTS_ENABLED:false}")
    private Boolean eventsEnabled;

    @Autowired
    private WebClient.Builder rest;

    @Value("${K_SINK:http://broker-ingress.knative-eventing.svc.cluster.local/default/default}")
    private String K_SINK;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final AgendaItemRepository agendaItemRepository;

    @Configuration
    public static class CloudEventHandlerConfiguration implements CodecCustomizer {

        @Override
        public void customize(CodecConfigurer configurer) {
            configurer.customCodecs().register(new CloudEventHttpMessageReader());
            configurer.customCodecs().register(new CloudEventHttpMessageWriter());
        }

    }

    public AgendaItemService(AgendaItemRepository agendaItemRepository) {
        this.agendaItemRepository = agendaItemRepository;
    }

    public String createAgendaItem(AgendaItem agendaItem) {
        log.info("> New Agenda Item Received: " + agendaItem);
        if (Pattern.compile(Pattern.quote("fail"), Pattern.CASE_INSENSITIVE).matcher(agendaItem.getTitle()).find()) {
            log.error(">> Something went wrong, it seems on purpose :)");
            throw new IllegalStateException("Something went wrong with adding the Agenda Item: " + agendaItem);
        }

        AgendaItem savedAgendaItem = agendaItemRepository.save(agendaItem);

        log.info("> Agenda Item Added to Agenda: {}", savedAgendaItem);
        log.info("\t eventsEnabled: " + eventsEnabled);

        if(eventsEnabled) {
            try {
                emitCloudEventForAgendaItemAdded(savedAgendaItem);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return "Agenda Item Added to Agenda";

    }

    private void emitCloudEventForAgendaItemAdded(AgendaItem agendaItem) throws JsonProcessingException {

        CloudEventBuilder cloudEventBuilder = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType("Agenda.ItemCreated")
                .withSource(URI.create("agenda-service.default.svc.cluster.local"))
                .withData(objectMapper.writeValueAsString(agendaItem).getBytes(StandardCharsets.UTF_8))
                .withDataContentType("application/json; charset=UTF-8")
                .withSubject(agendaItem.getTitle());

        CloudEvent cloudEvent = cloudEventBuilder.build();

        logCloudEvent(cloudEvent);

        log.info("Producing CloudEvent with AgendaItem: " + agendaItem);

        rest.baseUrl(K_SINK).filter(logRequest()).build()
                .post().bodyValue(cloudEvent)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(t -> t.printStackTrace())
                .doOnSuccess(s -> log.info("Result -> " + s)).subscribe();
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