package org.semanpix.kafka.connect.mail.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        System.setProperty("server.servlet.context-path", "/imap-kafka-connect-api");

        SpringApplication.run(Application.class, args);

    }

}
