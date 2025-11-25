package org.idea.fithub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class FithubApplication {
    public static void main(String[] args) {
        SpringApplication.run(FithubApplication.class, args);
    }
}