package com.mudcode.springboot.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {

    @RequestMapping(produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("OK");
    }

}
