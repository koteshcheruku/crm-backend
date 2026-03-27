package com.example.crm.Dto;

import com.example.crm.Enum.InOut;
import com.example.crm.Model.UsersLogsModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogsDto {
    private Long id;
    private LocalDateTime loggedInDateTime;
    private InOut inOut;
    private LocalDateTime loggedOutDateTime;
    private Long employeeId;

    public LogsDto(UsersLogsModel data) {
        this.loggedInDateTime = data.getLoggedInDateTime();
        this.loggedOutDateTime = data.getLoggedOutDateTime();
        if (data.getEmployeeId() != null)
            this.employeeId = data.getEmployeeId().getId();
    }
}
