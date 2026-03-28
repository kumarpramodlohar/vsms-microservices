package com.vsms.inventory.domain.repository;

import com.vsms.inventory.domain.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Stock transaction repository.
 * TODO: add query methods needed by StockServiceImpl
 */
@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    // TODO: List<StockTransaction> findByIsActiveTrue();
    // TODO: Page<StockTransaction> findByIsActiveTrue(Pageable pageable);
    // TODO: Optional<StockTransaction> findByIdAndIsActiveTrue(Long id);
}
