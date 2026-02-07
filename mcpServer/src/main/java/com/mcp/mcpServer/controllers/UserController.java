package com.mcp.mcpServer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcp.mcpServer.models.User;
import com.mcp.mcpServer.payload.UserDTO;
import com.mcp.mcpServer.services.TheUserService;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private TheUserService userService;

    // add new user
    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    // delete user
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    // update user
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    // get all users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return userService.getAllUsers();
    }

    // get user by id
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUser(id);
    }

    // get user by name
    @GetMapping("/user/name/{username}")
    public ResponseEntity<?> getUserByName(@PathVariable String username) {
        return userService.getUserByName(username);
    }

}
