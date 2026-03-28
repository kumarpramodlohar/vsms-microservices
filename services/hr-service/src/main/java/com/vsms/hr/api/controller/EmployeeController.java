package com.vsms.hr.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for HR and employee management.
 * TODO: migrate logic from com.vsms.hr.api.controller.HrController in monolith
 */
@RestController
@RequestMapping("/api/v1/hr")
@RequiredArgsConstructor
@Tag(name = "HR Management", description = "Employee master, attendance, advances, payroll earning/deduction management")
public class EmployeeController {

    // TODO: inject EmployeeService

    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@RequestBody Object request) {
        // TODO: implement createEmployee — migrate from com.vsms.hr.service.impl.HrServiceImpl
        throw new UnsupportedOperationException("TODO: implement createEmployee");
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        // TODO: implement getEmployeeById
        throw new UnsupportedOperationException("TODO: implement getEmployeeById");
    }

    @GetMapping("/employees")
    public ResponseEntity<?> getAllEmployees() {
        // TODO: implement getAllEmployees with pagination
        throw new UnsupportedOperationException("TODO: implement getAllEmployees");
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Object request) {
        // TODO: implement updateEmployee
        throw new UnsupportedOperationException("TODO: implement updateEmployee");
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        // TODO: implement soft delete
        throw new UnsupportedOperationException("TODO: implement deleteEmployee");
    }

    @PostMapping("/attendance")
    public ResponseEntity<?> recordAttendance(@RequestBody Object request) {
        // TODO: implement recordAttendance — migrate from monolith mst_employee_attendance
        throw new UnsupportedOperationException("TODO: implement recordAttendance");
    }

    @GetMapping("/attendance")
    public ResponseEntity<?> getAttendance(@RequestParam(required = false) Long employeeId,
                                           @RequestParam(required = false) String month,
                                           @RequestParam(required = false) String year) {
        // TODO: implement getAttendance with filters
        throw new UnsupportedOperationException("TODO: implement getAttendance");
    }

    @PostMapping("/earning-deductions")
    public ResponseEntity<?> createEarningDeduction(@RequestBody Object request) {
        // TODO: implement createEarningDeduction (payroll component)
        throw new UnsupportedOperationException("TODO: implement createEarningDeduction");
    }

    @GetMapping("/payroll")
    public ResponseEntity<?> getPayrollData(@RequestParam(required = false) Integer month,
                                            @RequestParam(required = false) Integer year) {
        // TODO: implement getPayrollData — used by report-service HrClient
        throw new UnsupportedOperationException("TODO: implement getPayrollData");
    }
}
