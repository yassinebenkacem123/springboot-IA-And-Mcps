package com.mcp.mcpServer.services;

import org.springframework.http.ResponseEntity;

public interface AccountService {

    ResponseEntity<?> createAccount(Long userId);
    
}
