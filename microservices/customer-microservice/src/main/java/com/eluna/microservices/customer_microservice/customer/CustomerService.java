package com.eluna.microservices.customer_microservice.customer;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    public final CustomerRepository repository;
    public final CustomerMapper mapper;

    public String saveCustomer(CustomerRequest request) {
        var customer = mapper.toCustomer(request);
        var savedCustomer = repository.save(customer);
        return savedCustomer.getId();
    }

    public CustomerResponse getCustomerById(String customerId) {
        return repository.findById(customerId)
            .map(mapper::toCustomerResponse)
            .orElseThrow();
    }

    public List<CustomerResponse> getCustomers() {
        return repository.findAll()
            .stream()
            .map(mapper::toCustomerResponse)
            .toList();
    }

    public void deleteCustomerById(String customerId) {
        repository.findById(customerId)
        .orElseThrow();
        repository.deleteById(customerId);
    }

}
