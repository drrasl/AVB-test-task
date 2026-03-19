package com.avbinvest.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "The user name cannot be empty.")
    @Size(min = 3, max = 250, message = "The name must contain from 3 to 250 characters.")
    private String firstName;

    @NotBlank(message = "The user's last name cannot be empty.")
    @Size(min = 3, max = 250, message = "The last name must contain from 3 to 250 characters.")
    private String lastName;

    @NotBlank(message = "The phone can't be empty")
    @Size(min = 1, max = 20, message = "The phone number must contain from 1 to 20 characters.")
    private String phone;

    @NotNull
    @Positive
    private Long companyId;
}
