package com.example.crm.Model;

import com.example.crm.Enum.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "leads")
public class LeadsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String names;
    @Column(nullable = false)
    private Long phone;
    @Column(nullable = false)
    private String source;
    @Column(nullable = false)
    private StatusEnum status;
    @Column(nullable = false)
    private String assignedTo;
    @Column(nullable = false)
    private String followUp;
    @Column(nullable = false)
    private String actions;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private UsersModel employeeId;
}
