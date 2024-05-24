package io.geekya215.nyaoj.storage;

import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MinioService {
    private static final Logger log = LoggerFactory.getLogger(MinioService.class);
    private final MinioConfiguration minioConfiguration;
    private final MinioClient minioClient;

    public MinioService(MinioConfiguration minioConfiguration, MinioClient minioClient) {
        this.minioConfiguration = minioConfiguration;
        this.minioClient = minioClient;
    }

    public @NonNull Result<Void, ErrorResponse<String>> putFile(
            @NonNull final UUID uuid,
            @NonNull final MultipartFile file,
            @NonNull final String type
    ) {
        try (final InputStream inputStream = file.getInputStream()) {
            final ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfiguration.getBucket())
                    .object(uuid + type)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
            return Result.success();
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }
}
