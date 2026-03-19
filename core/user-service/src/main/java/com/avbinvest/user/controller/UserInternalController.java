package com.avbinvest.user.controller;

import com.avbinvest.user.dto.UserShortDto;
import com.avbinvest.user.service.UserInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Slf4j
public class UserInternalController {

    private final UserInternalService userInternalService;

    @GetMapping
    List<UserShortDto> getUsersByIds(@RequestParam(name = "userIds", required = false) List<Long> userIds) {
        log.debug("User Internal Controller: Received request from external service to provide UserShortDto ");
        return userInternalService.getUsersByIds(userIds);
    }
}
