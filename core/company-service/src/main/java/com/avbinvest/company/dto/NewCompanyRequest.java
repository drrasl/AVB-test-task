package com.avbinvest.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class NewCompanyRequest {

    @NotBlank(message = "The Company name cannot be empty")
    @Size(min = 3, max = 250, message = "The Company name must contain from 3 to 250 characters.")
    private String companyName;

    @NotNull(message = "The Company budget cannot be null")
    private BigDecimal budget;

    @NotNull(message = "The list of the employee's Id cannot be null")
    private List<Long> employeeIds;
}
