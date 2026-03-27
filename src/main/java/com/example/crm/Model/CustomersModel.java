package com.example.crm.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "customers")
public class CustomersModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private Long phone;
    private String email;
    @Column(nullable = false)
    private String accountManager;
    @Column(nullable = false)
    private String currentPlan;
    @Column(nullable = false)
    private Long paidReceiptNo;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private LocalDate joinedDate;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private UsersModel employeeId;
}

