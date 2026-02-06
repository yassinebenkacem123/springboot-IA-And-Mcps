package com.mcp.mcpServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mcp.mcpServer.models.Account;

public interface AccountRepo extends JpaRepository<Account, Long> {
    
}
