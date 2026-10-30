package com.devkit.hsd.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class RestTestController {

    @GetMapping("/secure/user")
    public String secureEndPoint(){
        return "secure endpoint çalıştı";
    }

    @GetMapping("/secure/admin")
    public String secureEndPointAdmin(){
        return "secure endpoint çalıştı";
    }

}


