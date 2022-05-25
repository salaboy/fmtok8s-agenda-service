package com.salaboy.conferences.agenda.util;

import com.salaboy.conferences.agenda.model.Proposal;
import com.salaboy.conferences.agenda.model.AgendaItem;

public class AgendaItemCreator {

    public static final String DAY = "2020-10-05";
    public static final String OTHER_DAY = "2020-10-02";

    public static AgendaItem validWithDefaultDay() {
        return new AgendaItem(new Proposal(), "Title", "Author", DAY, "13:00");
    }

    public static AgendaItem otherValidWithDefaultDay() {
        return new AgendaItem(new Proposal(), "Other title", "Other Author", DAY, "13:00");
    }

    public static AgendaItem withFail() {
        return new AgendaItem(new Proposal(),"Title fail", "Author fail",  OTHER_DAY, "12:30");
    }
}
