package com.salaboy.conferences.agenda;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisProperties {
    private int redisPort;
    private String redisHost;
    private String redisPassword;


    public RedisProperties(
            @Value("${spring.redis.port:6379}") int redisPort,
            @Value("${spring.redis.host:localhost}") String redisHost,
            @Value("${spring.redis.password:}") String redisPassword) {
        this.redisPort = redisPort;
        this.redisHost = redisHost;
        this.redisPassword = redisPassword;
    }


    // getters

    public int getRedisPort() {
        return redisPort;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public String getRedisPassword() {
        return redisPassword;
    }
}
