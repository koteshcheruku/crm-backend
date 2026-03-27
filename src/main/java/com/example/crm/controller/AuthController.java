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
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    // ─── Invite Flow ────────────────────────────────────────────────────────────

    /**
     * POST /api/auth/invite  (Admin-only, secured via SecurityConfig)
     * Creates a partial user with a UUID invite token and returns the invite link.
     * Body: { "username", "fullname", "email", "role" }
     */
    @PostMapping("/api/auth/invite")
    public ResponseEntity<?> createInvite(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String username = body.get("username");
        String fullname = body.get("fullname");
        String roleStr = body.get("role");

        if (email == null || roleStr == null) {
            return ResponseEntity.badRequest().body("email and role are required");
        }
        if (usersRepo.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("A user with that email already exists");
        }

        RoleModel role = roleRepo.findByName(roleStr.toUpperCase()).orElse(null);
        if (role == null) {
            return ResponseEntity.badRequest().body("Invalid role: " + roleStr);
        }

        String token = UUID.randomUUID().toString();
        UsersModel user = new UsersModel();
        user.setEmail(email);
        user.setUsername(username != null ? username : email.split("@")[0]);
        user.setFullname(fullname != null ? fullname : "");
        user.setRole(role);
        user.setInviteToken(token);
        user.setApprovalStatus("PENDING");
        user.setStatus("Inactive");
        user.setPassword("__INVITE_PLACEHOLDER__");
        user.setJoinedDate(LocalDate.now());
        usersRepo.save(user);

        Map<String, String> result = new HashMap<>();
        result.put("inviteToken", token);
        result.put("inviteLink", "http://localhost:5173/invite/" + token);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/auth/invite/{token}  (public)
     * Returns partial user info for pre-filling the invite form.
     */
    @GetMapping("/api/auth/invite/{token}")
    public ResponseEntity<?> getInviteInfo(@PathVariable String token) {
        Optional<UsersModel> opt = usersRepo.findByInviteToken(token);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid or expired invite link");
        }
        UsersModel u = opt.get();
        Map<String, Object> info = new HashMap<>();
        info.put("username", u.getUsername());
        info.put("fullname", u.getFullname());
        info.put("email", u.getEmail());
        info.put("role", u.getRole() != null ? u.getRole().getName() : null);
        return ResponseEntity.ok(info);
    }

    /**
     * POST /api/auth/register/{token}  (public)
     * Accepts { "password" }, sets password, clears invite token, marks PENDING.
     */
    @PostMapping("/api/auth/register/{token}")
    public ResponseEntity<?> registerViaInvite(@PathVariable String token,
                                               @RequestBody Map<String, String> body) {
        Optional<UsersModel> opt = usersRepo.findByInviteToken(token);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid or expired invite link");
        }
        String password = body.get("password");
        if (password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters");
        }
        UsersModel user = opt.get();
        user.setPassword(passwordEncoder.encode(password));
        user.setInviteToken(null);
        user.setApprovalStatus("PENDING");
        usersRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "Registration successful. Awaiting admin approval."));
    }
}
