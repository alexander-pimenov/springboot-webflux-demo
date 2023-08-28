package com.javatechie.webflux.zip;

import reactor.core.publisher.Mono;

public class ServiceTwo {
    public Mono<String> methodTwo() {
        return Mono.just("La-la-la");
    }
}
