package com.salaboy.conferences.agenda.util;

import com.salaboy.conferences.agenda.model.Proposal;
import com.salaboy.conferences.agenda.model.AgendaItem;

import java.util.UUID;

public class AgendaItemCreator {

    public static final String DAY = "2020-10-05";
    public static final String OTHER_DAY = "2020-10-02";

    public static AgendaItem validWithDefaultDay() {
        return new AgendaItem(null, new Proposal(UUID.randomUUID().toString(), "Author", "Title"), "Title", "Author", DAY, "13:00", 0);
    }

    public static AgendaItem otherValidWithDefaultDay() {
        return new AgendaItem(null, new Proposal(UUID.randomUUID().toString(), "Author", "Title"), "Other title", "Other Author", DAY, "13:00", 0);
    }

    public static AgendaItem withFail() {
        return new AgendaItem(null, new Proposal(UUID.randomUUID().toString(), "Author", "Title"),"Title fail", "Author fail",  OTHER_DAY, "12:30", 0);
    }
}
