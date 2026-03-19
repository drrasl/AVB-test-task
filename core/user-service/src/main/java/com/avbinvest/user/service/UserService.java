package com.avbinvest.user.service;

import com.avbinvest.user.dto.NewUserRequest;
import com.avbinvest.user.dto.UpdateUserRequest;
import com.avbinvest.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto addUser(NewUserRequest newUserRequest);
    UserDto updateUser(Long userId, UpdateUserRequest updateUserRequest);
    void deleteUser(Long userId);
    UserDto getUserById(Long userId);
}
