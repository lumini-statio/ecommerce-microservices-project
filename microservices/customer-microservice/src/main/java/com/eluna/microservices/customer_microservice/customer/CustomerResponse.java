package com.eluna.microservices.customer_microservice.customer;

import lombok.Builder;

/**
 * CustomerResponse
 */
@Builder 
public record CustomerResponse(
    String id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String address,
    String city
) {

}
