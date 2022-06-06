package com.salaboy.conferences.agenda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;

public record AgendaItem(String id, Proposal proposal, String title, String author, String day, String time,
                         @JsonIgnore int version) {

    @Id
    public String getId() {
        return id;
    }


    public AgendaItem withId(String id) {
        return new AgendaItem(id, proposal(), title(), author(), day(), time(), version());
    }

    public AgendaItem withVersion(int version) {
        return new AgendaItem(id(), proposal(), title(), author(), day(), time(), version);
    }

}
