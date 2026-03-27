package com.example.crm.Repo;

import com.example.crm.Model.TasksModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepo extends JpaRepository<TasksModel, Long> {
    // @Query(value = "SELECT * FROM tasks WHERE employee_id = :empId", nativeQuery
    // = true)
    // Optional<Object> findByEmployeeId(@Param("empId") Long empId);
    List<TasksModel> findByEmployeeId_Id(Long empId);
}
