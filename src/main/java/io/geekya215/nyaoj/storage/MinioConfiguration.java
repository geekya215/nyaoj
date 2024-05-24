package io.geekya215.nyaoj.storage;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@ConfigurationProperties(prefix = "minio")
@Configuration
public class MinioConfiguration {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinioConfiguration that = (MinioConfiguration) o;
        return Objects.equals(endpoint, that.endpoint) && Objects.equals(accessKey, that.accessKey) && Objects.equals(secretKey, that.secretKey) && Objects.equals(bucket, that.bucket);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(endpoint);
        result = 31 * result + Objects.hashCode(accessKey);
        result = 31 * result + Objects.hashCode(secretKey);
        result = 31 * result + Objects.hashCode(bucket);
        return result;
    }

    @Override
    public String toString() {
        return "MinioConfiguration{" +
                "endpoint='" + endpoint + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", bucket='" + bucket + '\'' +
                '}';
    }
}
