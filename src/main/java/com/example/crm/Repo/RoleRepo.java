package com.example.crm.Repo;

import com.example.crm.Model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<RoleModel, Long> {
    Optional<RoleModel> findByName(String name);
}
