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
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader;
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AgendaItemService {

    private static final Logger log = LoggerFactory.getLogger(AgendaItemService.class);

    @Value("${EVENTS_ENABLED:false}")
    private Boolean eventsEnabled;

    private final WebClient.Builder rest;
    private final AgendaItemRepository agendaItemRepository;

    @Value("${K_SINK:http://broker-ingress.knative-eventing.svc.cluster.local/default/default}")
    private String K_SINK;

    private ObjectMapper objectMapper = new ObjectMapper();

    public AgendaItemService(AgendaItemRepository agendaItemRepository, WebClient.Builder rest) {
        this.agendaItemRepository = agendaItemRepository;
        this.rest = rest;
    }

    public Flux<AgendaItem> getAll() {
        return agendaItemRepository.findAll();
    }

    public Flux<AgendaItem> getByDay(String day) {
        return agendaItemRepository.findByDay(day);
    }

    public Mono<AgendaItem> getById(String id) {
        return agendaItemRepository.findById(id);
    }

    public Mono<Void> deleteAll() {
        return agendaItemRepository.deleteAll();
    }

    public Flux<AgendaItem> getHighlights() {
        return agendaItemRepository.highlights();
    }

    @Configuration
    public static class CloudEventHandlerConfiguration implements CodecCustomizer {

        @Override
        public void customize(CodecConfigurer configurer) {
            configurer.customCodecs().register(new CloudEventHttpMessageReader());
            configurer.customCodecs().register(new CloudEventHttpMessageWriter());
        }

    }


    public Mono<AgendaItem> createAgendaItem(AgendaItem agendaItem) {
        log.info("> New Agenda Item Received: " + agendaItem);
        if (Pattern.compile(Pattern.quote("fail"), Pattern.CASE_INSENSITIVE).matcher(agendaItem.title()).find()) {
            log.error(">> Something went wrong, it seems on purpose :)");
            throw new IllegalStateException("Something went wrong with adding the Agenda Item: " + agendaItem);
        }

        log.info("\t eventsEnabled: " + eventsEnabled);
        return agendaItemRepository.save(agendaItem)
                .doOnSuccess(ai -> emitCloudEventForAgendaItemAdded(ai));

    }

    private String writeValueAsString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Error when serializing Score", ex);
        }
    }

    private Mono<AgendaItem> emitCloudEventForAgendaItemAdded(AgendaItem agendaItem) {
        if (eventsEnabled) {
            CloudEventBuilder cloudEventBuilder = CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withType("Agenda.ItemCreated")
                    .withSource(URI.create("agenda-service.default.svc.cluster.local"))
                    .withData(writeValueAsString(agendaItem).getBytes(StandardCharsets.UTF_8))
                    .withDataContentType("application/json; charset=UTF-8")
                    .withSubject(agendaItem.title());

            CloudEvent cloudEvent = cloudEventBuilder.build();

            logCloudEvent(cloudEvent);

            log.info("Producing CloudEvent with AgendaItem: " + agendaItem);

            return rest.baseUrl(K_SINK).filter(logRequest()).build()
                    .post().bodyValue(cloudEvent)
                    .retrieve()
                    .toBodilessEntity()
                    .doOnSuccess(result -> log.info("Published event. Id: {}, type: {}, agendaItemId: {}.", cloudEvent.getId(), cloudEvent.getType(), agendaItem.getId()))
                    .doOnError(result -> log.error("Error publishing event. Cause: {}. Message: {}", result.getCause(), result.getMessage()))
                    .thenReturn(agendaItem);
        }
        return Mono.just(agendaItem);

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