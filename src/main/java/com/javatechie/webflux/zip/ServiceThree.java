package com.javatechie.webflux.zip;

import reactor.core.publisher.Mono;

public class ServiceThree {
    public Mono<String> methodThree(CustomObject customObject) {
        String firstName = customObject.getFirstName();
        String lastName = customObject.getLastName();
        String upperCase = firstName.toUpperCase();
        String lowerCase = lastName.toLowerCase();
        return Mono.just(upperCase + "-" + lowerCase);
    }
}
