package com.vsms.master.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for master/reference data management.
 * TODO: migrate logic from com.vsms.master.api.controller.MasterController in monolith
 * Covers: items, UOM, categories, locations, companies, terms, call types, etc.
 */
@RestController
@RequestMapping("/api/v1/master")
@RequiredArgsConstructor
@Tag(name = "Master Data", description = "Reference data service — items, UOM, locations, companies, lookup tables")
public class MasterController {

    // TODO: inject ItemService, CompanyService, LocationService, UomService, etc.

    // ── Items ──────────────────────────────────────────────────────────────────

    @PostMapping("/items")
    public ResponseEntity<?> createItem(@RequestBody Object request) {
        // TODO: implement createItem — migrate from com.vsms.master.service.impl.ItemServiceImpl
        throw new UnsupportedOperationException("TODO: implement createItem");
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        // TODO: implement getItemById
        throw new UnsupportedOperationException("TODO: implement getItemById");
    }

    @GetMapping("/items")
    public ResponseEntity<?> getAllItems() {
        // TODO: implement getAllItems with pagination
        throw new UnsupportedOperationException("TODO: implement getAllItems");
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody Object request) {
        // TODO: implement updateItem
        throw new UnsupportedOperationException("TODO: implement updateItem");
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        // TODO: implement soft delete
        throw new UnsupportedOperationException("TODO: implement deleteItem");
    }

    // ── Companies ─────────────────────────────────────────────────────────────

    @GetMapping("/companies/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        // TODO: implement getCompanyById — used by sales-service and fulfilment-service Feign clients
        throw new UnsupportedOperationException("TODO: implement getCompanyById");
    }

    @GetMapping("/companies")
    public ResponseEntity<?> getAllCompanies() {
        // TODO: implement getAllCompanies
        throw new UnsupportedOperationException("TODO: implement getAllCompanies");
    }

    // ── Locations ─────────────────────────────────────────────────────────────

    @GetMapping("/locations/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable Long id) {
        // TODO: implement getLocationById — used by hr-service Feign client
        throw new UnsupportedOperationException("TODO: implement getLocationById");
    }

    // ── UOM ───────────────────────────────────────────────────────────────────

    @GetMapping("/uom/{id}")
    public ResponseEntity<?> getUomById(@PathVariable Long id) {
        // TODO: implement getUomById — used by inventory-service Feign client
        throw new UnsupportedOperationException("TODO: implement getUomById");
    }

    // ── States / Countries ────────────────────────────────────────────────────

    @GetMapping("/states/{id}")
    public ResponseEntity<?> getStateById(@PathVariable Long id) {
        // TODO: implement getStateById
        throw new UnsupportedOperationException("TODO: implement getStateById");
    }

    @GetMapping("/countries/{id}")
    public ResponseEntity<?> getCountryById(@PathVariable Long id) {
        // TODO: implement getCountryById
        throw new UnsupportedOperationException("TODO: implement getCountryById");
    }
}