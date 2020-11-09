package com.salaboy.conferences.agenda.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Proposal {
    private String id;
    private String author;
    private String title;
    private Date talkTime;

    public Proposal() {
    }

    public Proposal(String id, String author, String title, Date talkTime) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.talkTime = talkTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTalkTime() {
        return talkTime;
    }

    public void setTalkTime(Date talkTime) {
        this.talkTime = talkTime;
    }

    @Override
    public String toString() {
        return "Proposal{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", talkTime=" + talkTime +
                '}';
    }
}
