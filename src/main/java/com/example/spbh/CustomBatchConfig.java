package com.example.spbh;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;

@RequiredArgsConstructor
public class CustomBatchConfig extends DefaultBatchConfigurer {

//    private final DataSource dataSource;
//    private final PlatformTransactionManager transactionManager;
//
//    @Override
//    protected JobRepository createJobRepository() throws Exception {
//        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
//
//        factoryBean.setDataSource(dataSource);
//        factoryBean.setTransactionManager(transactionManager);
//        factoryBean.afterPropertiesSet();
//        JobRepository jobRepository = factoryBean.getObject();
//        return jobRepository;
//    }
//
//    @Override
//    protected JobExplorer createJobExplorer() throws Exception {
//        JobExplorerFactoryBean factoryBean = new JobExplorerFactoryBean();
//        factoryBean.setDataSource(dataSource);
//        factoryBean.afterPropertiesSet();
//        JobExplorer jobExplorer = factoryBean.getObject();
//        return jobExplorer;
//
//    }
//
//    @Override
//    protected JobLauncher createJobLauncher() throws Exception {
//        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
//        return super.createJobLauncher();
//    }
}
