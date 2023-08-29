package com.javatechie.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}
