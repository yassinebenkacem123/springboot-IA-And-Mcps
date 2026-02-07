package com.mcp.mcpServer.services;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;

import com.mcp.mcpServer.models.User;
import com.mcp.mcpServer.payload.UserDTO;

public interface TheUserService {

    ResponseEntity<?> addUser(UserDTO user);

    ResponseEntity<?> deleteUser(Long id);

    ResponseEntity<?> updateUser(Long id, UserDTO userDTO);

    ResponseEntity<?> getAllUsers();

    ResponseEntity<?> getUser(Long id);

    ResponseEntity<?> getUserByName(String username);

}
