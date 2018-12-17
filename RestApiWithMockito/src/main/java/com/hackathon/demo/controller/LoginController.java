package com.hackathon.demo.controller;

import com.hackathon.demo.entity.UserDetails;
import com.hackathon.demo.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    @PostMapping(value="/loginData",produces = "application/json")
    public List<UserDetails> getUserDetails() {
        System.out.println(" get request for User Details ");

        List<UserDetails> value = loginService.getUserDetails();
        return value;
    }
}
