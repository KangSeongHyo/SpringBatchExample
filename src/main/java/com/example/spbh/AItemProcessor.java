package com.example.spbh;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class AItemProcessor implements ItemProcessor<Input,Output> {
    @Override
    public Output process(Input item) throws Exception {
        System.out.println(Thread.currentThread()+" ## A 처리 - "+item+" ArgumentException");
        throw new IllegalArgumentException("임의로 예외발생 시키기");
    }
}
