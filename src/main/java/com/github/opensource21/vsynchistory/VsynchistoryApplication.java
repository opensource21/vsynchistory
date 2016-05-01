package com.github.opensource21.vsynchistory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VsynchistoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(VsynchistoryApplication.class, args);
    }
}
