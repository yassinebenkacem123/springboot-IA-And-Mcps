package com.mcp.mcpServer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcp.mcpServer.payload.AccountDTO;
import com.mcp.mcpServer.services.AccountService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AccountController {
    
    @Autowired
    private AccountService accountService;

    // create user account :
    @PostMapping("/createAccount/{userId}")
    public ResponseEntity<?> createAccount(@RequestParam @Valid Long userId, @RequestBody AccountDTO accountDTO){
        return accountService.createAccount(userId, accountDTO);
    }

    // get user account info :
    @GetMapping("/getAccountIfo/{username}")
    public ResponseEntity<?> getAccountInfo(@RequestParam String username){
        return accountService.getAccountInfo(username);
    }
    // update user account info :

    
}
