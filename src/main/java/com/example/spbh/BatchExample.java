package com.example.spbh;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.orm.JpaNamedQueryProvider;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;

@Slf4j
@EnableBatchProcessing
@RequiredArgsConstructor
@Configuration
public class BatchExample {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final AItemProcessor aItemProcessor;
    private final BItemProcessor bItemProcessor;
    private final OtherItemProcessor otherItemProcessor;

    @Bean
    public Job job(Step step){
        return jobBuilderFactory.get("Job")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step step(ItemReader<Input> jpaPagingItemReader, ItemWriter<Output> jpaItemWriter, ItemProcessor<Input,Output> itemProcessor){

        return stepBuilderFactory.get("Chunk")
                .<Input,Output>chunk(10)
                .reader(jpaPagingItemReader)
                .processor(itemProcessor)
                .writer(jpaItemWriter)
                .faultTolerant()
                    .skipPolicy(skipPolicy())
                .taskExecutor(new SimpleAsyncTaskExecutor())
                    .throttleLimit(3)
                .build();
    }

    public SkipPolicy skipPolicy(){
        CustomSkipPolicy skipPolicy = new CustomSkipPolicy();
        skipPolicy.setExceptionClassMap(IllegalArgumentException.class,3);
        skipPolicy.setExceptionClassMap(IllegalAccessException.class,3);
        return skipPolicy;
    }


    @Bean
    public ItemProcessor<Input,Output> itemProcessor(){
        ClassifierCompositeItemProcessor<Input, Output> processor = new ClassifierCompositeItemProcessor<>();

        processor.setClassifier(input -> {
            switch (input.getType()){
                case "A" :
                    return aItemProcessor;
                case "B" :
                    return bItemProcessor;
                default:
                    return otherItemProcessor;
            }
        });

        return processor;
    }

    @Bean
    public JpaItemWriter<Output> jpaItemWriter(){
        return new JpaItemWriterBuilder<Output>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();
    }

    @Bean
    public JpaPagingItemReader<Input> jpaPagingItemReader(){

        JpaPagingItemReader<Input> jpaPagingItemReader = new JpaPagingItemReader<>();
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);

        JpaNamedQueryProvider<Input> queryProvider = new JpaNamedQueryProvider<>();
        queryProvider.setNamedQuery("input");

        jpaPagingItemReader.setQueryProvider(queryProvider);
        jpaPagingItemReader.setPageSize(5);
        jpaPagingItemReader.setTransacted(false);
        jpaPagingItemReader.setSaveState(false);


        return jpaPagingItemReader;
    }

}