package com.salaboy.conferences.agenda.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeDayGeneratorTests {

    @Test
    public void generate_day_ShouldBeNotBeEmpty() {
        var generator = new TimeDayGenerator();
        assertThat(generator.day()).isNotEmpty();
    }

    @Test
    public void generate_day_ContainsMondayOrTuesday() {
        var generator = new TimeDayGenerator();
        var day = generator.day();
        assertThat(TimeDayGenerator.DAYS).contains(day);
    }

    @Test
    public void generate_time_ShouldBeNotEmpty() {
        var generator = new TimeDayGenerator();
        var time = generator.time();
        assertThat(time).isNotEmpty();
    }

    @Test
    public void generate_time_ContainsAValidTime() {
        var generator = new TimeDayGenerator();
        var time = generator.time();
        assertThat(TimeDayGenerator.TIMES).contains(time);
    }
}
