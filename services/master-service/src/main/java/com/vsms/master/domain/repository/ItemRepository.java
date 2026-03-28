package com.vsms.master.domain.repository;

import com.vsms.master.domain.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Item repository.
 * TODO: add query methods needed by ItemServiceImpl
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // TODO: List<Item> findByIsActiveTrue();
    // TODO: Page<Item> findByIsActiveTrue(Pageable pageable);
    // TODO: boolean existsByItemCodeIgnoreCaseAndIsActiveTrue(String itemCode);
}