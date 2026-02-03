package com.minhduc.smartrestaurant.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.User;
import com.minhduc.smartrestaurant.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User userRequest) {
        User newUser = this.userService.handleCreateUser(userRequest);
        return newUser;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") long id) {
        return this.userService.fetchUserById(id);
    }

    @GetMapping("/users")
    public List<User> getAllUser() {
        return this.userService.fetchAllUsers();
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        // TODO: process PUT request
        User updateUser = this.userService.handleUpdateUser(user);
        return updateUser;
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable("id") long id) {
        this.userService.handleDeleteUser(id);
    }
}
