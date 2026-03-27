package com.example.crm.service;

import com.example.crm.Dto.DocsDto;
import com.example.crm.Dto.UserPrincipal;
import com.example.crm.Model.DocsModel;
import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.DocsRepo;
import com.example.crm.Repo.UsersRepo;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocsService {
    @Autowired
    private DocsRepo repo;
    @Autowired
    private UsersRepo userRepo;

    public ResponseEntity<?> getDocs(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        assert principal != null;
        if ("ADMIN".equals(principal.getEmployee().getRole().getName())) {
            List<DocsDto> data = repo.findAll().stream().map(DocsDto::new).collect(Collectors.toList());
            return ResponseEntity.ok(data);
        } else {
            List<DocsDto> data = repo.findByEmployeeId_Id(principal.getEmployee().getId()).stream().map(DocsDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(data);
        }
    }

    public ResponseEntity<?> createDoc(MultipartFile file, Authentication auth) throws Exception {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        assert principal != null;
        UsersModel user = userRepo.findByEmail(principal.getUsername());
        String filePath = "uploads/" + file.getOriginalFilename();
        file.transferTo(new File(filePath));
        DocsModel doc = new DocsModel();
        doc.setFileName(file.getOriginalFilename());
        doc.setSize(file.getSize() / 1024);
        doc.setUploadedBy(user.getUsername());
        doc.setEmployeeId(user);
        doc.setFileUrl(doc.getFileUrl());
        return ResponseEntity.ok(doc);
    }

    public ResponseEntity<?> editDocsService(Long id, DocsDto data) {
        DocsModel doc = repo.findById(id).orElseThrow();
        if (!(doc.getFileName().equals(data.getFileName())))
            doc.setFileName(data.getFileName());
        return ResponseEntity.ok(data);
    }

    public ResponseEntity<?> deleteDocService(Long id) {
        repo.deleteById(id);
        return ResponseEntity.ok("Successfully deleted document");
    }
}
