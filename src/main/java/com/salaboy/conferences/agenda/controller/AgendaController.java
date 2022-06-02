package com.salaboy.conferences.agenda.controller;


import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.service.AgendaItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping
public class AgendaController {

    private static final Logger log = LoggerFactory.getLogger(AgendaController.class);
    private final AgendaItemService agendaItemService;

    public AgendaController(
            final AgendaItemService agendaItemService) {

        this.agendaItemService = agendaItemService;
    }

    @PostMapping
    public Mono<AgendaItem> newAgendaItem(@RequestBody AgendaItem agendaItem) {
        log.info("> REST ENDPOINT INVOKED for Creating a new Agenda Item");
        return agendaItemService.createAgendaItem(agendaItem);
    }

    @GetMapping
    public Flux<AgendaItem> getAll() {
        log.info("> REST ENDPOINT INVOKED for Getting All Agenda Items");
        return agendaItemService.getAll();
    }

    @GetMapping("/highlights")
    public Flux<AgendaItem> getHighlights() {
        List<AgendaItem> highlights = new ArrayList<>();
        Iterable<AgendaItem> all = agendaItemService.getAll().toIterable();
        all.forEach(highlights::add);
        Collections.shuffle(highlights);
        if (highlights.size() > 4) {
            return Flux.fromIterable(highlights.subList(0, 3));
        } else {
            return Flux.fromIterable(highlights);
        }
    }

    @GetMapping("/day/{day}")
    public Flux<AgendaItem> getAllByDay(@PathVariable(value = "day", required = true) final String day) {
        log.info("> REST ENDPOINT INVOKED for Getting Agenda Items by Day: " + day);
        return agendaItemService.getByDay(day);
    }

    @GetMapping("/{id}")
    public Mono<AgendaItem> getById(@PathVariable("id") String id) {
        log.info("> REST ENDPOINT INVOKED for Getting Agenda Item by Id: " + id);
        return agendaItemService.getById(id);
    }

    @DeleteMapping("/")
    public Mono<Void> clearAgendaItems() {
        log.info("> Deleting all Agenda Items");
        return agendaItemService.deleteAll();
    }

}
