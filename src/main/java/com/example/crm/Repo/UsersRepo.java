package com.example.crm.Repo;

import com.example.crm.Model.UsersModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<UsersModel, Long> {
    UsersModel findByEmail(String email);
    Optional<UsersModel> findByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UsersModel> findByInviteToken(String token);
}
