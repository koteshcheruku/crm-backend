package com.example.crm.service;

import com.example.crm.Dto.TaskDto;
import com.example.crm.Dto.UserPrincipal;
import com.example.crm.Model.UsersModel;
import com.example.crm.Model.TasksModel;
import com.example.crm.Repo.UsersRepo;
import com.example.crm.Repo.TasksRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class TasksService {
    @Autowired
    private TasksRepo repo;
    @Autowired
    private UsersRepo usersRepo;

    public ResponseEntity<?> showTasksService(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        assert principal != null;
        Long empId = principal.getEmployee().getId();
        String roleName = principal.getEmployee().getRole().getName();
        if ("ADMIN".equals(roleName)) {
            List<TaskDto> data = repo.findAll()
                    .stream().map(entity -> new TaskDto((TasksModel) entity)).toList();
            return ResponseEntity.ok(data);
        } else {
            List<TaskDto> data = repo.findByEmployeeId_Id(empId)
                    .stream().map(entity -> new TaskDto((TasksModel) entity)).toList();
            return ResponseEntity.ok(data);
        }

    }

    public ResponseEntity<?> deleteLeadService(Long id, Principal principal) {
        repo.deleteById(id);
        return ResponseEntity.ok().body(HttpStatus.ACCEPTED);
    }

    public ResponseEntity<?> editTaskService(Long id, TaskDto data) {
        TasksModel task = repo.findById(id).orElseThrow(() -> new RuntimeException("task not found " + id));
        task.setStatus(data.getStatus());
        task.setTask(data.getTask());
        task.setDueDate(data.getDueDate());
        repo.save(task);
        return ResponseEntity.ok("task edited");
    }

    public ResponseEntity<?> createNewLeadService(TaskDto data) {
        TasksModel task = new TasksModel();
        task.setId(data.getId());
        task.setTask(data.getTask());
        task.setAssignedTo(data.getAssignedTo());
        task.setDueDate(data.getDueDate());
        task.setStatus(data.getStatus());
        task.setPriority(data.getPriority());
        if (data.getEmployeeId() != null) {
            UsersModel emp = usersRepo.getReferenceById(data.getId());
            task.setEmployeeId(emp);
        }
        repo.save(task);
        return ResponseEntity.ok(data);
    }
}
