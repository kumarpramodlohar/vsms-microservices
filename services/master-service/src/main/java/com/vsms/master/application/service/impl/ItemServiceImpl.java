package com.vsms.master.application.service.impl;

import com.vsms.master.application.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Item service implementation.
 * TODO: migrate business logic from com.vsms.master.service.impl.ItemServiceImpl in monolith
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    // TODO: inject ItemRepository
    // TODO: inject ItemMapper

    // TODO: implement createItem
    // TODO: implement getItemById — throw ResourceNotFoundException if not found
    // TODO: implement getAllItems with pagination
    // TODO: implement updateItem
    // TODO: implement deleteItem (soft delete via is_active = false)
}