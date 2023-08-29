package com.javatechie.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <a href="https://www.appsdeveloperblog.com/exception-handling-in-project-reactor/">source1</a>
 * При работе с Reactive Streams, если во время отправки данных Издателем возникает ошибка, весь поток будет прерван,
 * а Потребителю будет отправлен сигнал onError. Никакие другие сигналы не будут отправлены после onError.
 * Но ошибки нужно обрабатывать, как и в try-catch.
 * <p>
 * В Project Reactor мы можем обрабатывать исключения, используя некоторые из следующих операторов:
 * <p>
 * onErrorReturn()
 * onErrorResume()
 * onErrorContinue()
 * onErrorMap()
 * doOnError()
 * <p>
 * <p>
 * <a href="https://stackoverflow.com/questions/67531829/how-to-handle-errors-in-reactive-spring-webflux">source2</a>
 * <p>
 * Всего существует шесть методов обработки ошибок, пять из которых обсуждаются ниже:
 * <p>
 * -= onErrorReturn : возвращает резервное значение для всего потока (Mono/Flux). =-
 * Например, если имеется поток из 10 элементов и ошибка возникает в элементе 3, то остальные 4,5,6… не будут выполнены,
 * вместо этого будет рассматриваться резервное значение. Таким образом, код восстановится после исключения. Kод полностью
 * восстановится и продолжил работу, как будто ошибки никогда не было. Т.е. сообщений об ошибке не будет, но будут пропущены
 * несколько элементов.
 * <p>
 * onErrorResume : возвращает резервное значение в терминах Mono/Flux для всего потока (mono/flux). Например, если имеется
 * поток из 10 элементов и ошибка возникает в элементе 3, то остальные 4,5,6… не будут выполнены, вместо этого будет
 * рассматриваться резервное значение.
 * Метод onErrorResume() принимает интерфейс Function и выдает результат, в данном случае Mono. Благодаря наличию здесь
 * Function, можно делать вызовы других функций.
 * <p>
 * onErrorContinue : потребляет (ошибку, данные) и НЕ разделяет их.
 * Он учитывает потребителя для элементов ошибки и оставляет нижестоящую цепочку как есть для хороших элементов.
 * Т.е. метод onErrorContinue() перехватывает исключение, элемент, вызвавший исключение, будет удален, а издатель продолжит
 * генерировать оставшиеся элементы. Например, если есть поток из 10 элементов и ошибка происходит в элементе 3, то все
 * элементы (от 1 до 10), кроме 3, будут выполняться нормально, но элемент 3 будет иметь другое выполнение, как указано
 * в потребителе onErrorContinue.
 * <p>
 * doOnError : поглощает ошибку и выбрасывает ее. Останавливает выполнение дальнейших элементов в потоке.     (*stop machine*)
 * Этот оператор является одним из обратных вызовов doOn в Project Reactor. Это не меняет исходную последовательность.
 * С помощью этого оператора мы можем перехватить исключение и выполнить некоторые действия, когда сигнал onError отправляется
 * от издателя, но код не может восстановиться, и ошибка все равно распространяется на вызывающую сторону.
 * <p>
 * onErrorMap : преобразовать одну ошибку в другую. Останавливает выполнение дальнейших элементов в потоке.    (*stop machine*)
 * Т.е. с помощью onErrorMap() код не может восстановиться после исключения. Этот метод только перехватывает исключение
 * и преобразует его из одного типа в другой.
 * <p>
 * Все эти пять методов представлены в трех вариантах:
 * <p>
 * - Просто: рассмотрим непосредственно ожидаемый аргумент.
 * - С исключением: учитывайте ожидаемый аргумент, если исключение соответствует предоставленному классу исключения.
 * - С предикатом: рассмотрите ожидаемый аргумент, если предикат дает истину.
 */
@Slf4j

public class ErrorHandlingTest {

    /**
     * onError
     */
    @Test
    public void onError_Flux() {
        Flux.just(2, 7, 10)
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Mono.just(12))
                .log()
                .subscribe(num -> log.info("Number: {}", num));
    }

    /**
     * onErrorReturn: вернуть резервное значение
     */
    @Test
    public void onErrorReturnDirectly_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorReturn(4)
                .subscribe(num -> log.info("Number: {}", num));
    }

    @Test
    public void onErrorReturnDirectly_Flux() {
        Flux.just(2, 7, 10)
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Mono.just(12))
                .concatWith(Mono.just(13))
                .onErrorReturn(72)
                .concatWith(Mono.just(55))
                .concatWith(Mono.just(56))
                .log()
                .subscribe(num -> log.info("Number: {}", num),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    @Test
    public void onErrorReturnIfArithmeticException_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorReturn(ArithmeticException.class, 4)
                .subscribe(num -> log.info("Number: {}", num));
    }

    @Test
    public void onErrorReturnIfPredicatePasses_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorReturn(error -> error instanceof ArithmeticException, 4)
                .subscribe(num -> log.info("Number: {}", num));
    }

    /**
     * onErrorResume: возвращает резервное значение в терминах Mono/Flux
     */
    @Test
    public void onErrorResume_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorResume(error -> Mono.just(4))
                .subscribe(num -> log.info("Number: {}", num));
    }

    @Test
    public void onErrorResume_Flux() {
        Flux.just(2, 7, 10)
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Mono.just(12))
                .onErrorResume(err -> {
                    System.out.println("Error caught: " + err);
                    innerFunctionLikeTriggerFromOnErrorResume_Flux();
                    return Mono.just(122);
                })
                .concatWith(Mono.just(55))
                .concatWith(Mono.just(56))
                .concatWith(Mono.just(57))
                .log()
                .subscribe(num -> log.info("Number: {}", num),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    private void innerFunctionLikeTriggerFromOnErrorResume_Flux() {
        Flux.just(100, 200, 300)
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Mono.just(400))
                .onErrorResume(err -> {
                    System.out.println("Error caught: " + err);
                    return Mono.just(40000000);
                })
                .concatWith(Mono.just(500))
                .concatWith(Mono.just(600))
                .log()
                .subscribe(num -> log.info("Number: {}", num),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    @Test
    public void onErrorResume_IfArithmeticException_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorResume(
                        ArithmeticException.class,
                        error -> Mono.just(4)
                )
                .subscribe(num -> log.info("Number: {}", num));
    }

    @Test
    public void onErrorResume_IfPredicatePasses_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorResume(
                        error -> error instanceof ArithmeticException,
                        error -> Mono.just(4)
                )
                .subscribe(num -> log.info("Number: {}", num));
    }

    /**
     * onErrorContinue: потребляет (ошибку, данные) и НЕ разделяет их.
     */
    @Test
    public void onErrorContinue_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorContinue((error, obj) -> log.info("error:[{}], obj:[{}]", error, obj))
                .subscribe(num -> log.info("Number: {}", num));
    }

    @Test
    public void onErrorContinue_Flux() {
        Flux.just(2, 7, 10, 8, 12, 22, 24)
                .map(element -> {
                    if (element == 8) {
                        throw new RuntimeException("Exception occurred!");
                    }
                    return element;
                }).onErrorContinue((ex, element) -> {
                    System.out.println("Exception caught: " + ex);
                    System.out.println("The element that caused the exception is: " + element);
                }).log()
                .subscribe(num -> log.info("Number: {}", num));
    }

    @Test
    public void onErrorContinue_IfArithmeticException_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorContinue(
                        ArithmeticException.class,
                        (error, obj) -> log.info("error:[{}], obj:[{}]", error, obj)
                )
                .subscribe(num -> log.info("Number: {}", num));
    }

    @Test
    public void onErrorContinue_IfPredicatePasses_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorContinue(
                        error -> error instanceof ArithmeticException,
                        (error, obj) -> log.info("error:[{}], obj:[{}]", error, obj)
                )
                .subscribe(num -> log.info("Number: {}", num));
    }

    /**
     * doOnError: поглощает ошибку и выбрасывает ее
     */
    @Test
    public void doOnError_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .doOnError(error -> log.info("caught error"))
                .subscribe(num -> log.info("Number: {}", num));
    }

    @Test
    public void doOnError_Flux() {
        Flux.just(1, 2, 3)
                .concatWith(Flux.error(new RuntimeException("Exception occurred.")))
                .concatWith(Mono.just(4))
                .doOnError(ex -> {
                    System.out.println("Exception caught: " + ex);
                    innerFunctionLikeTriggerFromDoOnError_Flux_withOnErrorResume();
                }) // catch and print the exception
                .concatWith(Mono.just(5)) //не выполнится
                .concatWith(Mono.just(6)) //не выполнится
                .log()
                .subscribe(num -> log.info("Number: {}", num),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    private void innerFunctionLikeTriggerFromDoOnError_Flux_withOnErrorResume() {
        Flux.just(1000, 2000)
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Mono.just(3000))
                .onErrorResume(err -> {
                    System.out.println("Error caught: " + err);
                    innerFunctionLikeTriggerFromOnErrorResume_Flux();
                    return Mono.just(40000000);
                })
                .concatWith(Mono.just(5000))
                .concatWith(Mono.just(6000))
                .log()
                .subscribe(num -> log.info("Number: {}", num),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    @Test
    public void doOnError_IfArithmeticException_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .doOnError(
                        ArithmeticException.class,
                        error -> log.info("caught error")
                )
                .subscribe(num -> log.info("Number: {}", num));
    }

    @Test
    public void doOnError_IfPredicatePasses_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .doOnError(
                        error -> error instanceof ArithmeticException,
                        error -> log.info("caught error")
                )
                .subscribe(num -> log.info("Number: {}", num));
    }

    /**
     * onErrorMap: преобразовать одну ошибку в другую
     */
    @Test
    public void OnErrorMap_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorMap(error -> new RuntimeException("SomeMathException"))
                .subscribe(num -> log.info("Number: {}", num));
    }

    @Test
    public void OnErrorMap_Flux() {
        Flux.just(2, 7, 10, 8, 12, 22, 24)
                .map(element -> {
                    if (element == 8) {
                        throw new RuntimeException("Exception occurred!");
                    }
                    return element;
                }).onErrorMap(ex -> {
                    System.out.println("Exception caught: " + ex);
                    return new CustomException(ex.getMessage(), ex);
                })
                .concatWith(Mono.just(25)) //не выполнится
                .concatWith(Mono.just(26)) //не выполнится
                .log()
                .subscribe(num -> log.info("Number: {}", num),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    @Test
    public void OnErrorMap_IfArithmeticException_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorMap(
                        ArithmeticException.class,
                        error -> new RuntimeException("SomeMathException")
                )
                .subscribe(num -> log.info("Number: {}", num));
    }

    @Test
    public void OnErrorMap_IfPredicatePasses_Mono() {
        Mono.just(2)
                .map(i -> i / 0) // will produce ArithmeticException
                .onErrorMap(
                        error -> error instanceof ArithmeticException,
                        error -> new RuntimeException("SomeMathException")
                )
                .subscribe(num -> log.info("Number: {}", num));
    }


}
