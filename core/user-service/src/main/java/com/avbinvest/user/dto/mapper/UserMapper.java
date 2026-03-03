package com.avbinvest.user.dto.mapper;

import com.avbinvest.user.dto.NewUserRequest;
import com.avbinvest.user.dto.UserDto;
import com.avbinvest.user.dto.UserShortDto;
import com.avbinvest.user.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toUser(NewUserRequest newUserRequest) {
        return User.builder()
                .id(0L)
                .firstName(newUserRequest.getFirstName())
                .lastName(newUserRequest.getLastName())
                .phone(newUserRequest.getPhone())
                .companyId(newUserRequest.getCompanyId())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .build();
        //CompanyDto will be filled separately in service
    }
}
