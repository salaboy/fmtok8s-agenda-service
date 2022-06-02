package com.salaboy.conferences.agenda.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Proposal(String id, String author, String title) {

}
