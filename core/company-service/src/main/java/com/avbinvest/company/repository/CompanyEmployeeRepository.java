package com.avbinvest.company.repository;

import com.avbinvest.company.model.CompanyEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyEmployeeRepository extends JpaRepository<CompanyEmployee,Long> {

    // Return all entities by company ID
    List<CompanyEmployee> findByCompanyId(Long companyId);

    // Return list of employee ID's by Company ID
    @Query("SELECT ce.employeeId FROM CompanyEmployee ce WHERE ce.companyId = :companyId")
    List<Long> findEmployeeIdsByCompanyId(Long companyId);

    // Delete all data for company ID (in order to update with new data)
    void deleteByCompanyId(Long companyId);

    List<CompanyEmployee>findByCompanyIdIn(List<Long> companyId);
}
