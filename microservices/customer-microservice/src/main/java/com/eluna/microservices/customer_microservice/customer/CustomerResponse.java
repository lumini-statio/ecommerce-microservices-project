package com.eluna.microservices.customer_microservice.customer;

import lombok.Builder;

import java.util.UUID;

/**
 * CustomerResponse
 */
@Builder 
public record CustomerResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String address,
    String city
) {

}
