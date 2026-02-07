package com.mcp.mcpServer.services;

import java.util.HashMap;

import org.modelmapper.ModelMapper;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mcp.mcpServer.models.User;
import com.mcp.mcpServer.payload.UserDTO;
import com.mcp.mcpServer.repositories.UserRepo;

@Service
public class UserServiceImplt implements TheUserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @McpTool(name = "add user", description = "add a new user to the system.")
    public ResponseEntity<?> addUser(
            @McpArg(name = "user", description = "The user to add with this credentials (username, useremail, password)") UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        try {
            User savedUser = userRepo.save(user);
            var response = new HashMap<String, Object>();
            response.put("userId", savedUser.getUserId());
            response.put("username", savedUser.getUsername());
            response.put("email", savedUser.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving user: " + e.getMessage());
        }
    }

    @Override
    @McpTool(name = "delete user", description = "delete a user from the system by id.")
    public ResponseEntity<?> deleteUser(
            @McpArg(name = "id", description = "The id of the user to delete") Long id) {
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
    @McpTool(name = "update user", description = "update a user in the system by id.")
    public ResponseEntity<?> updateUser(
            @McpArg(name = "id", description = "The id of the user to update") Long id,
            @McpArg(name = "user", description = "The user object with updated information") UserDTO userDTO) {
        try {
            User user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            User updatedUser = userRepo.save(user);

            var response = new HashMap<String, Object>();
            response.put("userId", updatedUser.getUserId());
            response.put("username", updatedUser.getUsername());
            response.put("email", updatedUser.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating user: " + e.getMessage());
        }

    }

    @Override
    @McpTool(name = "get all users", description = "get all users information.")
    public ResponseEntity<?> getAllUsers() {
        try {
            var users = userRepo.findAll();
            var payload = users.stream()
                    .map(user -> {
                        var userMap = new HashMap<String, Object>();
                        userMap.put("userId", user.getUserId());
                        userMap.put("username", user.getUsername() != null ? user.getUsername() : "");
                        userMap.put("email", user.getEmail() != null ? user.getEmail() : "");

                        // Handle accounts with null descriptions and null collections
                        var accounts = user.getAccounts();
                        if (accounts != null && !accounts.isEmpty()) {
                            var accountsList = accounts.stream()
                                    .map(account -> {
                                        var accountMap = new HashMap<String, Object>();
                                        accountMap.put("accountId", account.getAccountId());
                                        accountMap.put("accountName",
                                                account.getAccountName() != null ? account.getAccountName() : "");
                                        accountMap.put("description",
                                                account.getDescription() != null ? account.getDescription() : "");
                                        accountMap.put("createdAt",
                                                account.getCreatedAt() != null ? account.getCreatedAt().toString()
                                                        : "");
                                        return accountMap;
                                    })
                                    .toList();
                            userMap.put("accounts", accountsList);
                        } else {
                            userMap.put("accounts", new java.util.ArrayList<>());
                        }
                        return userMap;
                    })
                    .toList();
            return ResponseEntity.ok(payload);
        } catch (Exception e) {
            e.printStackTrace();
            var details = (e.getMessage() != null && !e.getMessage().isBlank())
                    ? e.getMessage()
                    : e.getClass().getSimpleName();
            return ResponseEntity.status(500).body("Error retrieving users: " + details);
        }
    }

    @Override
    @McpTool(name = "get user by id", description = "get a user information by id.")
    public ResponseEntity<?> getUser(@McpArg(name = "id", description = "The id of the user to retrieve") Long id) {
        try {
            User user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

            // Create a clean response without circular references
            var response = new HashMap<String, Object>();
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername() != null ? user.getUsername() : "");
            response.put("email", user.getEmail() != null ? user.getEmail() : "");

            // Handle accounts with null descriptions and null collections
            var accounts = user.getAccounts();
            if (accounts != null && !accounts.isEmpty()) {
                var accountsList = accounts.stream()
                        .map(account -> {
                            var accountMap = new HashMap<String, Object>();
                            accountMap.put("accountId", account.getAccountId());
                            accountMap.put("accountName",
                                    account.getAccountName() != null ? account.getAccountName() : "");
                            accountMap.put("description",
                                    account.getDescription() != null ? account.getDescription() : "");
                            accountMap.put("createdAt",
                                    account.getCreatedAt() != null ? account.getCreatedAt().toString() : "");
                            return accountMap;
                        })
                        .toList();
                response.put("accounts", accountsList);
            } else {
                response.put("accounts", new java.util.ArrayList<>());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving user: " + e.getMessage());
        }
    }

    @Override
    @McpTool(name = "get user by name", description = "get a user information by name.")
    public ResponseEntity<?> getUserByName(
            @McpArg(name = "username", description = "The username of the user to retrieve") String username) {
        try {
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

            // Create a clean response without circular references
            var response = new HashMap<String, Object>();
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername() != null ? user.getUsername() : "");
            response.put("email", user.getEmail() != null ? user.getEmail() : "");

            // Handle accounts with null descriptions and null collections
            var accounts = user.getAccounts();
            if (accounts != null && !accounts.isEmpty()) {
                var accountsList = accounts.stream()
                        .map(account -> {
                            var accountMap = new HashMap<String, Object>();
                            accountMap.put("accountId", account.getAccountId());
                            accountMap.put("accountName",
                                    account.getAccountName() != null ? account.getAccountName() : "");
                            accountMap.put("description",
                                    account.getDescription() != null ? account.getDescription() : "");
                            accountMap.put("createdAt",
                                    account.getCreatedAt() != null ? account.getCreatedAt().toString() : "");
                            return accountMap;
                        })
                        .toList();
                response.put("accounts", accountsList);
            } else {
                response.put("accounts", new java.util.ArrayList<>());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving user: " + e.getMessage());
        }
    }
}
