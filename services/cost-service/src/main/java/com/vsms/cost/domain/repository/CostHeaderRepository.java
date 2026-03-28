package com.vsms.cost.domain.repository;

import com.vsms.cost.domain.entity.CostHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Cost header repository.
 * TODO: add query methods needed by CostServiceImpl
 */
@Repository
public interface CostHeaderRepository extends JpaRepository<CostHeader, Long> {

    // TODO: List<CostHeader> findByIsActiveTrue();
    // TODO: Page<CostHeader> findByIsActiveTrue(Pageable pageable);
    // TODO: Optional<CostHeader> findByIdAndIsActiveTrue(Long id);
    // TODO: Optional<CostHeader> findByOrderCodeAndIsActiveTrue(String orderCode);
}
