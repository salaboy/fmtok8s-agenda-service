package com.salaboy.conferences.agenda.repository;

import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.model.Proposal;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class AgendaItemRepositoryImpl implements AgendaItemRepository {
    private final static String KEY = "AGENDAITEMS";

    private final ReactiveRedisOperations<String, AgendaItem> redisOperations;
    private final ReactiveHashOperations<String, String, AgendaItem> hashOperations;

    @Autowired
    public AgendaItemRepositoryImpl(ReactiveRedisOperations<String, AgendaItem> redisOperations) {
        this.redisOperations = redisOperations;
        this.hashOperations = redisOperations.opsForHash();
    }

    @Override
    public Mono<AgendaItem> findById(String id) {
        return hashOperations.get(KEY, id);
    }

    @Override
    public Flux<AgendaItem> findAll() {
        return hashOperations.values(KEY);
    }

    @Override
    public Mono<AgendaItem> save(AgendaItem agendaItem) {
        if(agendaItem.title().isEmpty() || agendaItem.proposal() == null)
            return Mono.error(new IllegalArgumentException("Cannot be saved: title and proposal are required, but one or both is empty."))
                    .thenReturn(agendaItem);

        if (agendaItem.getId() == null || agendaItem.getId().isEmpty()) {
            String userId = UUID.randomUUID().toString().replaceAll("-", "");

            return Mono.defer(() -> addOrUpdateAgendaItem(agendaItem.withId(userId).withVersion(0), existsByTitle(agendaItem.title())
                    .mergeWith(existsByProposal(agendaItem.proposal()))
                    .any(b -> b)));
        } else {
            return findById(agendaItem.getId())
                    .flatMap(ai -> {
                        if (ai.version() != agendaItem.version()) {
                            return Mono.error(
                                    new OptimisticLockingFailureException(
                                            "This record has already been updated earlier by another object."));
                        } else {
                            agendaItem.withVersion(agendaItem.version() + 1);

                            return Mono.defer(() -> {
                                Mono<Boolean> exists = Mono.just(false);

                                if (!ai.title().equals(agendaItem.title())) {
                                    exists = existsByTitle(agendaItem.title());
                                }
                                if (!ai.proposal().equals(agendaItem.proposal())) {
                                    exists = existsByProposal(agendaItem.proposal());
                                }

                                return addOrUpdateAgendaItem(agendaItem, exists);
                            });
                        }
                    })
                    .switchIfEmpty(Mono.defer(() -> addOrUpdateAgendaItem(agendaItem, existsByTitle(agendaItem.title())
                            .mergeWith(existsByProposal(agendaItem.proposal()))
                            .any(b -> b))));
        }
    }



    @Override
    public Flux<AgendaItem> findByDay(String day) {
        return hashOperations.values(KEY)
                .filter(ai -> ai.day().equals(day));
    }





    @Override
    public Mono<Long> count() {
        return hashOperations.values(KEY).count();
    }

    @Override
    public Mono<Void> deleteAll() {
        return hashOperations.delete(KEY).then();
    }

    @Override
    public Mono<Void> delete(AgendaItem agendaItem) {
        return hashOperations.remove(KEY, agendaItem.getId()).then();
    }

    @Override
    public Mono<Void> deleteAllById(Iterable<? extends String> strings) {
        return null;
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return hashOperations.remove(KEY, id).then();
    }


    //Others... Implements the following methods for your business logic

    @Override
    public <S extends AgendaItem> Flux<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public <S extends AgendaItem> Flux<S> saveAll(Publisher<S> publisher) {
        return null;
    }

    @Override
    public Mono<AgendaItem> findById(Publisher<String> publisher) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(Publisher<String> publisher) {
        return null;
    }

    @Override
    public Flux<AgendaItem> findAllById(Iterable<String> iterable) {
        return null;
    }

    @Override
    public Flux<AgendaItem> findAllById(Publisher<String> publisher) {
        return null;
    }

    @Override
    public Mono<Void> deleteById(Publisher<String> publisher) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Iterable<? extends AgendaItem> iterable) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends AgendaItem> publisher) {
        return null;
    }


    // private utility method to add new user if not exist with username and email
    private Mono<AgendaItem> addOrUpdateAgendaItem(AgendaItem agendaItem, Mono<Boolean> exists) {
        return exists.flatMap(exist -> {
                    if (exist) {
                        return Mono.error(new DuplicateKeyException("Duplicate key, Title: " +
                                agendaItem.title() + " or proposal: " + agendaItem.proposal() + " exists."));
                    } else {
                        return hashOperations.put(KEY, agendaItem.getId(), agendaItem)
                                .map(isSaved -> agendaItem);
                    }
                })
                .thenReturn(agendaItem);
    }

    @Override
    public Mono<AgendaItem> findByTitle(String title) {
        return hashOperations.values(KEY)
                .filter(ai -> ai.title().equals(title))
                .singleOrEmpty();
    }

    @Override
    public Mono<AgendaItem> findByProposal(Proposal proposal) {
        return hashOperations.values(KEY)
                .filter(ai -> ai.proposal().equals(proposal))
                .singleOrEmpty();
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return hashOperations.hasKey(KEY, id);
    }

    @Override
    public Mono<Boolean> existsByTitle(String title) {
        return findByTitle(title)
                .hasElement();
    }

    @Override
    public Mono<Boolean> existsByProposal(Proposal proposal) {
        return findByProposal(proposal)
                .hasElement();
    }
}
