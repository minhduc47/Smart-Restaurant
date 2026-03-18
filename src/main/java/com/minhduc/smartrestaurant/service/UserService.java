package com.minhduc.smartrestaurant.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.smartrestaurant.domain.Category;
import com.minhduc.smartrestaurant.domain.PasswordResetToken;
import com.minhduc.smartrestaurant.domain.Role;
import com.minhduc.smartrestaurant.domain.User;
import com.minhduc.smartrestaurant.domain.request.ReqCreateUserDTO;
import com.minhduc.smartrestaurant.domain.request.ReqRegisterDTO;
import com.minhduc.smartrestaurant.domain.request.ReqUpdateUserDTO;
import com.minhduc.smartrestaurant.domain.response.ResCreateUserDTO;
import com.minhduc.smartrestaurant.domain.response.ResUpdateUserDTO;
import com.minhduc.smartrestaurant.domain.response.ResUserDTO;
import com.minhduc.smartrestaurant.domain.response.email.ResEmailResetPassword;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.repository.PasswordResetTokenRepository;
import com.minhduc.smartrestaurant.repository.UserRepository;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository,
            RoleService roleService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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

    public User handleUpdateUser(ReqUpdateUserDTO userDetails) {
        Optional<User> userOptional = this.userRepository.findById(userDetails.getId());
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        user.setAddress(userDetails.getAddress());
        user.setName(userDetails.getName());
        user.setGender(userDetails.getGender());
        user.setAge(userDetails.getAge());

        if (userDetails.getRoleId() != null) {
            Role role = this.roleService.fetchRoleById(userDetails.getRoleId());
            user.setRole(role != null ? role : null);
        }

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

    @Transactional
    public void handleForgotPassword(String email) throws IdInvalidException {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new IdInvalidException("Email " + email + " không tồn tại");
        }

        this.passwordResetTokenRepository.deleteByUser(user);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setExpiryDate(Instant.now().plus(15, ChronoUnit.MINUTES));
        passwordResetToken.setUser(user);
        this.passwordResetTokenRepository.save(passwordResetToken);

        ResEmailResetPassword payload = new ResEmailResetPassword();
        payload.setEndpoint("http://localhost:8080/api/v1/auth/reset-password");
        payload.setToken(passwordResetToken.getToken());
        payload.setExpiresInMinutes(15);

        this.emailService.sendEmailFromTemplateSync(
                user.getEmail(),
                "[Smart Restaurant] Yêu cầu đặt lại mật khẩu",
                "reset_password",
                user.getName(),
                payload);
    }

    @Transactional
    public void handleResetPassword(String token, String newPassword) throws IdInvalidException {
        PasswordResetToken passwordResetToken = this.passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IdInvalidException("Token không hợp lệ"));

        if (passwordResetToken.getExpiryDate().isBefore(Instant.now())) {
            this.passwordResetTokenRepository.delete(passwordResetToken);
            throw new IdInvalidException("Token đã hết hạn");
        }

        User user = passwordResetToken.getUser();
        if (user == null) {
            throw new IdInvalidException("Không tìm thấy user cho token này");
        }

        user.setPassword(this.passwordEncoder.encode(newPassword));
        this.userRepository.save(user);
        this.passwordResetTokenRepository.delete(passwordResetToken);
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredPasswordResetTokens() {
        this.passwordResetTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }
}
