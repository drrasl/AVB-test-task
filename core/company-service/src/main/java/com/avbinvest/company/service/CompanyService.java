package com.avbinvest.company.service;

import com.avbinvest.company.dto.CompanyDto;
import com.avbinvest.company.dto.NewCompanyRequest;
import com.avbinvest.company.dto.UpdateCompanyRequest;

import java.util.List;

public interface CompanyService {
    List<CompanyDto> getAllCompanies();
    CompanyDto addCompany(NewCompanyRequest newCompanyRequest);
    CompanyDto updateCompany(Long companyId, UpdateCompanyRequest updateCompanyRequest);
    void deleteCompany(Long companyId);
    CompanyDto getCompanyById(Long companyId);
}
