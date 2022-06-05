package com.salaboy.conferences.agenda.util;

import com.salaboy.conferences.agenda.model.Proposal;
import com.salaboy.conferences.agenda.model.AgendaItem;

import java.util.UUID;

public class AgendaItemCreator {

    public static final String MONDAY = "Monday";
    public static final String TUESDAY = "Tuesday";

    public static AgendaItem validMonday() {
        String id = UUID.randomUUID().toString();
        return new AgendaItem(null, new Proposal(id), "Title-" + id, "Author", MONDAY, "13:00", 0);
    }

    public static AgendaItem validTuesday() {
        String id = UUID.randomUUID().toString();
        return new AgendaItem(null, new Proposal(id), "Title-" + id, "Author", TUESDAY, "13:00", 0);
    }

    public static AgendaItem withFail() {
        return new AgendaItem(null, new Proposal(UUID.randomUUID().toString()),"Title fail", "Author fail",  TUESDAY, "12:30", 0);
    }
}
