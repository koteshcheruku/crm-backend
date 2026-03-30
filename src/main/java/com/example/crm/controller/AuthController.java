package com.example.crm.controller;


import com.example.crm.Dto.AuthenticationResponse;
import com.example.crm.Dto.LoginRequest;
import com.example.crm.Model.RoleModel;
import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.UsersRepo;
import com.example.crm.Repo.RoleRepo;
import com.example.crm.service.AuthService;
import com.example.crm.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/api/login")
    public ResponseEntity<?> loginVerification(
            @RequestBody LoginRequest req,
            HttpServletResponse response){
        String logInId = req.getEmail();
        String password = req.getPassword();
        try{
            UsersModel employee = authService.isAuthenticated(logInId, password);

            // Block pending users from logging in
            if ("PENDING".equals(employee.getApprovalStatus())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "pending_approval"));
            }

            String accessToken = jwtService.generateAccessToken(employee);
            String refreshToken = jwtService.generateRefreshToken(employee);

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(new AuthenticationResponse(accessToken, "Log in success"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/api/auth/me")
    public ResponseEntity<?> authMe(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("No auth");
        try {
            return authService.authMeService(authentication);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/api/auth/invite")
    public ResponseEntity<?> createInvite(@RequestBody Map<String, String> body) {
        try {
            return authService.createInvite(body);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create invite");
        }
    }

    @GetMapping("/api/auth/invite/{token}")
    public ResponseEntity<?> getInviteInfo(@PathVariable String token) {
        try {
            return authService.getInviteInfo(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get invite info");
        }
    }

    @PostMapping("/api/auth/register/{token}")
    public ResponseEntity<?> registerViaInvite(@PathVariable String token,
                                               @RequestBody Map<String, String> body) {
        try {
            return authService.registerViaInvite(token,body);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register");
        }
    }
}
