package com.nju.server.controller;

import com.nju.server.message.MqProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageController {
    @Autowired
    private MqProvider mqProvider;

    @PostMapping("/push")
    public String pushMsg(@RequestParam("msg") String msg) {
        boolean res = mqProvider.send("myGroup", msg);
        return "yes";
    }
}
