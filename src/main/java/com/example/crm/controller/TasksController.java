package com.example.crm.controller;

import com.example.crm.Dto.TaskDto;
import com.example.crm.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/tasks")
public class TasksController {

    @Autowired
    private TasksService service;

    @GetMapping("")
    public ResponseEntity<?> showTasks(Authentication authentication) {
        try {
            return service.showTasksService(authentication);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during showing tasks");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Principal principal) {
        try {
            return service.deleteLeadService(id, principal);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during deleting tasks");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editTask(@PathVariable Long id, @RequestBody TaskDto data) {
        try {
            return service.editTaskService(id, data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during update tasks");
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> newTask(@RequestBody TaskDto data) {
        try {
            return service.createNewLeadService(data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during creating new task");
        }
    }
}
