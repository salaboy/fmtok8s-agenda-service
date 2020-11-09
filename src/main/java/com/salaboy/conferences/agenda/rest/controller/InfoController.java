package com.salaboy.conferences.agenda.rest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class InfoController {

    @Value("${version:0.0.0}")
    private String version;

    @GetMapping("/info")
    public String getInfo() {
        return "{ \"name\" : \"Agenda Service (REST)\", \"version\" : \"v" + version + "\", \"source\": \"https://github.com/salaboy/fmtok8s-agenda-rest/releases/tag/v" + version + "\" }";
    }
}
