package com.example.spbh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomSkipPolicy implements SkipPolicy {

    private final Map<Class<? extends Throwable>,Integer> exceptionClassMap = new HashMap<>();
    private final Map<Class<? extends Throwable>,Integer> exceptionCountMap = new HashMap<>();

    public void setExceptionClassMap(Class<? extends Throwable> clazz, int limit){
        exceptionClassMap.put(clazz,limit);
    }

    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {

        if(exceptionClassMap.containsKey(t.getClass())){

            int limit = exceptionClassMap.get(t.getClass());
            int count = exceptionCountMap.getOrDefault(t.getClass(),0);

            if(count+1 > limit){
                throw new SkipLimitExceededException(limit,t);
            }
            exceptionCountMap.put(t.getClass(), count+1);

            return true;

        }
        return false;
    }
}
