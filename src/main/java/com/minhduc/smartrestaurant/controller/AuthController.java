package com.minhduc.smartrestaurant.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.User;
import com.minhduc.smartrestaurant.domain.request.ReqLoginDTO;
import com.minhduc.smartrestaurant.domain.request.ReqRegisterDTO;
import com.minhduc.smartrestaurant.domain.response.ResCreateUserDTO;
import com.minhduc.smartrestaurant.domain.response.ResLoginDTO;
import com.minhduc.smartrestaurant.domain.response.ResPermissionDTO;
import com.minhduc.smartrestaurant.domain.response.ResLoginDTO.UserGetAccount;
import com.minhduc.smartrestaurant.domain.response.ResLoginDTO.UserLogin;
import com.minhduc.smartrestaurant.service.UserService;
import com.minhduc.smartrestaurant.util.SecurityUtil;
import com.minhduc.smartrestaurant.util.annotation.ApiMessage;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

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

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Set information to SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Format response access_token
        ResLoginDTO res = new ResLoginDTO();
        // Query database để lấy thông tin user sau đó gán vào UserLogin
        User currentUserDB = this.userService.handleGetUserByUsername(loginDTO.getUsername());
        if (currentUserDB != null) {
            res.setUser(this.mapUserLogin(currentUserDB));
        }
        // create access_token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
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

    @GetMapping("/auth/account")
    @ApiMessage("Get user information")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        ResLoginDTO res = new ResLoginDTO();
        // Constructor Inner Class
        UserGetAccount userGetAccount = new UserGetAccount();
        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            userGetAccount.setUser(this.mapUserLogin(currentUserDB));
        }

        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = "refresh_token") String refresh_token)
            throws IdInvalidException {
        // Check token valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // Lưu ý: Double Check: Check user by refresh_token and email => increase
        // security
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token invalid");
        }

        // issue new token/set refresh token as cookies

        // Format response access_token
        ResLoginDTO res = new ResLoginDTO();

        // Query database để lấy thông tin user sau đó gán vào UserLogin
        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            res.setUser(this.mapUserLogin(currentUserDB));
        }

        // create new_access_token
        String new_access_token = this.securityUtil.createAccessToken(email, res);

        res.setAccessToken(new_access_token);

        // create new_refresh_token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

        // Save new_refresh_token to database
        this.userService.updateUserToken(new_refresh_token, email);

        // Create new cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // update refresh token = null in database
        this.userService.updateUserToken(null, email);

        // remove refresh token cookies
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // Cookies will immediately expire
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString()).body(null);
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody ReqRegisterDTO registerDTO)
            throws IdInvalidException {
        // check email exist in database
        boolean isEmailExist = this.userService.isEmailExist(registerDTO.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email " + registerDTO.getEmail() + " đã tồn tại, vui lòng sử dụng email khác.");
        }
        User newUser = this.userService.handleRegister(registerDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.userService.convertToResCreateUserDTO(newUser));
    }

    private UserLogin mapUserLogin(User user) {
        UserLogin userLogin = new UserLogin();
        userLogin.setId(user.getId());
        userLogin.setEmail(user.getEmail());
        userLogin.setName(user.getName());
        userLogin.setRole(this.mapRoleUserLogin(user));
        return userLogin;
    }

    private ResLoginDTO.RoleUserLogin mapRoleUserLogin(User user) {
        if (user.getRole() == null) {
            return null;
        }

        List<ResPermissionDTO> permissions = user.getRole().getPermissions() == null
                ? Collections.emptyList()
                : user.getRole().getPermissions().stream()
                        .map(item -> new ResPermissionDTO(
                                item.getId(),
                                item.getName(),
                                item.getApiPath(),
                                item.getMethod(),
                                item.getModule()))
                        .toList();

        return new ResLoginDTO.RoleUserLogin(user.getRole().getId(), user.getRole().getName(), permissions);
    }
}