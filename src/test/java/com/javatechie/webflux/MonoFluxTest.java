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

    @DisplayName("тестирование подписки на МОНО и объекта IraReqRef. Просмотр данных через вывод в консоль log()")
    @Test
    public void testMono3() {
        IraReqRef iraReqRef = new IraReqRef(new IraReqId("1234567"));
        Mono<?> monoString1 = Mono.just(iraReqRef)
                .log(); //выводит все шаги в консоль
        monoString1.subscribe(System.out::println, (e) -> System.out.println(e.getMessage()));
        //имеем такой вывод:
        //20:23:25.397 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
        //20:23:25.413 [main] INFO reactor.Mono.Just.1 - | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
        //20:23:25.416 [main] INFO reactor.Mono.Just.1 - | request(unbounded)
        //20:23:25.416 [main] INFO reactor.Mono.Just.1 - | onNext(IraReqRef(iraReqId=IraReqId(value=1234567)))
        //IraReqRef(iraReqId=IraReqId(value=1234567))
        //20:23:25.417 [main] INFO reactor.Mono.Just.1 - | onComplete()
    }

    @DisplayName("тестирование подписки на МОНО и объекта IraReqRef с ошибкой. Просмотр данных через вывод в консоль log()")
    @Test
    public void testMono4() {
        IraReqRef iraReqRef = new IraReqRef(new IraReqId("1234567"));
        Mono<?> monoString1 = Mono.just(iraReqRef)
                .then(Mono.error(new RuntimeException("Exception occured")))
                .log(); //выводит все шаги в консоль
        monoString1.subscribe(System.out::println, (e) -> System.out.println(e.getMessage()));
        //имеем такой вывод:
        //20:25:29.342 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
        //20:25:29.356 [main] INFO reactor.Mono.IgnoreThen.1 - | onSubscribe([Fuseable] MonoIgnoreThen.ThenIgnoreMain)
        //20:25:29.358 [main] INFO reactor.Mono.IgnoreThen.1 - | request(unbounded)
        //20:25:29.361 [main] ERROR reactor.Mono.IgnoreThen.1 - | onError(java.lang.RuntimeException: Exception occured)
        //20:25:29.364 [main] ERROR reactor.Mono.IgnoreThen.1 -
        //java.lang.RuntimeException: Exception occured
        //....
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

    @DisplayName("тестирование подписки на Flux и объект IraReqRef. Просмотр данных через вывод в консоль log()")
    @Test
    public void testFlux3() {
        IraReqRef iraReqRef1 = new IraReqRef(new IraReqId("111111"));
        IraReqRef iraReqRef2 = new IraReqRef(new IraReqId("222222"));
        IraReqRef iraReqRef3 = new IraReqRef(new IraReqId("333333"));
        IraReqRef iraReqRef4 = new IraReqRef(new IraReqId("444444"));
        IraReqRef iraReqRef5 = new IraReqRef(new IraReqId("555555"));
        IraReqRef iraReqRef6 = new IraReqRef(new IraReqId("666666"));

        Flux<IraReqRef> fluxString1 = Flux.just(iraReqRef1, iraReqRef2, iraReqRef3, iraReqRef4)
                .concatWithValues(iraReqRef5)
                .concatWithValues(iraReqRef6)
                .log();
        fluxString1.subscribe(System.out::println, (e) -> System.out.println(e.getMessage()));
        //
        //20:29:22.206 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
        //20:29:22.219 [main] INFO reactor.Flux.ConcatArray.1 - onSubscribe(FluxConcatArray.ConcatArraySubscriber)
        //20:29:22.221 [main] INFO reactor.Flux.ConcatArray.1 - request(unbounded)
        //20:29:22.222 [main] INFO reactor.Flux.ConcatArray.1 - onNext(IraReqRef(iraReqId=IraReqId(value=111111)))
        //IraReqRef(iraReqId=IraReqId(value=111111))
        //20:29:22.222 [main] INFO reactor.Flux.ConcatArray.1 - onNext(IraReqRef(iraReqId=IraReqId(value=222222)))
        //IraReqRef(iraReqId=IraReqId(value=222222))
        //20:29:22.222 [main] INFO reactor.Flux.ConcatArray.1 - onNext(IraReqRef(iraReqId=IraReqId(value=333333)))
        //IraReqRef(iraReqId=IraReqId(value=333333))
        //20:29:22.222 [main] INFO reactor.Flux.ConcatArray.1 - onNext(IraReqRef(iraReqId=IraReqId(value=444444)))
        //IraReqRef(iraReqId=IraReqId(value=444444))
        //20:29:22.223 [main] INFO reactor.Flux.ConcatArray.1 - onNext(IraReqRef(iraReqId=IraReqId(value=555555)))
        //IraReqRef(iraReqId=IraReqId(value=555555))
        //20:29:22.223 [main] INFO reactor.Flux.ConcatArray.1 - onNext(IraReqRef(iraReqId=IraReqId(value=666666)))
        //IraReqRef(iraReqId=IraReqId(value=666666))
        //20:29:22.223 [main] INFO reactor.Flux.ConcatArray.1 - onComplete()
    }

    @DisplayName("тестирование подписки на Flux и объект IraReqRef с ошибкой. Просмотр данных через вывод в консоль log()")
    @Test
    public void testFlux4() {
        IraReqRef iraReqRef1 = new IraReqRef(new IraReqId("111111"));
        IraReqRef iraReqRef2 = new IraReqRef(new IraReqId("222222"));
        IraReqRef iraReqRef3 = new IraReqRef(new IraReqId("333333"));
        IraReqRef iraReqRef4 = new IraReqRef(new IraReqId("444444"));
        IraReqRef iraReqRef5 = new IraReqRef(new IraReqId("555555"));
        IraReqRef iraReqRef6 = new IraReqRef(new IraReqId("666666"));

        Flux<IraReqRef> fluxString1 = Flux.just(iraReqRef1, iraReqRef2, iraReqRef3, iraReqRef4)
                .concatWithValues(iraReqRef5)
                .concatWith(Flux.error(new RuntimeException("Exception occured in Flux")))
                .concatWithValues(iraReqRef6)
                .log();
        fluxString1.subscribe(System.out::println, (e) -> System.out.println(e.getMessage()));
        //
        //20:30:53.925 [main] DEBUG reactor.util.Loggers - Using Slf4j logging framework
        //20:30:53.940 [main] INFO reactor.Flux.ConcatArray.1 - onSubscribe(FluxConcatArray.ConcatArraySubscriber)
        //20:30:53.942 [main] INFO reactor.Flux.ConcatArray.1 - request(unbounded)
        //20:30:53.943 [main] INFO reactor.Flux.ConcatArray.1 - onNext(IraReqRef(iraReqId=IraReqId(value=111111)))
        //IraReqRef(iraReqId=IraReqId(value=111111))
        //20:30:53.944 [main] INFO reactor.Flux.ConcatArray.1 - onNext(IraReqRef(iraReqId=IraReqId(value=222222)))
        //IraReqRef(iraReqId=IraReqId(value=222222))
        //20:30:53.944 [main] INFO reactor.Flux.ConcatArray.1 - onNext(IraReqRef(iraReqId=IraReqId(value=333333)))
        //IraReqRef(iraReqId=IraReqId(value=333333))
        //20:30:53.944 [main] INFO reactor.Flux.ConcatArray.1 - onNext(IraReqRef(iraReqId=IraReqId(value=444444)))
        //IraReqRef(iraReqId=IraReqId(value=444444))
        //20:30:53.944 [main] INFO reactor.Flux.ConcatArray.1 - onNext(IraReqRef(iraReqId=IraReqId(value=555555)))
        //IraReqRef(iraReqId=IraReqId(value=555555))
        //20:30:53.945 [main] ERROR reactor.Flux.ConcatArray.1 - onError(java.lang.RuntimeException: Exception occured in Flux)
        //20:30:53.947 [main] ERROR reactor.Flux.ConcatArray.1 -
        //java.lang.RuntimeException: Exception occured in Flux
        //......
        //Exception occured in Flux
    }

}
