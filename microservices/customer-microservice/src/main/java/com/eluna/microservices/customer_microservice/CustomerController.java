package com.eluna.microservices.customer_microservice;

import com.eluna.microservices.customer_microservice.services.CustomerImportService;
import com.eluna.microservices.customer_microservice.services.CustomerService;
import com.eluna.microservices.customer_microservice.utils.CustomerRequest;
import com.eluna.microservices.customer_microservice.utils.CustomerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService service;
    private final CustomerImportService importService;

    @PostMapping
    public ResponseEntity<UUID> createCustomer(@RequestBody @Valid CustomerRequest request) {
        return ResponseEntity.ok(service.saveCustomer(request));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable("customerId") UUID customerId) {
        return ResponseEntity.ok(service.getCustomerById(customerId));
    }
    
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(service.getCustomers());
    }
    
    @PutMapping
    public ResponseEntity<Void> updateCustomer(@RequestBody @Valid CustomerRequest request) {
        service.saveCustomer(request);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable("customerId") UUID customerId) {
        service.deleteCustomerById(customerId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importCustomers(
            @RequestParam("file") MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El archivo está vacío"));
        }

        JobExecution execution = importService.importFromFile(file);
        BatchStatus status = execution.getStatus();

        Map<String, Object> body = Map.of(
                "jobExecutionId", execution.getId(),
                "status", status.toString());

        return status == BatchStatus.COMPLETED
                ? ResponseEntity.ok(body)
                : ResponseEntity.unprocessableEntity().body(body);
    }
}