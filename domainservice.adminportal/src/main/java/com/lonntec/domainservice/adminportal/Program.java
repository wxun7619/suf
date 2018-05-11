package com.lonntec.domainservice.adminportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

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
