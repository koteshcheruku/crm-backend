package com.example.crm.service;

import com.example.crm.Dto.UserPrincipal;
import com.example.crm.Enum.InOut;
import com.example.crm.Model.UsersLogsModel;
import com.example.crm.Model.UsersModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;
    @Autowired
    UserDetailsService userDetailsService;

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
}
