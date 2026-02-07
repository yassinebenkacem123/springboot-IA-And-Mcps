package com.mcp.mcpServer.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcp.mcpServer.models.User;


@Repository
public interface UserRepo extends JpaRepository<User, Long>{

    Optional<User> findByUsername(String username);

    
}
