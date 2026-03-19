package com.avbinvest.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyShortDto {
    private Long id;
    private String name;
    private BigDecimal budget;
    private ArrayList<Long> userIds;
}
