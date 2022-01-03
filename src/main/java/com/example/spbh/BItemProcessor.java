package com.example.spbh;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BItemProcessor implements ItemProcessor<Input,Output> {
    @Override
    public Output process(Input item) throws Exception {
        System.out.println("## B 처리 - "+item);
        return null;
    }
}
