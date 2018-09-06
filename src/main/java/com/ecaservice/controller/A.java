package com.ecaservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class A {

    @GetMapping(value = "/hello")
    public String hello() {
        return "hi";
    }
}
