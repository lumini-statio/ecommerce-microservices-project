package com.eluna.microservices.customer_microservice.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
        String id,
        @NotNull(message = "first name is required.")
        String firstName,
        @NotNull(message = "last name is required.")
        String lastName,
        @NotNull(message = "email is required.")
        @Email(message = "email is not valid.")
        String email,
        String phone,
        String address,
        String city
) {
}
