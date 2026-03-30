package com.example.crm.service;

import com.example.crm.Dto.UserPrincipal;
import com.example.crm.Enum.InOut;
import com.example.crm.Model.RoleModel;
import com.example.crm.Model.UsersLogsModel;
import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.RoleRepo;
import com.example.crm.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsersModel isAuthenticated(String email, String password) {
        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        assert principal != null;
        return principal.getEmployee();
    }

    public ResponseEntity<?> authMeService(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        assert principal != null;
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("role", principal.getEmployee().getRole().getName().trim());
        userDetails.put("username", principal.getEmployee().getUsername());
        userDetails.put("email", principal.getEmployee().getEmail());
        userDetails.put("id", principal.getEmployee().getId());
        UsersLogsModel log = new UsersLogsModel();
        log.setEmployeeId(principal.getEmployee());
        log.setLoggedInDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        log.setInOut(InOut.ONLINE);
        return ResponseEntity.ok(userDetails);
    }

    public ResponseEntity<?> createInvite(Map<String, String> body) {
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
        result.put("inviteLink", "https://crm-backend-xsl9.onrender.com/invite/" + token);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> getInviteInfo(String token) {
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

    public ResponseEntity<?> registerViaInvite(String token, Map<String, String> body) {
        Optional<UsersModel> opt = usersRepo.findByInviteToken(token);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid or expired invite link");
        }
        String password = body.get("password");
        if (password == null || password.length() < 8) {
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
