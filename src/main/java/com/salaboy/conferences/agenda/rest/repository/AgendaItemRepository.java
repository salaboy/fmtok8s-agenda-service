package com.salaboy.conferences.agenda.rest.repository;

import com.salaboy.conferences.agenda.rest.model.AgendaItem;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AgendaItemRepository extends ReactiveMongoRepository<AgendaItem, String> {

    Flux<AgendaItem> findAllByDay(final String day);
}
