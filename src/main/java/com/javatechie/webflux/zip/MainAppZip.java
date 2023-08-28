package com.javatechie.webflux.zip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * <a href="https://stackoverflow.com/questions/58445213/zip-three-different-mono-of-different-type">Заверните три разных моно разного типа в один ответ</>
 * <p>
 * <a href="https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#zip-reactor.core.publisher.Mono-reactor.core.publisher.Mono-java.util.function.BiFunction-">Mono.zip-reactor.core</>
 */
public class MainAppZip {
    public static final Logger LOGGER = LoggerFactory.getLogger(MainAppZip.class);

    public static void main(String[] args) {

        ServiceOne serviceOne = new ServiceOne();
        Mono<CustomObject> responseMonoOne = serviceOne.methodOne();

        ServiceTwo serviceTwo = new ServiceTwo();
        Mono<String> responseMonoTwo = serviceTwo.methodTwo();

        ServiceThree serviceThree = new ServiceThree();

        Mono<Object> result = Mono.zip(responseMonoOne, responseMonoTwo)
                .zipWhen(data -> serviceThree.methodThree(data.getT1()))
                .flatMap(response -> {
                    LOGGER.info("==start==> response={}", response.toList());
                    response.getT1().getT1();//response from mono1
                    response.getT1().getT2();//response from mono 2
                    response.getT2();//response from mono 3
                    LOGGER.info("==end==> response={}", response.toList());

                    //тут можно достать данные
                    Tuple2<CustomObject, String> t1 = response.getT1(); //это пара полученная в Mono.zip
                    CustomObject t11 = t1.getT1(); //первый элемент из пары
                    String t21 = t1.getT2(); //второй элемент из пары

                    String t2 = response.getT2();

                    ResponseCustomObject res = new ResponseCustomObject(t11, t21, t2);

                    return Mono.just(res);
                });
        Object block = result.log().block();
        System.out.println(block);
        System.out.println(block.getClass());

    }
}
