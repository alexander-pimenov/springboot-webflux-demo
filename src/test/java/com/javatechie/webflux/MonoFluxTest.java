package com.javatechie.webflux;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoFluxTest {


    @DisplayName("тестирование подписки на МОНО. Просмотр данных через вывод в консоль log()")
    @Test
    public void testMono1() {
        Mono<?> monoString1 = Mono.just("javatechie")
                .log(); //выводит все шаги в консоль
        monoString1.subscribe(System.out::println, (e) -> System.out.println(e.getMessage()));
        //имеем такой вывод:
        //21:17:45.435 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
        //21:17:45.435 [main] INFO reactor.Mono.Just.1 - | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
        //21:17:45.435 [main] INFO reactor.Mono.Just.1 - | request(unbounded)
        //21:17:45.435 [main] INFO reactor.Mono.Just.1 - | onNext(javatechie)
        //javatechie
        //21:17:45.435 [main] INFO reactor.Mono.Just.1 - | onComplete()
    }

    @DisplayName("тестирование подписки на МОНО с ошибкой. Просмотр данных через вывод в консоль log()")
    @Test
    public void testMono2() {
        Mono<?> monoString2 = Mono.just("javatechie")
                .then(Mono.error(new RuntimeException("Exception occured")))
                .log(); //выводит все шаги в консоль
        monoString2.subscribe(System.out::println, (e) -> System.out.println(e.getMessage()));

        //имеем такой вывод
        //21:19:15.809 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
        //21:19:15.824 [main] INFO reactor.Mono.IgnoreThen.2 - | onSubscribe([Fuseable] MonoIgnoreThen.ThenIgnoreMain)
        //21:19:15.824 [main] INFO reactor.Mono.IgnoreThen.2 - | request(unbounded)
        //21:19:15.824 [main] ERROR reactor.Mono.IgnoreThen.2 - | onError(java.lang.RuntimeException: Exception occured)
        //21:19:15.840 [main] ERROR reactor.Mono.IgnoreThen.2 -
        //java.lang.RuntimeException: Exception occured
        //	....... скрыли много стектрейса
        //Exception occured
    }

    @DisplayName("тестирование подписки на Flux. Просмотр данных через вывод в консоль log()")
    @Test
    public void testFlux1() {
        //1
        Flux<String> fluxString1 = Flux.just("Spring", "Spring Boot", "Hibernate", "microservice")
                .concatWithValues("AWS")
                .concatWithValues("cloud")
                .log();
        fluxString1.subscribe(System.out::println, (e) -> System.out.println(e.getMessage()));
        //System.out::println - выводит сообoения в консоль
        //(e) -> System.out.println(e.getMessage()) - если ошибка, то
        //
        //21:23:28.860 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
        //21:23:28.872 [main] INFO reactor.Flux.ConcatArray.1 - onSubscribe(FluxConcatArray.ConcatArraySubscriber)
        //21:23:28.875 [main] INFO reactor.Flux.ConcatArray.1 - request(unbounded)
        //21:23:28.877 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Spring)
        //Spring
        //21:23:28.877 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Spring Boot)
        //Spring Boot
        //21:23:28.877 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Hibernate)
        //Hibernate
        //21:23:28.877 [main] INFO reactor.Flux.ConcatArray.1 - onNext(microservice)
        //microservice
        //21:23:28.878 [main] INFO reactor.Flux.ConcatArray.1 - onNext(AWS)
        //AWS
        //21:23:28.878 [main] INFO reactor.Flux.ConcatArray.1 - onNext(cloud)
        //cloud
        //21:23:28.878 [main] INFO reactor.Flux.ConcatArray.1 - onComplete()

        //1
        Flux<String> fluxString2 = Flux.just("Spring", "Spring Boot", "Hibernate", "microservice")
                .concatWithValues("AWS")
                .concatWithValues("cloud")
                .log();

        fluxString1.subscribe();
        //
        //21:27:09.923 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
        //21:27:09.939 [main] INFO reactor.Flux.ConcatArray.1 - onSubscribe(FluxConcatArray.ConcatArraySubscriber)
        //21:27:09.939 [main] INFO reactor.Flux.ConcatArray.1 - request(unbounded)
        //21:27:09.939 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Spring)
        //21:27:09.939 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Spring Boot)
        //21:27:09.939 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Hibernate)
        //21:27:09.939 [main] INFO reactor.Flux.ConcatArray.1 - onNext(microservice)
        //21:27:09.939 [main] INFO reactor.Flux.ConcatArray.1 - onNext(AWS)
        //21:27:09.939 [main] INFO reactor.Flux.ConcatArray.1 - onNext(cloud)
        //21:27:09.939 [main] INFO reactor.Flux.ConcatArray.1 - onComplete()
    }

    @DisplayName("тестирование подписки на Flux с ошибкой. Просмотр данных через вывод в консоль log()")
    @Test
    public void testFlux2() {
        Flux<String> fluxString2 = Flux.just("Spring", "Spring Boot", "Hibernate", "microservice")
                .concatWithValues("AWS")
                .concatWith(Flux.error(new RuntimeException("Exception occured in Flux")))
                .concatWithValues("cloud")
                .log();

        fluxString2.subscribe();
        //fluxString2.subscribe(System.out::println, (e) -> System.out.println(e.getMessage()));
        //
        //21:25:49.880 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
        //21:25:49.895 [main] INFO reactor.Flux.ConcatArray.1 - onSubscribe(FluxConcatArray.ConcatArraySubscriber)
        //21:25:49.895 [main] INFO reactor.Flux.ConcatArray.1 - request(unbounded)
        //21:25:49.895 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Spring)
        //Spring
        //21:25:49.895 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Spring Boot)
        //Spring Boot
        //21:25:49.895 [main] INFO reactor.Flux.ConcatArray.1 - onNext(Hibernate)
        //Hibernate
        //21:25:49.895 [main] INFO reactor.Flux.ConcatArray.1 - onNext(microservice)
        //microservice
        //21:25:49.895 [main] INFO reactor.Flux.ConcatArray.1 - onNext(AWS)
        //AWS
        //21:25:49.895 [main] ERROR reactor.Flux.ConcatArray.1 - onError(java.lang.RuntimeException: Exception occured in Flux)
        //21:25:49.895 [main] ERROR reactor.Flux.ConcatArray.1 -
        //java.lang.RuntimeException: Exception occured in Flux
        // .....скрыто много стектрейса
        //Exception occured in Flux

    }

}
