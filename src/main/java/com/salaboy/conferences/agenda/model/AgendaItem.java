package com.salaboy.conferences.agenda.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Objects;

@RedisHash("AgendaItem")
public class AgendaItem {

    @Id
    @Indexed
    private String id;
    private Proposal proposal;
    private String title;
    private String author;
    @Indexed
    private String day;
    private String time;


    protected AgendaItem() {}

    public AgendaItem(Proposal proposal, String title, String author, String day, String time) {
        this.proposal = proposal;
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

    public Proposal getProposal() {
        return proposal;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    @Override
    public String toString() {
        return "AgendaItem{" +
                "id='" + id + '\'' +
                ", proposal=" + proposal +
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
        return Objects.equals(id, that.id) && Objects.equals(proposal, that.proposal) && Objects.equals(title, that.title) && Objects.equals(author, that.author) && Objects.equals(day, that.day) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, proposal, title, author, day, time);
    }
}
