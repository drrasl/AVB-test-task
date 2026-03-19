package com.avbinvest.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private String name;
    private BigDecimal budget;
    private List<UserShortDto> users;
}
