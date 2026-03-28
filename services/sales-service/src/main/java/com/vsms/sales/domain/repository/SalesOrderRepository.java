package com.vsms.sales.domain.repository;

import com.vsms.sales.domain.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Sales order repository.
 * TODO: add query methods needed by SalesOrderServiceImpl
 */
@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    // TODO: Optional<SalesOrder> findByOrderCodeAndIsActiveTrue(String orderCode);
    // TODO: List<SalesOrder> findByIsActiveTrue();
    // TODO: Page<SalesOrder> findByIsActiveTrue(Pageable pageable);
    // TODO: boolean existsByOrderCodeAndIsActiveTrue(String orderCode);
}
