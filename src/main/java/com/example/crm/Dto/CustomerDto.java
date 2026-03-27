package com.example.crm.Dto;

import com.example.crm.Model.CustomersModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerDto {
    private Long id;
    private String name;
    private Long phone;
    private String email;
    private String accountManager;
    private String actions;
    private String currentPlan;
    private Long paidReceiptNo;
    private String status;
    private LocalDate joinedDate;
    private Long employeeId;

    public CustomerDto(CustomersModel data) {
        this.id = data.getId();
        this.name = data.getName();
        this.email = data.getEmail();
        this.accountManager = data.getAccountManager();
        this.currentPlan = data.getCurrentPlan();
        this.paidReceiptNo = data.getPaidReceiptNo();
        this.status = data.getStatus();
        this.joinedDate = data.getJoinedDate();
        this.phone = data.getPhone();
        if (data.getEmployeeId() != null) {
            this.employeeId = data.getEmployeeId().getId();
//            this.employeeName = data.getEmployeeId().getUsername();
        }
    }
}
