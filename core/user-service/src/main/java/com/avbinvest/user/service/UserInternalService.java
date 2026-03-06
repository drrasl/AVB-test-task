package com.avbinvest.user.service;

import com.avbinvest.user.dto.UserShortDto;

import java.util.List;

public interface UserInternalService {
    List<UserShortDto> getUsersByIds(List<Long> userIds);
}
