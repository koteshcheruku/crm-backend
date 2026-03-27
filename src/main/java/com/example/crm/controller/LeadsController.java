package com.example.crm.controller;

import com.example.crm.Dto.LeadDto;
import com.example.crm.service.LeadsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leads")
public class LeadsController {
    @Autowired
    private LeadsService service;

    @GetMapping("")
    public ResponseEntity<?> showLeads(Authentication authentication) {
        try {
            return service.showLeadsService(authentication);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during showing leads");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id) {
        try {
            return service.deleteLeadService(id);
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during deleting lead");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editLead(@PathVariable Long id, @RequestBody LeadDto data) {
        try {
            return service.editLeadService(id,data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during update lead");
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> newLead(@RequestBody LeadDto data) {
        try {
            return service.createNewLeadService(data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during creating new lead");
        }
    }
}
