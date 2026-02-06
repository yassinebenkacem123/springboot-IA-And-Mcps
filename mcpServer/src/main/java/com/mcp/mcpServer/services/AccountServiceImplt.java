package com.mcp.mcpServer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mcp.mcpServer.repositories.AccountRepo;
import com.mcp.mcpServer.repositories.UserRepo;

@Service
public class AccountServiceImplt implements AccountService {

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;
    @Override
    public ResponseEntity<?> createAccount(Long userId ) {
        return new ResponseEntity<>("User account implemtion....",HttpStatus.CREATED);
    }
}
