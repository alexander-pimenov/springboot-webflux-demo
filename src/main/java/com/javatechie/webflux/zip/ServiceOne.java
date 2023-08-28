package com.javatechie.webflux.zip;

import reactor.core.publisher.Mono;

public class ServiceOne {
    public Mono<CustomObject> methodOne() {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        CustomObject co = new CustomObject("Alex", "Rich");
        return Mono.just(co);
    }
}
