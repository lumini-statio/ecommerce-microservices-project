package com.eluna.microservices.customer_microservice.config;

import com.eluna.microservices.customer_microservice.customer.Customer;
import com.eluna.microservices.customer_microservice.customer.CustomerCsv;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.RecordFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.UUID;


@Configuration
public class BatchConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<CustomerCsv> reader(@Value("#{jobParameters['filePath']}") String file) {
        return new FlatFileItemReaderBuilder<CustomerCsv>()
                .name("customerReader")
                .resource(new FileSystemResource(file))
                .linesToSkip(1)
                .delimited().names("firstName","lastName","email","phone","address","city")
                .fieldSetMapper(new RecordFieldSetMapper<>(CustomerCsv.class))
                .build();
    }

    @Bean
    public ItemProcessor<CustomerCsv,Customer> processor() {
        return csv -> new Customer(UUID.randomUUID(), csv.firstName(), csv.lastName(),
                csv.email(), csv.phone(), csv.address(), csv.city());
    }

    @Bean
    public JdbcBatchItemWriter<Customer> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("INSERT INTO customers (id, first_name, last_name, email, phone, address, city) " +
                     "VALUES (:id, :firstName, :lastName, :email, :phone, :address, :city)")
                .beanMapped()
                .build();
    }

    @Bean
    public Step importStep(JobRepository jobRepository,
                             ItemReader<CustomerCsv> reader,
                             ItemProcessor<CustomerCsv,Customer> processor,
                             ItemWriter<Customer> writer) {
        return new StepBuilder("importStep", jobRepository)
                .<CustomerCsv,Customer> chunk(50)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importCustomersJob(JobRepository jobRepository, Step importStep) {
        return new JobBuilder("importCustomersJob", jobRepository)
                .start(importStep)
                .build();
    }
}
