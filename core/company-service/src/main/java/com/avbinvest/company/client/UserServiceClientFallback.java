package com.avbinvest.company.client;

import com.avbinvest.company.dto.UserShortDto;

import java.util.List;

public class UserServiceClientFallback implements UserClient {
    @Override
    public List<UserShortDto> getUsersByIds(List<Long> userIds) {
        throw new RuntimeException("Fallback response: Client service is unavailable during request users by ids");
    }
}
