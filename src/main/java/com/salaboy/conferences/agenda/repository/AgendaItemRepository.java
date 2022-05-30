package com.salaboy.conferences.agenda.repository;

import com.salaboy.conferences.agenda.model.AgendaItem;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaItemRepository extends CrudRepository<AgendaItem, String> {
    Iterable<AgendaItem> findAllByDay(String day);
}
