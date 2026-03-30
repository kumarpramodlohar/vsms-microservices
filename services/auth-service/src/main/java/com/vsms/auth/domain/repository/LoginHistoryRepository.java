package com.vsms.auth.domain.repository;

import com.vsms.auth.domain.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LoginHistory repository for database operations.
 */
@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Integer> {

    /**
     * Find login history by user ID and active status.
     */
    List<LoginHistory> findByUserIdAndIsActiveTrue(String userId);

    /**
     * Find login history by user ID between dates.
     */
    List<LoginHistory> findByUserIdAndLoginDtTimeBetweenAndIsActiveTrue(
            String userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all active login history.
     */
    List<LoginHistory> findByIsActiveTrue();

    /**
     * Find login history by active status.
     */
    @Query("SELECT lh FROM LoginHistory lh WHERE lh.isActive = :isActive")
    List<LoginHistory> findByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Count login history by user ID.
     */
    long countByUserIdAndIsActiveTrue(String userId);

    /**
     * Find recent login history by user ID.
     */
    @Query("SELECT lh FROM LoginHistory lh WHERE lh.userId = :userId AND lh.isActive = true ORDER BY lh.loginDtTime DESC")
    List<LoginHistory> findRecentByUserId(@Param("userId") String userId);
}
