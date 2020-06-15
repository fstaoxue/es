package com.taoge.es.business.busline.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/upload")
    public String upload(){
        return "es/upload";
    }

    @GetMapping("/mysql")
    public String mysql(){
        return "es/mysql";
    }

    @GetMapping("/box")
    public String box(){
        return "es/box";
    }

    @GetMapping("/head")
    public String head(){
        return "es/head";
    }

}
