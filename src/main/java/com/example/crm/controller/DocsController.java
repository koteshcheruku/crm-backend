package com.example.crm.controller;

import com.example.crm.Dto.DocsDto;
import com.example.crm.service.DocsService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class DocsController {
    @Autowired
    private DocsService service;
    @GetMapping("/documents")
    public ResponseEntity<?> getDocs(Authentication authentication){
        try{return service.getDocs(authentication);} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed fetching data");
        }
    }
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<?> deleteDoc(@PathVariable Long id) {
        try {
            return service.deleteDocService(id);
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during deleting document");
        }
    }

    @PutMapping("/documents/{id}")
    public ResponseEntity<?> editDoc(@PathVariable Long id, @RequestBody DocsDto data) {
        try {
            return service.editDocsService(id,data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during update document");
        }
    }

    @PostMapping("/documents/new/{id}")
    public ResponseEntity<?> newDoc(@PathVariable("id") Long id ,@RequestParam("file") MultipartFile file, Authentication authentication) {
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("File too large (max 10MB)");
        }
        try {
            return service.createDoc(file,authentication);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during creating new document");
        }
    }
}
