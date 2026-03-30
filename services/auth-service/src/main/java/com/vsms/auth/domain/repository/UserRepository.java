package com.vsms.auth.domain.repository;

import com.vsms.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User repository for database operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by userId and active status.
     */
    Optional<User> findByUserIdAndIsActiveTrue(String userId);

    /**
     * Check if user exists by userId (case-insensitive) and active status.
     */
    boolean existsByUserIdIgnoreCaseAndIsActiveTrue(String userId);

    /**
     * Check if email exists and is active.
     */
    boolean existsByEmailIgnoreCaseAndIsActiveTrue(String email);

    /**
     * Find all active users.
     */
    List<User> findByIsActiveTrue();

    /**
     * Find user by email and active status.
     */
    Optional<User> findByEmailIgnoreCaseAndIsActiveTrue(String email);

    /**
     * Find users by userType and active status.
     */
    List<User> findByUserTypeAndIsActiveTrue(String userType);

    /**
     * Find users by active status.
     */
    @Query("SELECT u FROM User u WHERE u.isActive = :isActive")
    List<User> findByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Count active users.
     */
    long countByIsActiveTrue();
}
