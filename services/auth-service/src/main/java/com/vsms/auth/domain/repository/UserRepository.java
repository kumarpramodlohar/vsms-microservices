package com.vsms.auth.domain.repository;

import com.vsms.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User repository.
 * TODO: add query methods needed by UserServiceImpl
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // TODO: Optional<User> findByUsernameAndIsActiveTrue(String username);
    // TODO: boolean existsByUsernameIgnoreCaseAndIsActiveTrue(String username);
    // TODO: List<User> findByIsActiveTrue();
}