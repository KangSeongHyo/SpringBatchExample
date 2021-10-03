package com.example.spbh;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNamedQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

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

    @Bean
    public Step step(JpaPagingItemReader<Input> jpaPagingItemReader){
        return stepBuilderFactory.get("Chunk")
                .<Input,Output>chunk(3)
                .reader(jpaPagingItemReader)
                .processor(new ItemProcessor<Input, Output>() {
                    @Override
                    public Output process(Input item) throws Exception {
                        Thread.sleep(1000*1);
                        System.out.println("### Process "+(++process)+"번 "+item);
                        return new Output();
                    }
                }).writer(new ItemWriter<Output>() {
                    @Override
                    public void write(List<? extends Output> items) throws Exception {
                        Thread.sleep(1000*1);
                        System.out.println("### Writer "+(++writer)+"번");
                        System.out.println();
                    }
                })
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