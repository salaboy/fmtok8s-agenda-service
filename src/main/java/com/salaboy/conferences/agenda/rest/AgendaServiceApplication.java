package com.salaboy.conferences.agenda.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
public class AgendaServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(AgendaServiceApplication.class, args);

    }

    @Value("${spring.redis.in-memory}")
    private boolean redisInMemory;



    private RedisServer redisServer;



    @PostConstruct
    public void postConstruct() {
        System.out.println(">> Starting Redis In Memory Cache. ");
        if(redisInMemory) {

            this.redisServer = new RedisServer();
            redisServer.start();

            System.out.println(">> Is Redis active? : " + redisServer.isActive());
        }
    }

    @PreDestroy
    public void preDestroy() {
        if(redisInMemory) {
            redisServer.stop();
        }
    }
}


