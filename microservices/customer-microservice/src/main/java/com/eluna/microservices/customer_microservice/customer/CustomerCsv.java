package com.eluna.microservices.customer_microservice.customer;

public record CustomerCsv(String firstName, String lastName, String email,
                          String phone, String address, String city) {}
