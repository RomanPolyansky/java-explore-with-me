package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class StatsServer {
    public static void main(String[] args) {
        log.info("Starting Statistics Server");
        SpringApplication.run(StatsServer.class, args);
        log.info("Statistics Server is Running");
    }
}
