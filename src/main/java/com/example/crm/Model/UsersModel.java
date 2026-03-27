package com.example.crm.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.List;

@Entity
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "employee_info")
public class UsersModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    private String fullname;
    @Column(nullable = false)
    private String email;
    private String department;
    @Column(nullable = false)
    private String password;
    private String oldPassword;
    private long mobileNo;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private RoleModel role;

    @Column(name = "role", insertable = false, updatable = false)
    private String oldRole;
    @Column(nullable = false)
    private LocalDate joinedDate;
    private String status;
    @Column(unique = true)
    private String inviteToken;
    @Column(nullable = false)
    private String approvalStatus = "APPROVED";
    @Column(nullable = false)
    private Boolean emailNotifications = true;
    @Column(nullable = false)
    private Boolean taskReminders = true;
    @Column(nullable = false)
    private Boolean leadUpdates = true;
    @Column(nullable = false)
    private Boolean systemAlerts = true;
    @OneToMany(mappedBy = "employeeId")
    @ToString.Exclude
    private List<TasksModel> tasks;
    @OneToMany(mappedBy = "employeeId")
    @ToString.Exclude
    private List<LeadsModel> leads;
    @OneToMany(mappedBy = "employeeId")
    @ToString.Exclude
    private List<CustomersModel> customers;
    @OneToMany(mappedBy = "employeeId")
    @ToString.Exclude
    private List<MessageModel> messages;
    @OneToMany(mappedBy = "employeeId")
    @ToString.Exclude
    private List<UsersLogsModel> logs;
    @OneToMany(mappedBy = "employeeId")
    @ToString.Exclude
    private List<DocsModel> docs;
}
