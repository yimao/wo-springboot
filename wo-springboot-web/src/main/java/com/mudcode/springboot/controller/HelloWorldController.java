package com.mudcode.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class HelloWorldController {

    @RequestMapping("/helloworld")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("helloworld");
        view.addObject("uuid", UUID.randomUUID().toString());
        return view;
    }

}
