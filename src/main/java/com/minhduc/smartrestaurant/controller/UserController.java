package com.minhduc.smartrestaurant.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.User;
import com.minhduc.smartrestaurant.domain.response.ResCreateUserDTO;
import com.minhduc.smartrestaurant.domain.response.ResUpdateUserDTO;
import com.minhduc.smartrestaurant.domain.response.ResUserDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.service.UserService;
import com.minhduc.smartrestaurant.util.annotation.ApiMessage;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User userRequest) throws IdInvalidException {
        // check email exist in database
        boolean isEmailExist = this.userService.isEmailExist(userRequest.getEmail());

        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email " + userRequest.getEmail() + " đã tồn tại, vui lòng sử dụng email khác.");
        }
        String hashPasword = this.passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(hashPasword);
        User newUser = this.userService.handleCreateUser(userRequest);
        ResCreateUserDTO resCreateUserDTO = this.userService.convertToResCreateUserDTO(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(resCreateUserDTO);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get user by ID")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.fetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        ResUserDTO resUserDTO = this.userService.convertToResUserDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(resUserDTO);
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.ok(this.userService.fetchAllUsers(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User requestUser) throws IdInvalidException {
        User fetchUserById = this.userService.fetchUserById(requestUser.getId());
        if (fetchUserById == null) {
            throw new IdInvalidException("User với id = " + requestUser.getId() + " không tồn tại");
        }

        User userUpdate = this.userService.handleUpdateUser(requestUser);

        ResUpdateUserDTO res = this.userService.convertToResUpdateUserDTO(userUpdate);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        this.userService.handleDeleteUser(id);
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }
}
