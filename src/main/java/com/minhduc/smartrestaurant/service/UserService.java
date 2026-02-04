package com.minhduc.smartrestaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.User;
import com.minhduc.smartrestaurant.domain.dto.Meta;
import com.minhduc.smartrestaurant.domain.dto.ResultPaginationDTO;
import com.minhduc.smartrestaurant.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        // Logic to handle user creation
        return this.userRepository.save(user);
    }

    public User fetchUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public ResultPaginationDTO fetchAllUsers(Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageUser.getNumber());
        meta.setPageSize(pageUser.getSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageUser.getContent());

        return result;
    }

    public User handleUpdateUser(User userDetails) {
        // Logic to handle user update
        User user = fetchUserById(userDetails.getId());
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user = this.userRepository.save(user);

        return user;
    }

    public void handleDeleteUser(long id) {
        // Logic to handle user deletion
        this.userRepository.deleteById(id);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
