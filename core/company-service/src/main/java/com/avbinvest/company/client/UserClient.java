package com.avbinvest.company.client;

import com.avbinvest.company.dto.UserShortDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class)
public interface UserClient {

    @GetMapping("/internal/users")
    List<UserShortDto> getUsersByIds(@RequestParam(name = "userIds", required = false) List<Long> userIds);
}
