package com.example.crm.Repo;

import com.example.crm.Model.TeamModel;
import com.example.crm.Model.UsersModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepo extends JpaRepository<TeamModel, Long> {
    List<TeamModel> findByMembersContaining(UsersModel user);
}
