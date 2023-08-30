package com.javatechie.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <a href="https://www.appsdeveloperblog.com/doon-callbacks-in-project-reactor/">source1</a>
 * <p>
 * Обратные вызовы doOn в Project Reactor
 * <p>
 * В Project Reactor есть операторы обратного вызова doOn, которые мы можем использовать для выполнения пользовательских
 * действий без изменения элементов в последовательности. Обратные вызовы doOn позволяют нам просматривать события,
 * создаваемые издателем (Mono или Flux).
 * <p>
 * Мы называем их операторами побочного эффекта, поскольку они не меняют исходную последовательность.
 * <p>
 * Часто используемые из них, такие как:
 * <p>
 * doOnSubscribe() – вызывается для каждой новой подписки от подписчика.
 * Мы можем просмотреть событие onSubscribe, которое происходит, когда подписчик подписывается на издателя.
 * Таким образом, мы можем получить объект подписки, отправляемый издателем.
 * <p>
 * doOnNext() – вызывается для каждого элемента, исходящего от издателя.
 * С помощью этого метода мы можем просмотреть все данные, отправляемые издателем.
 * <p>
 * doOnComplete() – вызывается, когда от издателя отправляется сигнал завершения.
 * Мы можем установить некоторый код, который будет выполняться, как только от издателя будет
 * отправлен сигнал завершения, используя метод doOnComplete() .
 * <p>
 * doOnError() – вызывается, когда от издателя отправляется сигнал ошибки.    (*stop machine*)
 * Метод doOnError() будет выполнен, если произойдет какое-то исключение и издатель отправит подписчику сигнал onError.
 * <p>
 * doFinally() – выполняется в конце как в успешном, так и в неудачном сценарии.
 * Если мы хотим получать уведомления как в успешных, так и в неудачных сценариях, мы будем
 * использовать метод doOnFinally().
 * <p>
 */
@Slf4j
public class DoOnCallbacksTest {

    /**
     * doOnSubscribe
     */
    @Test
    public void doOnSubscribe_Flux() {
        Flux.just("data1", "data2", "data3")
                .doOnSubscribe(subscription -> System.out.println("Subscription: " + subscription))
                .log()
                .subscribe(data -> log.info("Data: {}", data),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    /**
     * doOnNext
     */
    @Test
    public void doOnNext_Flux() {
        Flux.just("data1", "data2", "data3")
                .doOnNext(data -> System.out.println("Data: " + data))
                .log()
                .subscribe(data -> log.info("Data: {}", data),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    /**
     * doOnComplete
     */
    @Test
    public void doOnComplete_Flux() {
        Flux.just("data1", "data2", "data3")
                .doOnNext(data -> System.out.println("Data: " + data))
                .doOnComplete(() -> System.out.println("Издатель отправил сигнал завершения!"))
                .log()
                .subscribe(data -> log.info("Data: {}", data),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    /**
     * doOnError
     */
    @Test
    public void doOnError_Flux() {

        Mono mono = Mono.fromSupplier(() -> {
            throw new RuntimeException("произошла ошибка!"); // вызов сигнала onError от издателя (Mono)
        });

        mono.doOnError(err -> System.out.println("Ошибка: " + err))
                .log()
                .subscribe(data -> log.info("Data: {}", data));
    }

    @Test
    public void doOnError_Flux_2() {
        Flux.just(1, 2, 3)
                .concatWith(Flux.error(new RuntimeException("Exception occurred.")))
                .concatWith(Mono.just(4)) //не выполнится
                .doOnError(ex -> {
                    System.out.println("Exception caught: " + ex);
                    innerFunctionLikeTriggerFrom_Flux();
                }) // catch and print the exception
                .concatWith(Mono.just(5)) //не выполнится
                .concatWith(Mono.just(6)) //не выполнится
                .log()
                .subscribe(num -> log.info("Number: {}", num),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    @Test
    public void doOnFinally_Flux() {

        // успешный сценарий
        Mono.just("data")
                .doFinally(signal -> System.out.println(signal + " сигнал получен."))
                .log()
                .subscribe(data -> log.info("Number: {}", data),
                        (e) -> log.info("Error: {}", e.getMessage()));

        // неудавшийся сценарий
        Mono mono = Mono.fromSupplier(() -> {
            throw new RuntimeException("произошла ошибка!"); // вызов сигнала onError от издателя (Mono)
        });
        mono.doFinally(сигнал -> System.out.println(сигнал + " сигнал получен."))
                .log()
                .subscribe(data -> log.info("Number: {}", data));
    }

    private void innerFunctionLikeTriggerFrom_Flux() {
        Flux.just(1000, 2000)
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Mono.just(3000))
                .onErrorResume(err -> {
                    System.out.println("Error caught: " + err);
                    return Mono.just(40000000);
                })
                .concatWith(Mono.just(5000))
                .concatWith(Mono.just(6000))
                .log()
                .subscribe(num -> log.info("Number: {}", num),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    @Test
    public void monoDoOnMethods() {
        String name = "William Suane";
        Mono<Object> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(longNumber -> log.info("Request Received, starting doing something..."))
                .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
                .flatMap(s -> Mono.empty())
                .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s)) //will not be executed
                .doOnSuccess(s -> log.info("doOnSuccess executed {}", s));

        mono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"));

    }

    @Test
    public void monoDoOnError() {
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception"))
                .doOnError(e -> log.error("Error message: {}", e.getMessage()))
                .doOnNext(s -> log.info("Executing this doOnNext"))
                .log();

        StepVerifier.create(error)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void monoOnErrorResume() {
        String name = "William Suane";
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception"))
                .onErrorResume(s -> {
                    log.info("Inside On Error Resume");
                    return Mono.just(name);
                })
                .doOnError(e -> log.error("Error message: {}", e.getMessage()))
                .log();

        StepVerifier.create(error)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    public void monoOnErrorReturn() {
        String name = "William Suane";
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception"))
                .onErrorReturn("EMPTY")
                .onErrorResume(s -> {
                    log.info("Inside On Error Resume");
                    return Mono.just(name);
                })
                .doOnError(e -> log.error("Error message: {}", e.getMessage()))
                .log();

        StepVerifier.create(error)
                .expectNext("EMPTY")
                .verifyComplete();
    }

    /**
     * Отдаем только запрошенные количество объектов из потока.
     * С помощью оператора request(3)
     */
    @Test
    public void monoSubscriberConsumerSubscription() {
        String name1 = "William Suane [" + UUID.randomUUID() + "]";
        String name2 = "William Suane [" + UUID.randomUUID() + "]";
        String name3 = "William Suane [" + UUID.randomUUID() + "]";
        String name4 = "William Suane [" + UUID.randomUUID() + "]";
        String name5 = "William Suane [" + UUID.randomUUID() + "]";
        String name6 = "William Suane [" + UUID.randomUUID() + "]";

        Flux<String> flux = Flux.just(name1, name2, name3, name4, name5, name6)
                .log()
                .map(String::toUpperCase);
        log.info("=========================");

        flux.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"),
                subscription -> subscription.request(3));

        log.info("--------------------------");

//        StepVerifier.create(flux)
//                .expectNext(name1.toUpperCase())
//                .expectNext(name2.toUpperCase())
//                .expectNext(name3.toUpperCase())
//                .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerComplete() {
        String name = "William Suane";
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);

        mono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"));

        log.info("--------------------------");

        StepVerifier.create(mono)
                .expectNext(name.toUpperCase())
                .verifyComplete();
    }

    /**
     * Как видно из теста можно подписываться несколько раз на поток.
     * Тут подписались 4 раза и 4 раза получили результат. Тут это ошибка.
     */
    @Test
    public void monoSubscriberConsumerError() {
        String name = "William Suane";
        Mono<String> mono = Mono.just(name)
                .map(s -> {
                    throw new RuntimeException("*** Testing mono with error ****");
                });

        mono.subscribe(s -> log.info("Name {}", s), s -> log.error("Something bad happened!!!"));
        mono.subscribe(s -> log.info("Name {}", s), s -> log.error("Something bad happened!!!"));

        log.info("--------------------------");
        log.info("--------------------------");
        log.info("--------------------------");

        mono.subscribe(s -> log.info("Name {}", s), Throwable::printStackTrace);
        log.info("--------------------------");
        log.info("--------------------------");

        mono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"));

        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();
    }
}
