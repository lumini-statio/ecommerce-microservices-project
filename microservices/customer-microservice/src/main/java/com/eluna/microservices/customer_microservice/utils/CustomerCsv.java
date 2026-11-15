package com.eluna.microservices.customer_microservice.utils;

public record CustomerCsv(String firstName, String lastName, String email,
                          String phone, String address, String city) {}
