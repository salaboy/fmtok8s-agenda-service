package com.salaboy.conferences.agenda.service;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TimeDayGenerator {

    public static final String[] DAYS = {"Monday", "Tuesday"};
    public static final String[] TIMES = {"9:00 am", "10:00 am", "11:00 am", "1:00 pm", "2:00 pm", "3:00 pm", "4:00 pm", "5:00 pm"};
    private Random random = new Random();

    public String day() {
        int i = random.nextInt(DAYS.length);
        return DAYS[i];
    }

    public String time() {
        int i = random.nextInt(TIMES.length);
        return TIMES[i];
    }
}
