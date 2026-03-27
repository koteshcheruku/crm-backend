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
@Table(name = "tasks")
public class TasksModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String task;
    @Column(nullable = false)
    private String assignedTo;
    @Column(nullable = false)
    private String priority;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private LocalDate dueDate;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private UsersModel employeeId;
}
