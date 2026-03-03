package com.avbinvest.user.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 250, message = "Имя должно содержать от 3 до 250 символов")
    private String firstName;

    @NotBlank(message = "Фамилия пользователя не может быть пустой")
    @Size(min = 3, max = 250, message = "Фамилия должна содержать от 3 до 250 символов")
    private String lastName;

    @NotBlank(message = "Телефон не может быть пустым")
    @Size(min = 1, max = 20, message = "Телефон должен содержать от 1 до 20 символов")
    private String phone;

    @NotBlank(message = "Айди компании не может быть пустым")
    @Positive
    private Long companyId;
}
