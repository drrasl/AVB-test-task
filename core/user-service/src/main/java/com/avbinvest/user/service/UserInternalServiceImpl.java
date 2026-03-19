package com.avbinvest.user.service;

import com.avbinvest.user.dto.UserShortDto;
import com.avbinvest.user.dto.mapper.UserMapper;
import com.avbinvest.user.model.User;
import com.avbinvest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInternalServiceImpl implements UserInternalService{

    private final UserRepository repository;

    @Override
    public List<UserShortDto> getUsersByIds(List<Long> userIds) {
        log.debug("Internal Service: Request to provide users received");
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        List<User> users = repository.findByIdIn(userIds);
        if (users.isEmpty()) {
            log.debug("No users found in database");
            return List.of();
        }
        return users.stream()
                .map(UserMapper::toUserShortDto)
                .collect(Collectors.toList());
    }
}
