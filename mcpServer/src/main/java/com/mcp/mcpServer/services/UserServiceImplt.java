package com.mcp.mcpServer.services;

import org.jspecify.annotations.Nullable;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mcp.mcpServer.models.User;
import com.mcp.mcpServer.repositories.UserRepo;

@Service
public class UserServiceImplt implements TheUserService {

    @Autowired
    private UserRepo userRepo;

    
    @Override
    public ResponseEntity<?> addUser(User user) {
        try {
            User savedUser = userRepo.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> deleteUser(Long id) {
        try {
            if (!userRepo.existsById(id)) {
                return ResponseEntity.status(404).body("User not found with id: " + id);
            }
            userRepo.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting user: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> updateUser(Long id, User user) {
        try {
            if (!userRepo.existsById(id)) {
                return ResponseEntity.status(404).body("User not found with id: " + id);
            }
            user.setUserId(id); 
            User updatedUser = userRepo.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating user: " + e.getMessage());
        }
    }

    @Override
    @McpTool(name="get all users", description = "get all users information.")
    public @Nullable Object getAllUsers() {
        try {
            return userRepo.findAll();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving users: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getUser(Long id) {
        try {
            return ResponseEntity.ok(userRepo.findById(id).orElseThrow(()-> new RuntimeException("User not found with id: " + id)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving user: " + e.getMessage());
        }
    }
}
