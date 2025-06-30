package com.indexcreationweb.indexcreationweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.indexcreationweb.indexcreationweb")
@EnableAsync
public class IndexcreationwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndexcreationwebApplication.class, args);
    }

}
