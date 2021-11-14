package com.xzj.wiki.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zixi
 * @version 1.0
 * @date 21/11/12 下午 8:35
 */

@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @PostMapping("/hello/post")
    public String hellopost(String name) {
        return "Hello World! Post: " + name;
    }

}
