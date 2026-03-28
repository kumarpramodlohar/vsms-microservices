package com.vsms.fulfilment.domain.repository;

import com.vsms.fulfilment.domain.entity.InvoiceHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Invoice header repository.
 * TODO: add query methods needed by InvoiceServiceImpl
 */
@Repository
public interface InvoiceHeaderRepository extends JpaRepository<InvoiceHeader, Long> {

    // TODO: List<InvoiceHeader> findByIsActiveTrue();
    // TODO: Page<InvoiceHeader> findByIsActiveTrue(Pageable pageable);
    // TODO: Optional<InvoiceHeader> findByIdAndIsActiveTrue(Long id);
    // TODO: Optional<InvoiceHeader> findByInvoiceNumberAndIsActiveTrue(String invoiceNumber);
}
