package com.eluna.microservices.customer_microservice.config;

import com.eluna.microservices.customer_microservice.customer.Customer;
import com.eluna.microservices.customer_microservice.customer.CustomerCsv;
import com.eluna.microservices.customer_microservice.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.JobParameter;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.RecordFieldSetMapper;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;


@RequiredArgsConstructor
@Configuration
public class BatchConfig {
    private final CustomerRepository repository;

    @Bean
    public FlatFileItemReader<CustomerCsv> reader() {
        return new FlatFileItemReaderBuilder<CustomerCsv>()
                .name("customerReader")
                .resource(new ClassPathResource("data/usuarios.csv"))
                .linesToSkip(1)
                .delimited().names("firstName","lastName","email","phone","address","city")
                .fieldSetMapper(new RecordFieldSetMapper<>(CustomerCsv.class))
                .build();
    }

    @Bean
    public ItemProcessor<CustomerCsv,Customer> processor() {
        return csv -> new Customer(null, csv.firstName(), csv.lastName(),
                csv.email(), csv.phone(), csv.address(), csv.city());
    }

    @Bean
    public JdbcBatchItemWriter<Customer> writer() {
        return new JdbcBatchItemWriter<Customer>();
    }

    @Bean
    public Step pasoImportar(JobRepository jobRepository,
                             PlatformTransactionManager ptx,
                             ItemReader<CustomerCsv> reader,
                             ItemProcessor<CustomerCsv,Customer> processor,
                             ItemWriter<Customer> writer) {
        return new StepBuilder("pasoImportar", jobRepository)
                .<CustomerCsv,Customer> chunk(5)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importCustomersJob(JobRepository jobRepository, Step pasoImportar) {
        return new JobBuilder("importCustomersJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(pasoImportar)
                .build();
    }
}
