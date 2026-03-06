package com.avbinvest.company.service;

import com.avbinvest.company.dto.CompanyShortDto;
import com.avbinvest.company.dto.mapper.CompanyMapper;
import com.avbinvest.company.exception.CompanyNotFoundException;
import com.avbinvest.company.model.Company;
import com.avbinvest.company.model.CompanyEmployee;
import com.avbinvest.company.repository.CompanyEmployeeRepository;
import com.avbinvest.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyInternalServiceImpl implements CompanyInternalService{

    private final CompanyRepository companyRepository;
    private final CompanyEmployeeRepository companyEmployeeRepository;

    @Override
    public CompanyShortDto getCompanyById(Long companyId) {
        log.debug("Internal Service: Start of return the company with id: {}", companyId);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + companyId));
        List<Long> employeesIds = companyEmployeeRepository.findEmployeeIdsByCompanyId(companyId);
        CompanyShortDto companyShortDto = CompanyMapper.toCompanyShortDto(company, employeesIds);
        log.info("Company with Id: {} found and returned", companyId);
        return companyShortDto;
    }

    @Override
    public List<CompanyShortDto> getCompaniesByIds(List<Long> companyIds) {
        log.debug("Internal Service: Start of return # {} companies", companyIds.size());
        List<Company> companies = companyRepository.findByIdIn(companyIds);
        if (companies.isEmpty()) {
            log.debug("No companies found in database");
            return List.of();
        }
        List<CompanyEmployee> allEmployees = companyEmployeeRepository.findByCompanyIdIn(companyIds);
        Map<Long, List<Long>> employeeIdsByCompany = allEmployees.stream()
                .collect(Collectors.groupingBy(
                        CompanyEmployee::getCompanyId,
                        Collectors.mapping(CompanyEmployee::getEmployeeId, Collectors.toList())
                ));
        log.info("Companies were found and returned");
        return companies.stream()
                .map(company -> {
                    List<Long> employeesIds = employeeIdsByCompany.getOrDefault(company.getId(), List.of());
                    return CompanyMapper.toCompanyShortDto(company, employeesIds);
                })
                .collect(Collectors.toList());
    }
}
