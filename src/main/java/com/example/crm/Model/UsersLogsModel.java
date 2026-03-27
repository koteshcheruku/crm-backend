package com.example.crm.Model;

import com.example.crm.Enum.InOut;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@DynamicUpdate
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "logs")
public class UsersLogsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime loggedInDateTime;
    @Enumerated(value = EnumType.STRING)
    private InOut inOut;
    private LocalDateTime loggedOutDateTime;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private UsersModel employeeId;
}
