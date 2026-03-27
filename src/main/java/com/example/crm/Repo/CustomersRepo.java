package com.example.crm.Repo;

import com.example.crm.Model.CustomersModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomersRepo extends JpaRepository<CustomersModel, Long> {
    @Query(value = "SELECT * FROM customers WHERE employee_id = :empId", nativeQuery = true)
    List<CustomersModel> findAllByEmployeeId(@Param("empId") Long empId);

}
