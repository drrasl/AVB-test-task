package com.avbinvest.company.controller;

import com.avbinvest.company.dto.CompanyShortDto;
import com.avbinvest.company.service.CompanyInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Slf4j
public class CompanyInternalController {

    private final CompanyInternalService companyInternalService;

    @GetMapping("/company/{companyId}")
    CompanyShortDto getCompanyById(@PathVariable("companyId") Long companyId) {
        log.debug("Company Internal Controller: Received request from external service to provide CompanyShortDto");
        return companyInternalService.getCompanyById(companyId);
    }

    @GetMapping("/companies")
    List<CompanyShortDto> getCompaniesByIds(@RequestParam(name = "companyIds", required = false) List<Long> companyIds) {
        log.debug("Company Internal Controller: Received request from external service to provide List<CompanyShortDto>");
        return companyInternalService.getCompaniesByIds(companyIds);
    }
}
