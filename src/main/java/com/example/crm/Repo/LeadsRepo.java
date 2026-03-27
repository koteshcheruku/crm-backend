package com.example.crm.Repo;

import com.example.crm.Model.LeadsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadsRepo extends JpaRepository<LeadsModel, Long> {

    // @Query(value = "SELECT * FROM leads WHERE employee_id = :empId", nativeQuery
    // = true)
    // List<LeadsModel> findAllByEmployeeId(@Param("empId") Long empId);

    List<LeadsModel> findByEmployeeId_Id(Long empId);
}
