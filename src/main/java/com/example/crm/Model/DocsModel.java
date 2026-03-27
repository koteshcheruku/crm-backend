package com.example.crm.Model;

import com.example.crm.Enum.TypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@DynamicUpdate
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "documents")
public class DocsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fileName;
    private Long size;
    @Column(nullable = false)
    private String uploadedBy;
    @Column(nullable = false)
    private String fileUrl;
    @Enumerated(value = EnumType.STRING)
    private TypeEnum Type;
    @CreationTimestamp
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private UsersModel employeeId;
}
