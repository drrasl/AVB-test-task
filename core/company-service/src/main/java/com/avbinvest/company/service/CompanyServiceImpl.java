package com.avbinvest.company.service;

import com.avbinvest.company.client.UserClient;
import com.avbinvest.company.dto.CompanyDto;
import com.avbinvest.company.dto.NewCompanyRequest;
import com.avbinvest.company.dto.UpdateCompanyRequest;
import com.avbinvest.company.dto.UserShortDto;
import com.avbinvest.company.dto.mapper.CompanyMapper;
import com.avbinvest.company.exception.CompanyNotFoundException;
import com.avbinvest.company.exception.DuplicateCompanyException;
import com.avbinvest.company.model.Company;
import com.avbinvest.company.model.CompanyEmployee;
import com.avbinvest.company.repository.CompanyEmployeeRepository;
import com.avbinvest.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyEmployeeRepository companyEmployeeRepository;
    private final UserClient userClient;

    @Override
    public List<CompanyDto> getAllCompanies() {
        log.debug("Service: Request to provide all companies received");
        List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()) {
            log.debug("No companies found in database");
            return List.of();
        }
        List<CompanyEmployee> allEmployees = companyEmployeeRepository.findAll();
        Map<Long, List<Long>> employeeIdsByCompany = allEmployees.stream()
                .collect(Collectors.groupingBy(
                        CompanyEmployee::getCompanyId,
                        Collectors.mapping(CompanyEmployee::getEmployeeId, Collectors.toList())
                ));
        List<Long> allUserIds = allEmployees.stream()
                .map(CompanyEmployee::getEmployeeId)
                .distinct()
                .toList();
        List<UserShortDto> allUsers = getUsersByUserIds(allUserIds);
        Map<Long, UserShortDto> userMap = allUsers.stream()
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));
        return companies.stream()
                .map(company -> {
                    List<Long> employeeIds = employeeIdsByCompany.getOrDefault(company.getId(), List.of());
                    List<UserShortDto> companyUsers = employeeIds.stream()
                            .map(userMap::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    return buildCompanyDto(company, companyUsers);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompanyDto addCompany(NewCompanyRequest newCompanyRequest) {
        Long companyId = null;
        Company savedCompany = null;
        List<Long> employeeIds = newCompanyRequest.getEmployeeIds() != null
                ? newCompanyRequest.getEmployeeIds() : List.of();
        try {
            log.debug("Service: Add a new company: {}", newCompanyRequest);
            savedCompany = companyRepository.save(CompanyMapper.toCompany(newCompanyRequest));
            companyId = savedCompany.getId();
        } catch (DataIntegrityViolationException e) {
            log.error("Company with details: {} {} has already existed", newCompanyRequest.getCompanyName(),
                    newCompanyRequest.getBudget());
            throw new DuplicateCompanyException("Company already exists: " + newCompanyRequest);
        }
        Long finalCompanyId = companyId;
        List<CompanyEmployee> companyEmployees = getListOfCompanyEmployees(companyId, employeeIds);
        List<CompanyEmployee> savedCompanyEmployees = companyEmployeeRepository.saveAll(companyEmployees);
        log.debug("Service: Company with id {} with employees is saved in repository", finalCompanyId);
        return returnCompanyDto(savedCompany, employeeIds);
    }

    @Override
    @Transactional
    public CompanyDto updateCompany(Long companyId, UpdateCompanyRequest updateCompanyRequest) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + companyId));
        List<CompanyEmployee> employees = companyEmployeeRepository.findByCompanyId(companyId);
        List<Long> employeesId = employees.stream()
                .map(CompanyEmployee::getEmployeeId)
                .toList();
        log.debug("Service: Start to Update existed company: {}", updateCompanyRequest);
        boolean companyChanged = false;
        boolean employeesChanged = false;
        List<Long> newEmployeesId = null;
        if (updateCompanyRequest.getCompanyName() != null &&
                !updateCompanyRequest.getCompanyName().equals(company.getCompanyName())) {
            company.setCompanyName(updateCompanyRequest.getCompanyName());
            companyChanged = true;
        }
        if (updateCompanyRequest.getBudget() != null &&
                !updateCompanyRequest.getBudget().equals(company.getBudget())) {
            company.setBudget(updateCompanyRequest.getBudget());
            companyChanged = true;
        }
        if (updateCompanyRequest.getEmployeeIds() != null &&
                !updateCompanyRequest.getEmployeeIds().equals(employeesId)) {
            newEmployeesId = updateCompanyRequest.getEmployeeIds();
            employeesChanged = true;
        }

        if (!companyChanged && !employeesChanged) {
            log.debug("No changes detected for company id: {}", companyId);
            return returnCompanyDto(company, employeesId);
        }
        if (!employeesChanged && companyChanged) {
            log.debug("Changes detected only for Company part (save only company repo) for company id: {}", companyId);
            Company updatedCompany = companyRepository.save(company);
            return returnCompanyDto(company, employeesId);
        }
        log.debug("Changes detected for company id: {}", companyId);
        Company updatedCompany = companyRepository.save(company);
        companyEmployeeRepository.deleteByCompanyId(companyId);
        List<CompanyEmployee> companyEmployees = getListOfCompanyEmployees(companyId, newEmployeesId);
        List<CompanyEmployee> savedCompanyEmployees = companyEmployeeRepository.saveAll(companyEmployees);

        return returnCompanyDto(updatedCompany, newEmployeesId);
    }

    @Override
    @Transactional
    public void deleteCompany(Long companyId) {
        log.debug("Service: Start of deleting the company with id: {}", companyId);
        if (!companyRepository.existsById(companyId)) {
            log.info("Company with id: {} was not found", companyId);
            throw new CompanyNotFoundException("Company with id: " + companyId + " was not found");
        }
        companyRepository.deleteById(companyId);
        log.debug("Company with Id: {} was deleted", companyId);
    }

    @Override
    public CompanyDto getCompanyById(Long companyId) {
        log.debug("Service: Start of return the company with id: {}", companyId);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + companyId));
        List<Long> employeesIds = companyEmployeeRepository.findEmployeeIdsByCompanyId(companyId);
        log.info("Company with Id: {} found and returned", companyId);
        return returnCompanyDto(company, employeesIds);
    }

    private List<CompanyEmployee> getListOfCompanyEmployees(Long companyId, List<Long> employeeIds) {
        return employeeIds.stream()
                .map(id -> {
                    return CompanyEmployee.builder()
                            .companyId(companyId)
                            .employeeId(id)
                            .build();
                })
                .toList();
    }

    private CompanyDto returnCompanyDto(Company company, List<Long> employees) {
        List<UserShortDto> users = getUsersByUserIds(employees);
        CompanyDto companyDto = CompanyMapper.toCompanyDto(company);
        companyDto.setUsers(new ArrayList<>(users));
        log.debug("Service: Return CompanyDto from service to controller successfully, {}", companyDto);
        return companyDto;
    }

    private CompanyDto buildCompanyDto(Company company, List<UserShortDto> users) {
        CompanyDto companyDto = CompanyMapper.toCompanyDto(company);
        companyDto.setUsers(new ArrayList<>(users));
        log.debug("Built CompanyDto for company id: {} with {} users",
                company.getId(), users.size());
        return companyDto;
    }

    private List<UserShortDto> getUsersByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            log.debug("No user IDs to forward");
            return List.of();
        }
        try {
            log.debug("Receiving {} users from user-service", userIds.size());
            return userClient.getUsersByIds(userIds);
        } catch (Exception e) {
            log.error("Failed to get users: {}", e.getMessage());
            return List.of();
        }
    }
}
