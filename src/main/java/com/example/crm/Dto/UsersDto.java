package com.example.crm.Dto;

import com.example.crm.Model.UsersModel;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {
    private Long id;
    private String username;
    private String fullname;
    private String email;
    private String department;
    private long mobileNo;
    private String role;
    private LocalDate joinedDate;
    private String actions;
    private String status;
    private String password;
    private Boolean emailNotifications;
    private Boolean taskReminders;
    private Boolean leadUpdates;
    private Boolean systemAlerts;
    /** PENDING | APPROVED — registration / invite workflow */
    private String approvalStatus;

    private int taskCount;
    private int leadCount;
    private int customerCount;

    public UsersDto(UsersModel entity) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.fullname = entity.getFullname();
        this.email = entity.getEmail();
        this.department = entity.getDepartment();
        this.mobileNo = entity.getMobileNo();
        this.role = entity.getRole() != null ? entity.getRole().getName() : null;
        this.joinedDate = entity.getJoinedDate();
        this.status = entity.getStatus();
        this.password = entity.getPassword();
        this.emailNotifications = entity.getEmailNotifications();
        this.leadUpdates = entity.getLeadUpdates();
        this.systemAlerts = entity.getSystemAlerts();
        this.taskReminders = entity.getTaskReminders();

        // Safely map counts to avoid NullPointer if lists are null
        this.taskCount = (entity.getTasks() != null) ? entity.getTasks().size() : 0;
        this.leadCount = (entity.getLeads() != null) ? entity.getLeads().size() : 0;
        this.customerCount = (entity.getCustomers() != null) ? entity.getCustomers().size() : 0;
    }
}
