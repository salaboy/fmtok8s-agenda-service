package com.salaboy.conferences.agenda.rest.repository;

import com.salaboy.conferences.agenda.rest.model.AgendaItem;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaItemRepository extends CrudRepository<AgendaItem, String> {

    Iterable<AgendaItem> findAllByDay(String day);
}
