package com.salaboy.conferences.agenda.rest.controller;


import com.salaboy.conferences.agenda.rest.model.AgendaItem;
import com.salaboy.conferences.agenda.rest.repository.AgendaItemRepository;
import com.salaboy.conferences.agenda.rest.service.AgendaItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequestMapping
public class AgendaController {

    private final AgendaItemRepository agendaItemRepository;
    private final AgendaItemService agendaItemService;

    public AgendaController(
            final AgendaItemRepository agendaItemRepository,
            final AgendaItemService agendaItemService) {

        this.agendaItemRepository = agendaItemRepository;
        this.agendaItemService = agendaItemService;
    }

    @PostMapping
    public Mono<String> newAgendaItem(@RequestBody AgendaItem agendaItem) {
        log.info("> REST ENDPOINT INVOKED for Creating a new Agenda Item");
        return agendaItemService.createAgendaItem(agendaItem);
    }

    @GetMapping
    public Flux<AgendaItem> getAll() {
        log.info("> REST ENDPOINT INVOKED for Getting All Agenda Items");
        return Flux.fromIterable(agendaItemRepository.findAll());
    }

    @GetMapping("/highlights")
    public Flux<AgendaItem> getHighlights() {
        final List<AgendaItem> highlights = new ArrayList<>();
        var all = agendaItemRepository.findAll();
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
        return Flux.fromIterable(agendaItemRepository.findAllByDay(day));
    }

    @GetMapping("/{id}")
    public Mono<AgendaItem> getById(@PathVariable("id") String id) {
        log.info("> REST ENDPOINT INVOKED for Getting Agenda Item by Id: " + id);
        return Mono.justOrEmpty(agendaItemRepository.findById(id));
    }

    @DeleteMapping("/")
    public void clearAgendaItems() {
        log.info("> Deleting all Agenda Items");
        agendaItemRepository.deleteAll();
    }

}
