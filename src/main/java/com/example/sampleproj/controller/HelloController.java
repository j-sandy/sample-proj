package com.example.sampleproj.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    
    @GetMapping("/api/")
    public String home() {
        return "Welcome to Sample Spring Boot Project!";
    }
    
    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return String.format("Hello, %s!", name);
    }
    
    @GetMapping("/health")
    public String health() {
        return "Application is running!";
    }
}