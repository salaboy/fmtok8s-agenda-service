package com.salaboy.conferences.agenda.controller;


import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.service.AgendaItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
public class AgendaController {

    private static final Logger log = LoggerFactory.getLogger(AgendaController.class);
    private final AgendaItemService agendaItemService;

    public AgendaController(final AgendaItemService agendaItemService) {
        this.agendaItemService = agendaItemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
        return agendaItemService.getHighlights();
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
