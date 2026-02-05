package com.minhduc.smartrestaurant.controller;

import java.security.Security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.dto.LoginDTO;
import com.minhduc.smartrestaurant.domain.dto.ResLoginDTO;
import com.minhduc.smartrestaurant.util.SecurityUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final SecurityUtil securityUtil;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // create token
        String access_token = this.securityUtil.createToken(authentication);
        // Set information to SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Format response access_token
        ResLoginDTO res = new ResLoginDTO();
        res.setAccessToken(access_token);

        return ResponseEntity.ok().body(res);
    }
}