package com.example.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/guest")
    public String guest(){
        return "guest";
    }

    @GetMapping("/prem")
    public String prem(){
        return "prem";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }
}
