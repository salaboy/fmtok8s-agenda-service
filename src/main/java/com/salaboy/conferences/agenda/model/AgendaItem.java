package com.salaboy.conferences.agenda.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Objects;

public record AgendaItem(String id, Proposal proposal, String title, String author, String day, String time,
                         int version) {

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
