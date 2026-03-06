package com.avbinvest.company.controller;

import com.avbinvest.company.dto.CompanyDto;
import com.avbinvest.company.dto.NewCompanyRequest;
import com.avbinvest.company.dto.UpdateCompanyRequest;
import com.avbinvest.company.service.CompanyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/companies")
@Validated
public class CompanyController {

    private final CompanyService service;

    @GetMapping
    public List<CompanyDto> getAllCompanies() {
        log.debug("Controller: Request for getting all companies received");
        return service.getAllCompanies();
    }

    @PostMapping
    public CompanyDto addCompany(@Valid @RequestBody NewCompanyRequest newCompanyRequest) {
        log.debug("Controller: Request to add new company received");
        return service.addCompany(newCompanyRequest);
    }

    @PatchMapping("/{companyId}")
    public CompanyDto updateCompany(@Positive @PathVariable Long companyId,
                              @RequestBody UpdateCompanyRequest updateCompanyRequest) {
        log.debug("Controller: Request to update company with id: {} received", companyId);
        return service.updateCompany(companyId, updateCompanyRequest);
    }

    @DeleteMapping("/{companyId}")
    public void deleteCompany(@Positive @PathVariable Long companyId) {
        log.debug("Controller: Request to delete company with id: {} received", companyId);
        service.deleteCompany(companyId);
    }

    @GetMapping("/{id}")
    public CompanyDto getCompany(@PathVariable("id") Long companyId) {
        log.debug("Controller: Request to get company with id: {} received", companyId);
        return service.getCompanyById(companyId);
    }
}
