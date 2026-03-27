package com.example.crm.controller;

import com.example.crm.service.dashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    @Autowired private dashboardService service;
    @GetMapping("/api/dashboard")
    public ResponseEntity<?> dashboardView(Authentication authentication){
        return service.getData(authentication);
    }
}
