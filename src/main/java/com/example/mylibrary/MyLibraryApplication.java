package com.example.mylibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyLibraryApplication.class, args);
    }

}
