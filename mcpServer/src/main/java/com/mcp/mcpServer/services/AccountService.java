package com.mcp.mcpServer.services;

import org.springframework.http.ResponseEntity;

import com.mcp.mcpServer.payload.AccountDTO;

public interface AccountService {

    ResponseEntity<?> createAccount(Long userId, AccountDTO accountDTO);

    ResponseEntity<?> getAccountInfo(String username);
    
}
