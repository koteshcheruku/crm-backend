package com.example.crm.controller;

import com.example.crm.Dto.RoleDto;
import com.example.crm.Model.RoleModel;
import com.example.crm.Repo.RoleRepo;
import com.example.crm.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UsersRepo usersRepo;

    @GetMapping
    public ResponseEntity<List<RoleDto>> getRoles() {
        List<RoleDto> rolesWithCounts = roleRepo.findAll().stream().map(role -> {
            long count = usersRepo.findAll().stream()
                    .filter(u -> u.getRole() != null && u.getRole().getId().equals(role.getId())).count();
            return new RoleDto(role, count);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(rolesWithCounts);
    }

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String color = (String) body.get("color");
        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) body.get("permissions");

        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Role name is required"));
        }

        if (roleRepo.findByName(name.toUpperCase()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Role already exists"));
        }

        RoleModel newRole = new RoleModel();
        newRole.setName(name.toUpperCase());
        newRole.setColor(color != null ? color : "from-gray-500 to-gray-400");
        newRole.setPermissions(permissions != null ? permissions : List.of("VIEW_DASHBOARD"));

        roleRepo.save(newRole);
        return ResponseEntity.ok(newRole);
    }
}
