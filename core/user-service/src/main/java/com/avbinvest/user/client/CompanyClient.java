package com.avbinvest.user.client;

import com.avbinvest.user.dto.CompanyShortDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "company-service", fallback = CompanyServiceClientFallback.class)
public interface CompanyClient {

    @GetMapping("/internal/company/{companyId}")
    CompanyShortDto getCompanyById(@PathVariable("companyId") Long companyId);

    @GetMapping("/internal/companies")
    List<CompanyShortDto> getCompaniesByIds(@RequestParam(name = "companyIds", required = false) List<Long> companyIds);
}
