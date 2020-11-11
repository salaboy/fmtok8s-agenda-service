package com.salaboy.conferences.agenda.rest.controller;

import com.salaboy.conferences.agenda.rest.model.AgendaItem;
import com.salaboy.conferences.agenda.rest.repository.AgendaItemRepository;
import com.salaboy.conferences.agenda.rest.service.AgendaItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

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
        return agendaItemRepository.findAll();
    }

    @GetMapping("/day/{day}")
    public Mono<Set<AgendaItem>> getAllByDay(@PathVariable(value = "day", required = true) final String day) {
        log.info("> REST ENDPOINT INVOKED for Getting Agenda Items by Day: " + day);
        return agendaItemRepository.findAllByDay(day).collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public Mono<AgendaItem> getById(@PathVariable("id") String id) {
        log.info("> REST ENDPOINT INVOKED for Getting Agenda Item by Id: " + id);
        return agendaItemRepository.findById(id);
    }

    @DeleteMapping("/")
    public Mono<Void> clearAgendaItems() {
        log.info("> Deleting all Agenda Items");
        return agendaItemRepository.deleteAll();
    }

}
