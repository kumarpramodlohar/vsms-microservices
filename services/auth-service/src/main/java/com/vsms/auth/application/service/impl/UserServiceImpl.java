package com.vsms.auth.application.service.impl;

import com.vsms.auth.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User service implementation.
 * TODO: migrate business logic from com.vsms.admin.service.impl.UserServiceImpl in monolith
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    // TODO: inject UserRepository
    // TODO: inject PasswordEncoder (replace plaintext password handling from monolith)

    // TODO: implement createUser
    // TODO: implement getUserById
    // TODO: implement getAllUsers
    // TODO: implement updateUser
    // TODO: implement deleteUser (soft delete via is_active = false)
    // TODO: implement login — validate credentials, return JWT
}