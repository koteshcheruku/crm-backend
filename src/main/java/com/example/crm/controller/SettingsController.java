package com.example.crm.controller;

import com.example.crm.Dto.UsersDto;
import com.example.crm.Model.UsersModel;
import com.example.crm.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {
    @Autowired
    private SettingsService service;

    @GetMapping
    public ResponseEntity<?> returnUser(Authentication authentication) {
        try {
            return service.viewSettings(authentication);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during update");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> EditSettings(@PathVariable("id") Long id, @RequestBody UsersModel data) {
        if (id == null) {
            return ResponseEntity.badRequest().body("empty id");
        }
        try {
            return service.editSettings(id, data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during update");
        }
    }

    @PatchMapping("/{id}/notifications")
    public ResponseEntity<?> EditNotifications(@PathVariable("id") Long id, @RequestBody UsersDto data) {
        if (id == null) {
            return ResponseEntity.badRequest().body("empty id");
        }
        try {
            return service.editNotificationSettings(id, data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during update");
        }
    }

    @PatchMapping("/{id}/change-password")
    public ResponseEntity<?> ChangePassword(@PathVariable("id") Long id, @RequestBody UsersDto data) {
        if (id == null) {
            return ResponseEntity.badRequest().body("empty id");
        }
        try {
            return service.ChangePassword(id, data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during update");
        }
    }
}
