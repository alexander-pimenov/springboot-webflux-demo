package com.javatechie.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * В реактивном программировании мы имеем дело с издателями и подписчиками.
 * Издатель публикует данные подписчику. Обычно это источник данных. Это может быть база данных,
 * удаленный сервис или что-то еще, содержащее какие-либо данные.
 * <p>
 * В Project Reactor у нас есть два издателя: Mono и Flux.
 * <p>
 * Подписчик — это приложение, которое получает данные от издателя.
 * <p>
 * Взаимодействие между этими двумя типами начинается, когда Подписчик подписывается на Издателя.
 * Затем издатель отправляет подписку и начинает одновременно отправлять данные, как только они
 * станут доступны, в виде потока событий.
 * <p>
 * Когда издатель завершает отправку данных, он отправляет сигнал onComplete, чтобы уведомить подписчика
 * о том, что данных больше не будет.
 */
@Slf4j
public class BackpressureTest {

    /**
     * Из журналов видно, что после того, как мы подписались на Flux, был вызван метод request(), и Flux
     * начал отправлять данные с помощью сигналов onNext.
     * Поток будет излучать все элементы, и в таких случаях он может легко сокрушить Потребителя.
     * <p>
     * Чтобы избежать этой проблемы, Потребитель должен иметь возможность сообщить Производителю,
     * какой объем данных следует отправить.  Это называется противодавлением - Backpressure.
     * <p>
     * Мы используем метод request() интерфейса подписки, чтобы сообщить издателю, какой объем данных нам нужен.
     */
    @Test
    public void subscribe_Flux_1() {

        Flux<String> cities = Flux.fromIterable(
                new ArrayList<>(Arrays.asList("Нью-Йорк", "Лондон", "Париж", "Торонто", "Рим")));

        cities.log().subscribe();
    }

    /**
     * Мы используем метод request() интерфейса подписки, чтобы сообщить издателю, какой объем данных нам нужен.
     * Для этого мы будем использовать версию метода subscribe(), которая принимает BaseSubscriber.
     * Этот класс позволяет пользователю выполнять операции request() и cancel() непосредственно над ним.
     * Используя его, мы можем переопределить поведение по умолчанию.
     * <p>
     * Внутри мы вызываем метод request() объекта Subscription и приказываем издателю отправлять только 10 значений.
     */
    @Test
    public void subscribe_Flux_2() {

        Flux<Integer> publisher = Flux.range(1, 100)
                .log();

        publisher.subscribe(new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(10); // request only 5 elements
            }
        });
    }

    /**
     * Мы используем метод request() интерфейса подписки, чтобы сообщить издателю, какой объем данных нам нужен.
     * Для этого мы будем использовать версию метода subscribe(), которая принимает BaseSubscriber.
     * Этот класс позволяет пользователю выполнять операции request() и cancel() непосредственно над ним.
     * Используя его, мы можем переопределить поведение по умолчанию.
     * <p>
     * Внутри мы вызываем метод request() объекта Subscription и приказываем издателю отправлять только 10 значений.
     * <p>
     * Всегда полезно отменить подписку после получения последнего запрошенного элемента.
     * Для этого мы переопределим метод onNext(), вызываемый для каждого выпущенного элемента.
     * Расширим предыдущий пример.
     * <p>
     * Запрос cancel() был выполнен после последнего элемента.
     */
    @Test
    public void subscribe_Flux_3() {

        Flux<Integer> publisher = Flux.range(1, 100)
                .log();

        publisher.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(5);
            }

            @Override
            protected void hookOnNext(Integer value) {
                if (value == 5) { // we know that the last element is 5
                    cancel();
                }
            }
        });
    }


}
