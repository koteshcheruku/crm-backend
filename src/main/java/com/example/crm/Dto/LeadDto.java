package com.example.crm.Dto;

import com.example.crm.Enum.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeadDto {
    private Long id;
    private String names;
    private Long phone;
    private String source;
    private StatusEnum status;
    private String assignedTo;
    private String followUp;
    private String actions;
    private Long employeeId;

    public LeadDto(com.example.crm.Model.LeadsModel entity) {
        this.id = entity.getId();
        this.names = entity.getNames();
        this.phone = entity.getPhone();
        this.source = entity.getSource();
        this.status = entity.getStatus();
        this.assignedTo = entity.getAssignedTo();
        this.followUp = entity.getFollowUp();
        this.actions = entity.getActions();
        if (entity.getEmployeeId() != null) {
            this.employeeId = entity.getEmployeeId().getId();
        }
    }
}
