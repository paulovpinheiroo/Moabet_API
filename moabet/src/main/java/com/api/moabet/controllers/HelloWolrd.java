package com.api.moabet.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWolrd {
    @GetMapping
    public String hello() {
        return "Hello, World!";
    }
}
