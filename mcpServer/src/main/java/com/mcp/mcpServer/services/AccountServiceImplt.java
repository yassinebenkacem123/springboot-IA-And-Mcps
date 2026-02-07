package com.mcp.mcpServer.services;

import java.util.HashMap;
import java.util.Map;

import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mcp.mcpServer.models.Account;
import com.mcp.mcpServer.models.User;
import com.mcp.mcpServer.payload.AccountDTO;
import com.mcp.mcpServer.repositories.AccountRepo;
import com.mcp.mcpServer.repositories.UserRepo;

@Service
public class AccountServiceImplt implements AccountService {

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;

    // create account for user
    @Override
    @McpTool(name = "create account", description = "create a new account for a user by user id.")
    public ResponseEntity<?> createAccount(
            @McpArg(name = "userId", description = "The ID of the user for whom the account is being created") Long userId,
            @McpArg(name = "accountDTO", description = "Data transfer object containing account details") AccountDTO accountDTO) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Account account = new Account();
        account.setAccountName(accountDTO.getAccountName());
        account.setDescription(accountDTO.getDescription());
        account.setUser(user);
        accountRepo.save(account);
        return new ResponseEntity<>("Account created", HttpStatus.CREATED);
    }

    @Override
    @McpTool(name = "get account info", description = "get user account information by username.")
    public ResponseEntity<?> getAccountInfo(
            @McpArg(name = "username", description = "The username of the user whose account information is being retrieved") String username) {
        try {
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername() != null ? user.getUsername() : "");
            response.put("email", user.getEmail() != null ? user.getEmail() : "");

            // Create a clean list of accounts without circular references
            var accounts = user.getAccounts();
            if (accounts != null && !accounts.isEmpty()) {
                var accountsList = accounts.stream()
                        .map(account -> {
                            Map<String, Object> accountMap = new HashMap<>();
                            accountMap.put("accountId", account.getAccountId());
                            accountMap.put("accountName", account.getAccountName() != null ? account.getAccountName() : "");
                            accountMap.put("description", account.getDescription() != null ? account.getDescription() : "");
                            accountMap.put("createdAt", account.getCreatedAt() != null ? account.getCreatedAt().toString() : "");
                            return accountMap;
                        })
                        .toList();
                response.put("accounts", accountsList);
            } else {
                response.put("accounts", new java.util.ArrayList<>());
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error retrieving account info: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
