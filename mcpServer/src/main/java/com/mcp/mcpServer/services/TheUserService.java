package com.mcp.mcpServer.services;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;

import com.mcp.mcpServer.models.User;

public interface TheUserService {
    

    ResponseEntity<?> addUser(User user);

    ResponseEntity<?> deleteUser(Long id);

    ResponseEntity<?> updateUser(Long id, User user);

    @Nullable
    Object getAllUsers();

    ResponseEntity<?> getUser(Long id);
    
}
