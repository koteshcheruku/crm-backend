package com.example.crm.controller;

import com.example.crm.Dto.UsersDto;
import com.example.crm.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/users")
public class UsersController {
    @Autowired
    private UsersService service;

    @GetMapping("")
    public ResponseEntity<?> getUsers(){
        try{
            return service.getUsersService();
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during fetching users");
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable("id") Long id) {
        try {
            return service.deleteUserService(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during deleting users");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editUser(@PathVariable("id") Long id, @RequestBody UsersDto data) {
        if (id == null) {
            return ResponseEntity.badRequest().body("Employee ID must not be null for updates");
        }
        try {
            return service.editUserService(id,data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during update");
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> newUser(@RequestBody UsersDto data) {
        try {
            return service.createNewUserService(data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during update");
        }
    }
}
