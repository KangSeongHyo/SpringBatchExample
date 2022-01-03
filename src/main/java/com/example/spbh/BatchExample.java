package com.example.spbh;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.orm.JpaNamedQueryProvider;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Slf4j
@EnableBatchProcessing
@RequiredArgsConstructor
@Configuration
public class BatchExample {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final InputRepository inputRepository;
//
//    @Bean
//    public Step step(Tasklet tasklet){
//        return stepBuilderFactory.get("Step 이름")
//                .tasklet(tasklet)
//                .build();
//    }
//

    private static int reader = 0;
    private static int process = 0;
    private static int writer = 0;

    private final AItemProcessor aItemProcessor;
    private final BItemProcessor bItemProcessor;
    private final OtherItemProcessor otherItemProcessor;

    @Bean
    public Step step(JpaPagingItemReader<Input> jpaPagingItemReader,JpaItemWriter<Output> jpaItemWriter){

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

        return stepBuilderFactory.get("Chunk")
                .<Input,Output>chunk(10)
                .reader(jpaPagingItemReader)
                .processor(processor)
                .writer(jpaItemWriter)
                .build();
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
         /* JpaNativeQueryProvider<Input> queryProvider = new JpaNativeQueryProvider<>();
        queryProvider.setSqlQuery("SELECT * FROM INPUT");
        queryProvider.setEntityManager(entityManager);
        queryProvider.setEntityClass(Input.class);*/


        return jpaPagingItemReader;
    }

    @Bean
    public Job job(Step step){
        return jobBuilderFactory.get("Job")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }
}