package com.mos.backend.studymaterials.infrastructure.fileuploader.aws;

import com.mos.backend.common.event.Event;
import com.mos.backend.common.event.EventType;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.studymaterials.application.UploadType;
import com.mos.backend.studymaterials.application.event.FileUploadFailedEventPayloadWithNotification;
import com.mos.backend.studymaterials.application.event.FileUploadedEventPayloadWithNotification;
import com.mos.backend.studymaterials.entity.UploaderErrorCode;
import com.mos.backend.studymaterials.infrastructure.fileuploader.FileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3FileUploader extends FileUploader {

    private final S3Client s3Client;
    private final S3TransferManager s3TransferManager;
    private final ApplicationEventPublisher eventPublisher;
    private final S3AsyncClient s3AsyncClient;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private static final String BASE_URL = "https://%s.s3.amazonaws.com/%s";

    @Override
    public String generateFileUrl(UploadType type, Long folderName, String fileName) {
        return generateFileUrl(generateFileObjectKey(type, folderName, fileName));
    }

    @Override
    public String uploadFileSync(String UUIDFileName, Long folderName, UploadType type, MultipartFile file) {
        String filePath = generateFileObjectKey(type, folderName, UUIDFileName);

        PutObjectRequest putObjectRequest = prepareRequest(file, filePath);

        try {
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (Exception e) {
            throw new MosException(UploaderErrorCode.FILE_UPLOAD_EXCEPTION);
        }

        return generateFileUrl(filePath);
    }

    @Override
    public void uploadFileAsync(Long userId, String fileName, Long folderName, UploadType type, MultipartFile file) {
        String filePath = generateFileObjectKey(type, folderName, fileName);

        Path tempFilePath = null;

        try {
            tempFilePath = createTempFile(file);

            final Path finalTempFilePath = tempFilePath;
            final Long fileSize = Files.size(finalTempFilePath);

            AsyncRequestBody asyncRequestBody = AsyncRequestBody.fromFile(finalTempFilePath);
            PutObjectRequest putObjectRequest = prepareRequest(file, filePath, fileSize);

            UploadRequest uploadRequest = prepareUploadRequest(putObjectRequest, asyncRequestBody);

            s3TransferManager.upload(uploadRequest)
                    .completionFuture()
                    .whenComplete(((completedUpload, throwable) -> {
                        if (throwable != null) {
                            eventPublisher.publishEvent(Event.create(EventType.FILE_UPLOAD_FAILED, new FileUploadFailedEventPayloadWithNotification(userId, folderName, filePath, file.getOriginalFilename())));
                            log.error("비동기 S3 업로드 실패. Key: {}", filePath, throwable);
                        } else {
                            log.info("비동기 S3 업로드 성공 완료. Key: {}", filePath);
                            eventPublisher.publishEvent(Event.create(EventType.FILE_UPLOADED, new FileUploadedEventPayloadWithNotification(userId, folderName, file.getOriginalFilename())));
                        }
                        deleteTemporaryFile(finalTempFilePath);
                    }));

        } catch (Exception e) {
            if (tempFilePath != null) {
                deleteTemporaryFile(tempFilePath);
            }
            eventPublisher.publishEvent(Event.create(EventType.FILE_UPLOAD_FAILED, new FileUploadFailedEventPayloadWithNotification(userId, folderName, filePath, file.getOriginalFilename())));
            throw new MosException(UploaderErrorCode.FILE_UPLOAD_EXCEPTION);
        }
    }

    @Override
    public void deleteFile(String uriString) {
        String objectKey = uriToFileObjectKey(uriString);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        try {
            s3AsyncClient.deleteObject(deleteObjectRequest)
                    .whenComplete((response, throwable) -> {
                        if (throwable != null) {
                            log.error("비동기 S3 파일 삭제 실패 Bucket: {}, key: {}", bucketName, objectKey, throwable);
                        }
                    });
        } catch (Exception e) {
            log.error("비동기 S3 파일 삭제 요청 오료 발생 Bucket: {}, key: {}", bucketName, objectKey, e);
            throw new MosException(UploaderErrorCode.FILE_DELETE_EXCEPTION);
        }
    }

    @Override
    public String uriToFileObjectKey(String uriString) {
        try {
            URI url = new URI(uriString);
            String path = url.getPath();
            return path.substring(1);
        } catch (URISyntaxException e) {
            log.error("잘못된 형식의 URL 입니다: {}", uriString, e);
            throw new IllegalArgumentException(e);
        }
    }


    public void moveFile(String sourceKey, String destinationKey) {
        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(sourceKey)
                .destinationBucket(bucketName)
                .destinationKey(destinationKey)
                .build();

        CopyObjectResponse copyObjectResponse = s3Client.copyObject(copyRequest);
        deleteFile(generateFileUrl(sourceKey));
    }


    private static UploadRequest prepareUploadRequest(PutObjectRequest putObjectRequest, AsyncRequestBody asyncRequestBody) {
        return UploadRequest.builder()
                .putObjectRequest(putObjectRequest)
                .requestBody(asyncRequestBody)
                .build();
    }

    @Override
    public String generateFileObjectKey(UploadType type, Long folderName, String fileName) {
        return type.getFolderPath() + "/" + folderName + "/" + fileName;
    }

    private String generateFileUrl(String fileName) {
        return String.format(BASE_URL, bucketName, fileName);
    }

    private PutObjectRequest prepareRequest(MultipartFile file, String filePath) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
    }

    private PutObjectRequest prepareRequest(MultipartFile file, String filePath, Long fileSize) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .contentType(file.getContentType())
                .contentLength(fileSize)
                .build();
    }

}
