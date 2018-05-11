package com.lonntec.domainservice.adminportal.controller;

import com.alibaba.fastjson.JSONObject;
import com.lonntec.domainservice.adminportal.proxy.AuthSystemProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.benchem.framework.annotation.RequestTokenValidate;

import java.util.Calendar;
import java.util.Date;

@CrossOrigin
@RequestMapping("/auth")
@RestController
public class AuthManagerController {
    @Autowired
    AuthSystemProxy authSystem;

    @RequestMapping("/time")
    public Date getTime(){
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    @RequestMapping("/login")
    public String login(@RequestBody JSONObject formData){
        String token =authSystem.login(formData);
        return token;
    }

    @RequestTokenValidate
    @RequestMapping("/logout")
    public void logout(@RequestBody JSONObject formData){
        authSystem.logout(formData);
    }

    @RequestTokenValidate
    @RequestMapping("/getuser")
    public JSONObject getUser() {
        return authSystem.getUser();
    }
}
