package com.avbinvest.user.controller;

import com.avbinvest.user.dto.NewUserRequest;
import com.avbinvest.user.dto.UpdateUserRequest;
import com.avbinvest.user.dto.UserDto;
import com.avbinvest.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("Controller: Request for getting all users received");
        return service.getAllUsers();
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.debug("Controller: Request to add new user received");
        return service.addUser(newUserRequest);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Positive @PathVariable Long userId,
                              @RequestBody UpdateUserRequest updateUserRequest) {
        log.debug("Controller: Request to update user with id: {} received", userId);
        return service.updateUser(userId, updateUserRequest);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Positive @PathVariable Long userId) {
        log.debug("Controller: Request to delete user with id: {} received", userId);
        service.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long userId) {
        log.debug("Controller: Request to get user with id: {} received", userId);
        return service.getUserById(userId);
    }
}
