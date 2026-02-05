package com.minhduc.smartrestaurant.controller;

import java.security.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.User;
import com.minhduc.smartrestaurant.domain.dto.LoginDTO;
import com.minhduc.smartrestaurant.domain.dto.ResLoginDTO;
import com.minhduc.smartrestaurant.domain.dto.ResLoginDTO.UserLogin;
import com.minhduc.smartrestaurant.service.UserService;
import com.minhduc.smartrestaurant.util.SecurityUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Value("${minhduc.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.securityUtil = securityUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // create access_token
        String access_token = this.securityUtil.createAccessToken(authentication);
        // Set information to SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Format response access_token
        ResLoginDTO res = new ResLoginDTO();
        // Query database để lấy thông tin user sau đó gán vào UserLogin
        User currentUserDB = this.userService.handleGetUserByUsername(loginDTO.getUsername());
        if (currentUserDB != null) {
            // Set Data into Inner class: UserLogin
            UserLogin resUserLogin = res.new UserLogin();

            resUserLogin.setId(currentUserDB.getId());
            resUserLogin.setEmail(currentUserDB.getEmail());
            resUserLogin.setName(currentUserDB.getName());

            res.setUser(resUserLogin);
        }
        res.setAccessToken(access_token);

        // create refresh_token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);

        // Save refresh_token to database
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        // Create cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(res);
    }
}