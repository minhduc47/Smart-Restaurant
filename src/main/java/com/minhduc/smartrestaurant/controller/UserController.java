package com.minhduc.smartrestaurant.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.User;
import com.minhduc.smartrestaurant.service.UserService;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public String createUser() {
        User user = new User();
        user.setEmail("fmgduc79@gmail.com");
        user.setName("Minh Duc");
        user.setPassword("password123");
        this.userService.handleCreateUser(user);
        return "Create User";
    }
}
