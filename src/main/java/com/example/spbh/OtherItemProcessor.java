package com.example.spbh;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class OtherItemProcessor implements ItemProcessor<Input,Output> {

    @Override
    public Output process(Input item) throws Exception {
        System.out.println(Thread.currentThread()+" ## Other - "+item +" AccessException");

        throw new IllegalAccessException("임의로 예외발생 시키기");
        // return null;
    }
}
