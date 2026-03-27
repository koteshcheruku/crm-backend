package com.example.crm.service;

import com.example.crm.Enum.InOut;
import com.example.crm.Model.UsersLogsModel;
import com.example.crm.Model.UsersModel;
import com.example.crm.Repo.LogsRepo;
import com.example.crm.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {
    @Autowired private LogsRepo repo;
    @Autowired private UsersRepo usersRepo;
    public void onLogin(String username) {
        if(username != null)
            throw new RuntimeException("no username");
        UsersModel user = usersRepo.findByEmail(username);
        UsersLogsModel log = new UsersLogsModel();
        log.setEmployeeId(user);
        log.setInOut(InOut.ONLINE);
        repo.save(log);
    }
    public void onLogout(String username) {
        if(username != null)
            throw new RuntimeException("no username");
        UsersLogsModel log =
                repo.findTopByEmployeeId_IdOrderByLoggedInDateTimeDesc
                        (usersRepo.findByEmail(username).getId());
        log.setInOut(InOut.OFFLINE);
        log.setLoggedOutDateTime(LocalDateTime.now());
        repo.save(log);
    }
}
