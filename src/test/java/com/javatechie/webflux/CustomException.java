package com.javatechie.webflux;

public class CustomException extends Throwable {
    public CustomException(String message, Throwable ex) {
        super(message, ex);
    }
}
