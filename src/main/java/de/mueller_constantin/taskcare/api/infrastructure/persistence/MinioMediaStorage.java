package de.mueller_constantin.taskcare.api.infrastructure.persistence;

import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
public class MinioMediaStorage implements MediaStorage {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Autowired
    public MinioMediaStorage(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Override
    @SneakyThrows
    public void save(String path, String contentType, byte[] data) {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(path)
                .contentType(contentType)
                .stream(new ByteArrayInputStream(data), data.length, -1)
                .build());
    }

    @Override
    @SneakyThrows
    public byte[] load(String path) {
        GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(path)
                .build());

        return response.readAllBytes();
    }

    @Override
    @SneakyThrows
    public void delete(String path) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(path)
                .build());
    }

    @Override
    @SneakyThrows
    public boolean exists(String path) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(path)
                    .build());

            return true;
        } catch (ErrorResponseException e) {
            return false;
        }
    }

    @Override
    @SneakyThrows
    public long size(String path) {
        StatObjectResponse response = minioClient.statObject(StatObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(path)
                .build());

        return response.size();
    }

    @Override
    @SneakyThrows
    public String contentType(String path) {
        StatObjectResponse response = minioClient.statObject(StatObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(path)
                .build());

        return response.contentType();
    }
}
