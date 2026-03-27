package com.example.crm.service;

import com.example.crm.Dto.UserPrincipal;
import com.example.crm.Dto.UsersDto;
import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.UsersRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SettingsService {
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> viewSettings(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        assert principal != null;
        UsersModel user = principal.getEmployee();
        UsersDto data = new UsersDto();
        data.setUsername(user.getUsername());
        data.setEmail(user.getEmail());
        data.setEmailNotifications(user.getEmailNotifications());
        data.setTaskReminders(user.getTaskReminders());
        data.setSystemAlerts(user.getSystemAlerts());
        data.setLeadUpdates(user.getLeadUpdates());
        return ResponseEntity.ok(data);
    }
    @Transactional
    public ResponseEntity<?> editSettings(Long id, UsersModel data) {
        UsersModel existingUser = usersRepo.findById(id).orElseThrow(()-> new RuntimeException("user not found"));
        if (data.getFullname() != null && !Objects.equals(existingUser.getFullname(), data.getFullname()))
            existingUser.setFullname(data.getFullname());
        if (data.getEmail() != null && !Objects.equals(existingUser.getEmail(), data.getEmail()))
            existingUser.setEmail(data.getEmail());
        return ResponseEntity.ok("Done");
    }
    @Transactional
    public ResponseEntity<?> editNotificationSettings(Long id, UsersDto data) {
        UsersModel existingUser = usersRepo.findById(id).orElseThrow();
        System.out.println("data.getEmailNotifications(): "+data.getEmailNotifications());
        System.out.println("data.getTaskReminders() "+data.getTaskReminders());
        if (data.getEmailNotifications() != null
                && !Objects.equals(existingUser.getEmailNotifications(), data.getEmailNotifications())
        )
            existingUser.setEmailNotifications(data.getEmailNotifications());
        if (data.getLeadUpdates() != null
                && !Objects.equals(existingUser.getLeadUpdates(), data.getLeadUpdates())
        )
            existingUser.setLeadUpdates(data.getLeadUpdates());
        if (data.getSystemAlerts() != null
                && !Objects.equals(existingUser.getSystemAlerts(), data.getSystemAlerts())
        )
            existingUser.setSystemAlerts(data.getSystemAlerts());
        if (data.getTaskReminders() != null
                && !Objects.equals(existingUser.getTaskReminders(), data.getTaskReminders())
        )
            existingUser.setTaskReminders(data.getTaskReminders());
        usersRepo.save(existingUser);
        return ResponseEntity.ok("successfully updated");
    }

    public ResponseEntity<?> ChangePassword(Long id, UsersDto data) {
        UsersModel existingUser = usersRepo.findById(id).orElseThrow(()-> new RuntimeException("user not found"));
        if (data.getPassword() != null
                && !passwordEncoder.matches(existingUser.getPassword(), passwordEncoder.encode(data.getPassword()))){
            existingUser.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        usersRepo.save(existingUser);
        return ResponseEntity.ok("successfully updated");
    }
}

