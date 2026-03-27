package com.example.crm.Dto;

import com.example.crm.Model.TasksModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskDto {
    private Long id;
    private String task;
    private String assignedTo;
    private String priority;
    private String status;
    private LocalDate dueDate;
    private Long employeeId;
    public TaskDto(TasksModel data){
        this.id = data.getId();
        this.task = data.getTask();
        this.assignedTo = data.getAssignedTo();
        this.dueDate = data.getDueDate();
        this.status = data.getStatus();
        this.priority = data.getPriority();
        if (data.getEmployeeId() != null) {
            this.employeeId = data.getEmployeeId().getId();
        }
    }
}
