package com.eluna.microservices.customer_microservice.utils;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CustomerRequest(
        UUID id,
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
