package com.avbinvest.user.client;

import com.avbinvest.user.dto.CompanyShortDto;

import java.util.List;

public class CompanyServiceClientFallback implements CompanyClient {
    @Override
    public CompanyShortDto getCompanyById(Long companyId) {
        throw new RuntimeException("Fallback response: Client service is unavailable during request company by id");
    }

    @Override
    public List<CompanyShortDto> getCompaniesByIds(List<Long> companyIds) {
        throw new RuntimeException("Fallback response: Client service is unavailable during request companies by ids");
    }
}
