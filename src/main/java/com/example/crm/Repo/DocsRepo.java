package com.example.crm.Repo;

import com.example.crm.Model.DocsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocsRepo extends JpaRepository<DocsModel, Long> {
    List<DocsModel> findByEmployeeId_Id(Long id);
}
