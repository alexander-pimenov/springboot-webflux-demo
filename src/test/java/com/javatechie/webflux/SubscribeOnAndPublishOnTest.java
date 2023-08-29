package com.javatechie.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * subscribeOn and publishOn
 * !!! Тот же поток , который выполняет подписку, будет использоваться для выполнения всего конвейера !!!
 * <a href="https://www.appsdeveloperblog.com/subscribeon-and-publishon-operators-in-project-reactor/">source1</a>
 * <p>
 * Мы используем операторы subscribeOn и publishOn для переключения контекста выполнения (Планировщика) в реактивной цепочке.
 * <p>
 * Как уже упоминали, поведение по умолчанию заключается в том, что для выполнения всего конвейера будет использоваться
 * тот же поток, который выполняет подписку.
 * <p>
 * При запуске конвейера, в скобках [ ] указано имя потока, в данном случае main.
 * Вы можете видеть, что основной поток использовался на протяжении всего выполнения конвейера.
 * Иногда нам хотелось бы сказать Reactor не использовать тот же поток, который запустил подписку, на
 * протяжении всего конвейера. Мы можем сделать это с помощью методов subscribeOn() и publishOn() .
 * <p>
 * subscribeOn() - Метод subscribeOn() применяется к процессу подписки. Мы можем разместить его в любом месте
 * реактивной цепи. Он принимает планировщик и выбирает поток из предоставленного пула потоков.
 * <p>
 * У нас есть различные варианты планировщика, которые мы можем использовать:
 * <p>
 * Schedulers.parallel() — имеет фиксированный пул воркеров. Количество потоков эквивалентно количеству ядер процессора.
 * <p>
 * Schedulers.boundElastic() — имеет ограниченный пул эластичных потоков рабочих процессов. Количество
 * потоков может увеличиваться в зависимости от необходимости. Количество потоков может быть намного больше, чем количество
 * ядер процессора.
 * Используется в основном для блокировки вызовов ввода-вывода.
 * <p>
 * Schedulers.single() — повторно использует один и тот же поток для всех вызывающих абонентов.
 */
@Slf4j
public class SubscribeOnAndPublishOnTest {

    /**
     * обычная подписка
     * Во время запуска теста в скобках [ ] указано имя потока, в данном случае main.
     * Вы можете видеть, что основной поток использовался на протяжении всего выполнения конвейера.
     */
    @Test
    public void regularSubscribe_Flux() {
        Flux<String> cities = Flux.just("Нью-Йорк", "Стокгольм", "Лондон", "Париж", "Амстердам")
                .map(String::toUpperCase)
                .filter(cityName -> cityName.length() <= 8)
                .map(cityName -> cityName.concat(" Город"))
                .log();

        cities.subscribe(data -> log.info("Data: {}", data),
                (e) -> log.info("Error: {}", e.getMessage()));
    }

    /**
     * В этом примере мы используем ограниченный эластичный пул потоков Schedulers.boundElastic().
     * Во время запущенного теста видим два потока [main] и [boundedElastic-1]
     * Вы можете видеть, что основной поток начал подписку, но был переключен на поток boundedElastic-1.
     * Мы предоставили планировщик Schedulers.boundElastic(), и один из потоков из его пула потоков был
     * выбран для замены основного потока.
     */
    @Test
    public void boundedElasticSubscribe_Flux() {
        Flux<String> cities = Flux.just("Нью-Йорк", "Стокгольм", "Лондон", "Париж", "Амстердам")
                .subscribeOn(Schedulers.boundedElastic())
                .map(String::toUpperCase)
                .filter(cityName -> cityName.length() <= 8)
                .map(cityName -> cityName.concat(" Город"))
                .log();

        cities.subscribe(data -> log.info("Data: {}", data),
                (e) -> log.info("Error: {}", e.getMessage()));
    }

    /**
     * Метод publishOn() похож на метод subscribeOn(), но есть одно главное отличие.
     * <p>
     * stringToUpperCase: main
     * concat: boundedElastic-1
     * <p>
     * Все, что было до оператора publishOn(), выполнялось основным потоком main, а все,
     * что было после него, — boundedElastic-1.
     * Это связано с тем, что publicOn действует как любой
     * другой оператор. Он принимает сигналы из восходящего потока и воспроизводит их в нисходящем
     * направлении, одновременно выполняя обратный вызов для работника из связанного планировщика.
     */
    @Test
    public void publishOnSubscribe_Flux() {
        Flux.just("New York", "Stockholm", "London", "Paris", "Amsterdam")
                .map(SubscribeOnAndPublishOnTest::stringToUpperCase)
                .publishOn(Schedulers.boundedElastic())
                .map(SubscribeOnAndPublishOnTest::concat)
                .log()
                .subscribe(data -> log.info("Data: {}", data),
                        (e) -> log.info("Error: {}", e.getMessage()));
    }

    private static String stringToUpperCase(String name) {
        System.out.println("stringToUpperCase: " + Thread.currentThread().getName());
        return name.toUpperCase();
    }

    private static String concat(String name) {
        System.out.println("concat: " + Thread.currentThread().getName());
        return name.concat(" City");
    }
}
