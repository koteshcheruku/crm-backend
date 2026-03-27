package com.example.crm.service;

import com.example.crm.Dto.LeadDto;
import com.example.crm.Dto.UserPrincipal;
import com.example.crm.Model.UsersModel;
import com.example.crm.Model.LeadsModel;
import com.example.crm.Repo.UsersRepo;
import com.example.crm.Repo.LeadsRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeadsService {
    @Autowired
    private LeadsRepo leadsRepo;
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private NotificationService notificationService;

    public ResponseEntity<?> showLeadsService(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        assert principal != null;
        Long empId = principal.getEmployee().getId();
        String roleName = principal.getEmployee().getRole().getName();
        if ("ADMIN".equals(roleName)) {
            List<LeadDto> data = leadsRepo.findAll().stream()
                    .map(LeadDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(data);
        } else {
            List<LeadDto> data = leadsRepo.findByEmployeeId_Id(empId).stream()
                    .map(LeadDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(data);
        }
    }

    @Transactional
    public ResponseEntity<?> createNewLeadService(LeadDto data) {
        LeadsModel lead = new LeadsModel();
        lead.setId(data.getId());
        lead.setNames(data.getNames());
        lead.setPhone(data.getPhone());
        lead.setSource(data.getSource());
        lead.setStatus(data.getStatus());
        lead.setAssignedTo(data.getAssignedTo());
        lead.setFollowUp(data.getFollowUp());
        lead.setActions(data.getActions());
        if (data.getEmployeeId() != null) {
            UsersModel employee = usersRepo.getReferenceById(data.getEmployeeId());
            lead.setEmployeeId(employee);
        }
        leadsRepo.save(lead);

        // Notify all admins of the new lead in real-time
        notificationService.notifyAllAdmins(
                "NEW_LEAD",
                "New lead added: " + (data.getNames() != null ? data.getNames() : "Unknown"));

        return ResponseEntity.ok(data);
    }

    @Transactional
    public ResponseEntity<?> deleteLeadService(Long id) {
        leadsRepo.deleteById(id);
        return ResponseEntity.ok().body(HttpStatus.ACCEPTED);
    }

    @Transactional
    public ResponseEntity<?> editLeadService(Long id, LeadDto data) {
        LeadsModel lead = leadsRepo.findById(id).orElseThrow(() -> new RuntimeException("Lead not found " + id));
        if (!(lead.getActions().equals(data.getActions())))
            lead.setActions(data.getActions());
        if (!(lead.getFollowUp().equals(data.getFollowUp())))
            lead.setFollowUp(data.getFollowUp());
        if (!(lead.getStatus().equals(data.getStatus())))
            lead.setStatus(data.getStatus());
        if (!(lead.getSource().equals(data.getSource())))
            lead.setSource(data.getSource());
        leadsRepo.save(lead);
        return ResponseEntity.ok().body(HttpStatus.ACCEPTED);
    }
}
