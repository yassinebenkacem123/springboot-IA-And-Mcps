package com.mcp.mcpServer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcp.mcpServer.services.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AccountController {
    
    @Autowired
    private AccountService accountService;

    // create user account :
    @PostMapping("/createAccount/{userId}")
    public ResponseEntity<?> createAccount(@RequestParam @Valid  Long userId){
        return accountService.createAccount(userId);
    }

    // get user account info :

    // update user account info :

    
}
