package com.lonntec.domainservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages = {
        "team.benchem",
        "com.lonntec"
})
public class Program {
    public static void main(String[] args) {
        SpringApplication.run(Program.class, args);
    }
}
