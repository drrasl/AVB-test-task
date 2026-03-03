package com.avbinvest.user.service;

import com.avbinvest.user.client.CompanyClient;
import com.avbinvest.user.dto.*;
import com.avbinvest.user.dto.mapper.UserMapper;
import com.avbinvest.user.exception.CompanyNotFoundException;
import com.avbinvest.user.exception.DuplicateUserException;
import com.avbinvest.user.exception.UserNotFoundException;
import com.avbinvest.user.model.User;
import com.avbinvest.user.repository.UserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final CompanyClient companyClient;

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Service: Request to provide all users received");
        List<User> users = repository.findAll();
        if (users.isEmpty()) {
            log.debug("No users found in database");
            return List.of();
        }
        List<Long> companyIds = users.stream()
                .map(User::getCompanyId)
                .distinct()
                .toList();
        Map<Long, CompanyShortDto> companies = getCompaniesByCompanyIds(companyIds);
        List<UserDto> userDtos = users.stream()
                .map(user -> {
                    UserDto userDto = UserMapper.toUserDto(user);
                    CompanyShortDto companyShortDto = companies.get(user.getCompanyId());
                    userDto.setCompanyDto(companyShortDto);
                            return userDto;
                })
                .toList();
        return userDtos;
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        try {
            //User belongs to the company, so Company should be saved in Company DB before save the user.
            validateIfCompanyExists(newUserRequest.getCompanyId());
            log.debug("Service: Add a new user: {}", newUserRequest);
            User savedUser = repository.save(UserMapper.toUser(newUserRequest));
            log.debug("Service: User is saved in repository, {}", savedUser);
            CompanyShortDto company = getCompanyByCompanyId(savedUser.getCompanyId());
            UserDto userDto = UserMapper.toUserDto(savedUser);
            userDto.setCompanyDto(company);
            log.debug("Service: Return userDto from service to controller successfully, {}", userDto);
            return userDto;
        } catch (DataIntegrityViolationException e) {
            log.info("User with details: {} {} {} has already existed", newUserRequest.getFirstName(),
                    newUserRequest.getLastName(), newUserRequest.getPhone());
            throw new DuplicateUserException("User already exists: " + newUserRequest);
        }
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        log.debug("Service: Start to Update existed user: {}", updateUserRequest);
        boolean isUpdated = false;

        if (updateUserRequest.getFirstName() != null &&
                !updateUserRequest.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(updateUserRequest.getFirstName());
            isUpdated = true;
        }
        if (updateUserRequest.getLastName() != null &&
                !updateUserRequest.getLastName().equals(user.getLastName())) {
            user.setLastName(updateUserRequest.getLastName());
            isUpdated = true;
        }
        if (updateUserRequest.getPhone() != null &&
                !updateUserRequest.getPhone().equals(user.getPhone())) {
            user.setPhone(updateUserRequest.getPhone());
            isUpdated = true;
        }
        if (updateUserRequest.getCompanyId() != null &&
                !updateUserRequest.getCompanyId().equals(user.getCompanyId())) {
            validateIfCompanyExists(updateUserRequest.getCompanyId());
            user.setCompanyId(updateUserRequest.getCompanyId());
            isUpdated = true;
        }
        CompanyShortDto company = getCompanyByCompanyId(user.getCompanyId());
        if (!isUpdated) {
            log.debug("No changes detected for user id: {}", userId);
            UserDto userDto = UserMapper.toUserDto(user);
            userDto.setCompanyDto(company);
            return userDto;
        }
        User updatedUser = repository.save(user);
        log.debug("Service: User is updated and saved in repository, {}", updatedUser);
        UserDto userDto = UserMapper.toUserDto(updatedUser);
        userDto.setCompanyDto(company);
        log.debug("Service: Return userDto from service to controller successfully, {}", userDto);
        return userDto;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("Service: Start of deleting the user with id: {}", userId);
        if (!repository.existsById(userId)) {
            log.info("User with id: {} was not found", userId);
            throw new UserNotFoundException("User with id: " + userId + " was not found");
        }
        repository.deleteById(userId);
        log.debug("User with Id: {} was deleted", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        log.debug("Service: Start of return the user with id: {}", userId);
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        CompanyShortDto company = getCompanyByCompanyId(user.getCompanyId());
        UserDto userDto = UserMapper.toUserDto(user);
        userDto.setCompanyDto(company);
        log.info("User with Id: {} found and returned", userId);
        return userDto;
    }

    private CompanyShortDto getCompanyByCompanyId(Long companyId) {
        try {
            log.debug("Receiving data CompanyShortDto from company-service via feign - client");
            return companyClient.getCompanyById(companyId);
        } catch (Exception e) {
            log.error("Failed to get CompanyShortDto from company-service: {}. Return null-Dto", e.getMessage());
            return CompanyShortDto.builder().build();
        }
    }

    private Map<Long, CompanyShortDto> getCompaniesByCompanyIds(List<Long> companyIds) {
        if (companyIds.isEmpty()) {
            log.debug("No company IDs to forward");
            return Map.of();
        }
        try {
            log.debug("Receiving data List<CompanyShortDto> from company-service via feign - client");
            List<CompanyShortDto> companies = companyClient.getCompaniesByIds(companyIds);
            log.debug("Successfully received {} companies", companies.size());
            return companies.stream()
                    .collect(Collectors.toMap(CompanyShortDto::getId, Function.identity()));
        } catch (Exception e) {
            log.error("Failed to get List<CompanyShortDto> from company-service: {}. Return zero List", e.getMessage());
            return Map.of();
        }
    }

    private void validateIfCompanyExists(Long companyId) {
        try {
            log.debug("Receiving confirmation that the company is existed via feign - client");
            CompanyShortDto companyShortDto =  companyClient.getCompanyById(companyId);
        } catch (FeignException.NotFound e) {
            throw new CompanyNotFoundException("Company not found with id: " + companyId);
        }
    }
}
