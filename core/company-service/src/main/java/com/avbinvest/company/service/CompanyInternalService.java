package com.avbinvest.company.service;

import com.avbinvest.company.dto.CompanyShortDto;

import java.util.List;

public interface CompanyInternalService {

    CompanyShortDto getCompanyById(Long companyId);
    List<CompanyShortDto> getCompaniesByIds(List<Long> companyIds);
}
