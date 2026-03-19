package com.avbinvest.company.dto.mapper;

import com.avbinvest.company.dto.CompanyDto;
import com.avbinvest.company.dto.CompanyShortDto;
import com.avbinvest.company.dto.NewCompanyRequest;
import com.avbinvest.company.model.Company;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyMapper {

    public static Company toCompany(NewCompanyRequest newCompanyRequest) {
        return Company.builder()
                .id(0L)
                .companyName(newCompanyRequest.getCompanyName())
                .budget(newCompanyRequest.getBudget())
                .build();
    }

    public static CompanyDto toCompanyDto(Company company) {
        return CompanyDto.builder()
                .id(company.getId())
                .name(company.getCompanyName())
                .budget(company.getBudget())
                .build();
        //UserShortDto will be filled separately in service
    }

    public static CompanyShortDto toCompanyShortDto(Company company, List<Long> ids) {
        return CompanyShortDto.builder()
                .id(company.getId())
                .name(company.getCompanyName())
                .budget(company.getBudget())
                .userIds(new ArrayList<>(ids))
                .build();
    }
}
