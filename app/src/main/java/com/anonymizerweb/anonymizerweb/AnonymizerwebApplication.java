package com.anonymizerweb.anonymizerweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.anonymizerweb.anonymizerweb")
@EnableAsync
public class AnonymizerwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnonymizerwebApplication.class, args);
    }

}
