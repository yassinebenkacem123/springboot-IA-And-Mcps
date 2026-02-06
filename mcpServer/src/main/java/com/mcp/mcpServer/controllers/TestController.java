package com.mcp.mcpServer.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
    @GetMapping("/test")
    public ResponseEntity<?> testEndPoint(){
        Map<String ,String> obj = new HashMap<>();
        obj.put("Message", "Server is running...");
        return new ResponseEntity<>(obj,HttpStatus.OK);
    }
}
