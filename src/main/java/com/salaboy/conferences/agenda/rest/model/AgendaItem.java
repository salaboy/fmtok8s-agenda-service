package com.salaboy.conferences.agenda.rest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
public class AgendaItem {

    @Id
    private String id;
    private String proposalId;
    private String title;
    private String author;
    private String day;
    private String time;


    protected AgendaItem() {}

    public AgendaItem(String proposalId, String title, String author, String day, String time) {
        this.proposalId = proposalId;
        this.title = title;
        this.author = author;
        this.day = day;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    @Override
    public String toString() {
        return "AgendaItem{" +
                "id='" + id + '\'' +
                ", proposalId='" + proposalId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", day='" + day + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgendaItem that = (AgendaItem) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(proposalId, that.proposalId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(author, that.author) &&
                Objects.equals(day, that.day) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, proposalId, title, author, day, time);
    }
}
