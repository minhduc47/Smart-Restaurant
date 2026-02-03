package com.minhduc.smartrestaurant.service;

import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.User;
import com.minhduc.smartrestaurant.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handleCreateUser(User user) {
        // Logic to handle user creation
        this.userRepository.save(user);
    }
}
