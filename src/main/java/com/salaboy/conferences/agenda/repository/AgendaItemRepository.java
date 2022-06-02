package com.salaboy.conferences.agenda.repository;

import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.model.Proposal;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AgendaItemRepository extends ReactiveCrudRepository<AgendaItem, String> {

    Flux<AgendaItem> findByDay(String day);

    Mono<AgendaItem> findByTitle(String title);

    Mono<AgendaItem> findByProposal(Proposal proposal);

    Mono<Boolean> existsByTitle(String title);

    Mono<Boolean> existsByProposal(Proposal proposal);
}
