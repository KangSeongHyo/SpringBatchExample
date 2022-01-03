package com.example.spbh;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class OtherItemProcessor implements ItemProcessor<Input,Output> {

    @Override
    public Output process(Input item) throws Exception {
        System.out.println("## Other - "+item);
        return null;
    }
}
