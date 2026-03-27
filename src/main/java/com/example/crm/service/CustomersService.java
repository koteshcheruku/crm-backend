package com.example.crm.service;

import com.example.crm.Dto.CustomerDto;
import com.example.crm.Dto.UserPrincipal;
import com.example.crm.Model.CustomersModel;
import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.CustomersRepo;
import com.example.crm.Repo.UsersRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomersService {
    @Autowired
    private CustomersRepo repo;
    @Autowired
    private UsersRepo usersRepo;

    @Transactional
    public ResponseEntity<?> showCustomersService(Authentication authenticate) {
        UserPrincipal principal = (UserPrincipal) authenticate.getPrincipal();
        assert principal != null;
        Long empId = principal.getEmployee().getId();
        String roleName = principal.getEmployee().getRole().getName();
        if ("ADMIN".equals(roleName)) {
            List<CustomerDto> data = repo.findAll().stream()
                    .map(entity -> new CustomerDto((CustomersModel) entity))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(data);
        } else {
            List<CustomerDto> data = repo.findAllByEmployeeId(empId).stream()
                    .map(entity -> new CustomerDto((CustomersModel) entity))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(data);
        }
    }

    @Transactional
    public ResponseEntity<?> editCustomerService(Long id, CustomerDto data) {
        CustomersModel customer = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found " + id));
        customer.setAccountManager(data.getAccountManager());
        customer.setCurrentPlan(data.getCurrentPlan());
        customer.setPaidReceiptNo(data.getPaidReceiptNo());
        customer.setStatus(data.getStatus());
        repo.save(customer);
        return ResponseEntity.ok().body(HttpStatus.ACCEPTED);
    }

    @Transactional
    public ResponseEntity<?> createNewCustomerService(CustomerDto dto) {
        CustomersModel model = new CustomersModel();
        model.setId(dto.getId());
        model.setName(dto.getName());
        model.setEmail(dto.getEmail());
        model.setPhone(dto.getPhone());
        model.setAccountManager(dto.getAccountManager());
        model.setCurrentPlan(dto.getCurrentPlan());
        model.setPaidReceiptNo(dto.getPaidReceiptNo());
        model.setStatus(dto.getStatus());
        model.setJoinedDate(dto.getJoinedDate());
        if (dto.getEmployeeId() != null) {
            // This gets a "Proxy" - no SQL SELECT is fired!
            UsersModel employee = usersRepo.getReferenceById(dto.getEmployeeId());
            model.setEmployeeId(employee);
        }
        repo.save(model);
        return ResponseEntity.ok().body(HttpStatus.ACCEPTED);
    }
}
