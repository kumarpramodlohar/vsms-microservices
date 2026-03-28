package com.vsms.auth.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management and authentication.
 * TODO: migrate logic from com.vsms.admin.api.controller.UserController in monolith
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth & User Management", description = "Identity and access management — user CRUD, login, JWT issuance")
public class UserController {

    // TODO: inject UserService

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Object request) {
        // TODO: validate credentials, issue JWT via Spring Authorization Server
        throw new UnsupportedOperationException("TODO: implement login");
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Object request) {
        // TODO: implement createUser — migrate from com.vsms.admin.application.service.impl.UserServiceImpl
        throw new UnsupportedOperationException("TODO: implement createUser");
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        // TODO: implement getUserById
        throw new UnsupportedOperationException("TODO: implement getUserById");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        // TODO: implement getAllUsers with pagination
        throw new UnsupportedOperationException("TODO: implement getAllUsers");
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Object request) {
        // TODO: implement updateUser
        throw new UnsupportedOperationException("TODO: implement updateUser");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        // TODO: implement soft delete
        throw new UnsupportedOperationException("TODO: implement deleteUser");
    }

    @GetMapping("/jwks")
    public ResponseEntity<?> jwks() {
        // TODO: expose JWK Set for downstream resource servers (Spring Authorization Server provides this automatically)
        throw new UnsupportedOperationException("TODO: configure Spring Authorization Server JWKS endpoint");
    }
}