package com.eluna.microservices.customer_microservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

@Service
@RequiredArgsConstructor
public class CustomerImportService {

    private final JobOperator jobOperator;
    private final Job importCustomerJob;

    public JobExecution importFromFile(MultipartFile file) throws Exception {
        Path tempFile = null;

        try {
            tempFile = createSecureTempFile();
            file.transferTo(tempFile);

            JobParameters params = new JobParametersBuilder()
                    .addString("filePath", tempFile.toAbsolutePath().toString())
                    .addLong("startedAt", System.currentTimeMillis())
                    .toJobParameters();

            return jobOperator.start(importCustomerJob, params);
        } finally {
            if (tempFile != null) {
                Files.deleteIfExists(tempFile);
            }
        }
    }

    public Path createSecureTempFile() throws Exception {
        boolean posix = Files.getFileStore(Path.of(System.getProperty("java.io.tmpdir")))
                .supportsFileAttributeView("posix");

        if (posix) {
            return Files.createTempFile("customers-import-", ".csv",
                    PosixFilePermissions.asFileAttribute(
                            PosixFilePermissions.fromString("rw-------")
                    ));
        }

        return Files.createTempFile("customers-import-", ".csv");
    }
}
