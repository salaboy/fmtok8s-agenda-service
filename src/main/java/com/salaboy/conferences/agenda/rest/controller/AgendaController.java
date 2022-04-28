package com.salaboy.conferences.agenda.rest.controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.salaboy.conferences.agenda.rest.model.AgendaItem;
import com.salaboy.conferences.agenda.rest.repository.AgendaItemRepository;
import com.salaboy.conferences.agenda.rest.service.AgendaItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    public String newAgendaItem(@RequestBody AgendaItem agendaItem) {
        log.info("> REST ENDPOINT INVOKED for Creating a new Agenda Item");
        return agendaItemService.createAgendaItem(agendaItem);
    }

    @GetMapping
    public Iterable<AgendaItem> getAll() {
        log.info("> REST ENDPOINT INVOKED for Getting All Agenda Items");
        return agendaItemRepository.findAll();
    }

    @GetMapping("/highlights")
    public Iterable<AgendaItem> getHighlights() {
        List<AgendaItem> highlights = new ArrayList<>();
        Iterable<AgendaItem> all = agendaItemRepository.findAll();
        all.forEach(highlights::add);
        Collections.shuffle(highlights);
        if( highlights.size() > 4) {
            return highlights.subList(0, 3);
        }else{
            return highlights;
        }
    }

    @GetMapping("/day/{day}")
    public Iterable<AgendaItem> getAllByDay(@PathVariable(value = "day", required = true) final String day) {
        log.info("> REST ENDPOINT INVOKED for Getting Agenda Items by Day: " + day);
        return agendaItemRepository.findAllByDay(day);
    }

    @GetMapping("/{id}")
    public Optional<AgendaItem> getById(@PathVariable("id") String id) {
        log.info("> REST ENDPOINT INVOKED for Getting Agenda Item by Id: " + id);
        return agendaItemRepository.findById(id);
    }

    @DeleteMapping("/")
    public void clearAgendaItems() {
        log.info("> Deleting all Agenda Items");
        agendaItemRepository.deleteAll();
    }

}
