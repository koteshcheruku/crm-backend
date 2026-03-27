package com.example.crm.Repo;

import com.example.crm.Model.UsersLogsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogsRepo extends JpaRepository<UsersLogsModel,Long> {
    UsersLogsModel findTopByEmployeeId_IdOrderByLoggedInDateTimeDesc(Long id);

    List<UsersLogsModel> findTopByEmployeeId_Id(Long id);
}
