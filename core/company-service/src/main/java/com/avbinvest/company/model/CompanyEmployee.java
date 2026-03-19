package com.avbinvest.company.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_employees")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CompanyEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;
}
