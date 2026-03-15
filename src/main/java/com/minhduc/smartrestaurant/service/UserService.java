package com.minhduc.smartrestaurant.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minhduc.smartrestaurant.domain.Category;
import com.minhduc.smartrestaurant.domain.Role;
import com.minhduc.smartrestaurant.domain.User;
import com.minhduc.smartrestaurant.domain.request.ReqCreateUserDTO;
import com.minhduc.smartrestaurant.domain.request.ReqRegisterDTO;
import com.minhduc.smartrestaurant.domain.response.ResCreateUserDTO;
import com.minhduc.smartrestaurant.domain.response.ResUpdateUserDTO;
import com.minhduc.smartrestaurant.domain.response.ResUserDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public User handleCreateUser(ReqCreateUserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setAge(userDTO.getAge());
        user.setGender(userDTO.getGender());
        user.setAddress(userDTO.getAddress());

        Role role = this.roleService.fetchRoleById(userDTO.getRoleId());
        user.setRole(role != null ? role : null);

        user.setPassword(this.passwordEncoder.encode(userDTO.getPassword()));

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

    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = userRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());
        result.setMeta(meta);
        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        result.setResult(listUser);
        return result;
    }

    public User handleUpdateUser(User userDetails) {
        // Logic to handle user update
        User user = fetchUserById(userDetails.getId());
        if (user != null) {
            user.setAddress(userDetails.getAddress());
            user.setName(userDetails.getName());
            user.setGender(userDetails.getGender());
            user.setAge(userDetails.getAge());
            // check role exist
            if (userDetails.getRole() != null) {
                Role role = this.roleService.fetchRoleById(userDetails.getRole().getId());
                user.setRole(role != null ? role : null);
            }
            user = this.userRepository.save(user);

        }
        return user;
    }

    public void handleDeleteUser(long id) {
        // Logic to handle user deletion
        this.userRepository.deleteById(id);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.RoleUser role = new ResCreateUserDTO.RoleUser();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            res.setRole(role);
        }
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.RoleUser role = new ResUserDTO.RoleUser();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            res.setRole(role);
        }
        return res;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.RoleUser role = new ResUpdateUserDTO.RoleUser();
        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());

            res.setRole(role);
        }
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public User handleRegister(ReqRegisterDTO registerDTO) {
        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setName(registerDTO.getName());
        user.setAge(registerDTO.getAge());
        user.setGender(registerDTO.getGender());
        user.setAddress(registerDTO.getAddress());

        // Hash password
        user.setPassword(this.passwordEncoder.encode(registerDTO.getPassword()));

        Role userRole = this.roleService.findByName("USER");
        if (userRole != null) {
            user.setRole(userRole);
        }

        return this.userRepository.save(user);
    }
}
