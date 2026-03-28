package com.vsms.customer.domain.repository;

import com.vsms.customer.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Customer repository.
 * TODO: add query methods needed by CustomerServiceImpl
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    // TODO: List<Customer> findByIsActiveTrue();
    // TODO: Page<Customer> findByIsActiveTrue(Pageable pageable);
    // TODO: boolean existsByCustomerNameIgnoreCaseAndIsActiveTrue(String customerName);
    // TODO: boolean existsByGstNumberAndIsActiveTrue(String gstNumber);
    // TODO: boolean existsByPanNumberAndIsActiveTrue(String panNumber);
    // TODO: Optional<Customer> findByIdAndIsActiveTrue(UUID id);
}
