package com.avbinvest.company.repository;

import com.avbinvest.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company,Long> {

    List<Company> findByIdIn(List<Long> ids);
}
