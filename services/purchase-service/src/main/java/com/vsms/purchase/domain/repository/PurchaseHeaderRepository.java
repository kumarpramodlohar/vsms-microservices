package com.vsms.purchase.domain.repository;

import com.vsms.purchase.domain.entity.PurchaseHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Purchase header repository.
 * TODO: add query methods needed by PurchaseServiceImpl
 */
@Repository
public interface PurchaseHeaderRepository extends JpaRepository<PurchaseHeader, Long> {

    // TODO: List<PurchaseHeader> findByIsActiveTrue();
    // TODO: Page<PurchaseHeader> findByIsActiveTrue(Pageable pageable);
    // TODO: Optional<PurchaseHeader> findByIdAndIsActiveTrue(Long id);
}
