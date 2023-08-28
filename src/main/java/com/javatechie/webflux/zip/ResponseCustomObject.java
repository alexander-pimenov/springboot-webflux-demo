package com.javatechie.webflux.zip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCustomObject {
    private CustomObject name;
    private String t2;
    private String t3;
}
