package com.example.crm.service;

import com.example.crm.Dto.UsersDto;
import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.UsersRepo;
import com.example.crm.Repo.RoleRepo;
import com.example.crm.Model.RoleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UsersService {
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> getUsersService() {
        List<UsersDto> data = usersRepo.findAll().stream().map(UsersDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(data);
    }

    public ResponseEntity<?> editUserService(Long id, UsersDto data) {
        UsersModel existingUser = usersRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found " + id));

        if (data.getUsername() != null && !Objects.equals(existingUser.getUsername(), data.getUsername()))
            existingUser.setUsername(data.getUsername());
        if (data.getStatus() != null && !Objects.equals(existingUser.getStatus(), data.getStatus()))
            existingUser.setStatus(data.getStatus());
        if (data.getFullname() != null && !Objects.equals(existingUser.getFullname(), data.getFullname()))
            existingUser.setFullname(data.getFullname());
        if (data.getEmail() != null && !Objects.equals(existingUser.getEmail(), data.getEmail()))
            existingUser.setEmail(data.getEmail());
        if (data.getDepartment() != null && !Objects.equals(existingUser.getDepartment(), data.getDepartment()))
            existingUser.setDepartment(data.getDepartment());
        if (data.getMobileNo() <= 6_000_000_000L && existingUser.getMobileNo() != data.getMobileNo())
            existingUser.setMobileNo(data.getMobileNo());
        if (data.getRole() != null && (existingUser.getRole() == null
                || !Objects.equals(existingUser.getRole().getName(), data.getRole()))) {
            RoleModel roleModel = roleRepo.findByName(data.getRole())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            existingUser.setRole(roleModel);
        }
        if (data.getPassword() != null
                && !passwordEncoder.matches(existingUser.getPassword(), passwordEncoder.encode(data.getPassword())))
            existingUser.setPassword(passwordEncoder.encode(data.getPassword()));
        usersRepo.save(existingUser);
        return ResponseEntity.ok("Done");
    }

    public ResponseEntity<?> deleteUserService(Long id) {
        UsersModel existingUser = usersRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found " + id));
        usersRepo.deleteById(id);
        return ResponseEntity.ok("Done");

    }

    public ResponseEntity<?> createNewUserService(UsersDto data) {
        if (!usersRepo.existsByEmail(data.getEmail())) {
            UsersModel emp = new UsersModel();
            emp.setJoinedDate(LocalDate.now());
            emp.setUsername(data.getUsername());
            emp.setEmail(data.getEmail());
            if (data.getRole() != null) {
                RoleModel roleModel = roleRepo.findByName(data.getRole())
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                emp.setRole(roleModel);
            }
            emp.setFullname(data.getFullname());
            emp.setStatus("Active");
            emp.setMobileNo(data.getMobileNo());
            emp.setPassword(passwordEncoder.encode(data.getPassword()));
            usersRepo.save(emp);
            return ResponseEntity.ok("Done");
        }
        return ResponseEntity.badRequest().body("User already exists");

    }

    public ResponseEntity<?> pendingUser() {
        List<UsersDto> pending = usersRepo.findAll().stream()
                .filter(u -> "PENDING".equals(u.getApprovalStatus()))
                .map(UsersDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pending);
    }

    public ResponseEntity<?> approveUser(Long id) {
        UsersModel user = usersRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setApprovalStatus("APPROVED");
        user.setStatus("Active");
        usersRepo.save(user);
        return ResponseEntity.ok("User approved successfully");
    }

    public ResponseEntity<?> rejectUser(Long id) {
        usersRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        usersRepo.deleteById(id);
        return ResponseEntity.ok("User rejected and removed");
    }
}
