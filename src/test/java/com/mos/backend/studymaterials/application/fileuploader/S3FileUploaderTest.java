package com.mos.backend.studymaterials.application.fileuploader;

import com.mos.backend.common.event.Event;
import com.mos.backend.common.event.EventType;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.studymaterials.application.UploadType;
import com.mos.backend.studymaterials.application.event.FileUploadFailedEventPayloadWithNotification;
import com.mos.backend.studymaterials.application.event.FileUploadedEventPayloadWithNotification;
import com.mos.backend.studymaterials.entity.UploaderErrorCode;
import com.mos.backend.studymaterials.infrastructure.fileuploader.aws.S3FileUploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;
import software.amazon.awssdk.transfer.s3.model.Upload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class S3FileUploaderTest {

    @InjectMocks
    private S3FileUploader s3FileUploader;

    @Mock private S3Client s3Client;
    @Mock private S3TransferManager s3TransferManager;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private S3AsyncClient s3AsyncClient;

    @Captor private ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor;
    @Captor private ArgumentCaptor<RequestBody> requestBodyCaptor;
    @Captor private ArgumentCaptor<UploadRequest> uploadRequestCaptor;
    @Captor private ArgumentCaptor<DeleteObjectRequest> deleteObjectRequestCaptor;
    @Captor private ArgumentCaptor<CopyObjectRequest> copyObjectRequestCaptor;
    @Captor private ArgumentCaptor<Event> eventCaptor;

    // --- 테스트용 상수 ---
    private final String BUCKET_NAME = "test-bucket";
    private final String BASE_S3_URL = "https://"+ BUCKET_NAME +".s3.amazonaws.com/";


    @TempDir
    Path sharedTempDir;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(s3FileUploader, "bucketName", BUCKET_NAME);
    }

    @Test
    @DisplayName("generateFileObjectKey 성공 테스트")
    void generateFileObjectKey_success() {
        // Given
        Long folderName = 123L;
        String fileName = "test-file.txt";

        // When
        String objectKey = s3FileUploader.generateFileObjectKey(UploadType.STUDY, folderName, fileName);

        // Then
        assertThat(objectKey).isEqualTo("study/123/test-file.txt");
    }

    @Test
    @DisplayName("generateFileUrl 성공 테스트")
    void generateFileUrl_success() {
        // Given
        Long folderName = 456L;
        String fileName = "image.png";

        // When
        String fileUrl = s3FileUploader.generateFileUrl(UploadType.STUDY, folderName, fileName);

        // Then
        assertThat(fileUrl).isEqualTo(BASE_S3_URL + "study/456/image.png");
    }

    @Test
    @DisplayName("uriToFileObjectKey 성공 테스트")
    void uriToFileObjectKey_success() {
        // Given
        String uriString = BASE_S3_URL + "materials/123/document.pdf";

        // When
        String objectKey = s3FileUploader.uriToFileObjectKey(uriString);

        // Then
        assertThat(objectKey).isEqualTo("materials/123/document.pdf");
    }

    @Test
    @DisplayName("uriToFileObjectKey: 잘못된 uri 에러 발생 테스트")
    void uriToFileObjectKey_invalidUri() {
        // Given
        String invalidUriString = "invalid uri";

        // When & Then
        assertThatThrownBy(() -> s3FileUploader.uriToFileObjectKey(invalidUriString))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("uploadFileSync: 테스트 성공")
    void uploadFileSync_success() throws IOException {
        // Given
        String uuidFileName = UUID.randomUUID() + ".txt";
        Long folderName = 789L;
        String content = "File content";

        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "original.txt",
                "text/plain",
                content.getBytes(StandardCharsets.UTF_8)
        );
        String expectedObjectKey = "study/" + folderName + "/" + uuidFileName;
        String expectedUrl = BASE_S3_URL + expectedObjectKey;

        given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).willReturn(PutObjectResponse.builder().build());

        // When
        String actualUrl = s3FileUploader.uploadFileSync(uuidFileName, folderName, UploadType.STUDY, mockFile);

        // Then
        assertThat(actualUrl).isEqualTo(expectedUrl);

        verify(s3Client).putObject(putObjectRequestCaptor.capture(), requestBodyCaptor.capture());

        PutObjectRequest capturedRequest = putObjectRequestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedRequest.key()).isEqualTo(expectedObjectKey);
        assertThat(capturedRequest.contentType()).isEqualTo("text/plain");
        assertThat(capturedRequest.contentLength()).isEqualTo(content.length());

    }

    @Test
    @DisplayName("uploadFileSync: S3 업로드 에러 시 MosException 발생")
    void uploadFileSync_s3ThrowsException() throws IOException {
        // Given
        String uuidFileName = UUID.randomUUID() + ".jpg";
        Long folderName = 101L;
        MultipartFile mockFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[]{});

        given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .willThrow(SdkClientException.builder().message("S3 Error").build());

        // When & Then
        assertThatThrownBy(() -> s3FileUploader.uploadFileSync(uuidFileName, folderName, UploadType.STUDY, mockFile))
                .isInstanceOf(MosException.class)
                .hasFieldOrPropertyWithValue("errorCode", UploaderErrorCode.FILE_UPLOAD_EXCEPTION);
    }

    @Test
    @DisplayName("uploadFileSync: inputStream 에러 시 MosException 발생")
    void uploadFileSync_getInputStreamThrowsException() throws IOException {
        // Given
        String uuidFileName = UUID.randomUUID() + ".png";
        Long folderName = 112L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getSize()).thenReturn(100L);
        when(mockFile.getInputStream()).thenThrow(new IOException("Cannot read stream"));

        // When & Then
        assertThatThrownBy(() -> s3FileUploader.uploadFileSync(uuidFileName, folderName, UploadType.STUDY, mockFile))
                .isInstanceOf(MosException.class)
                .hasFieldOrPropertyWithValue("errorCode", UploaderErrorCode.FILE_UPLOAD_EXCEPTION);

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }


    @Test
    @DisplayName("uploadFileAsync: 비동기 업로드 성공 테스트")
    void uploadFileAsync_success() throws Exception {
        // Given
        String fileName = "async-upload.jpeg";
        Long folderName = 201L;
        String content = "zip content";
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                fileName,
                "application/image",
                content.getBytes(StandardCharsets.UTF_8)
        );
        String expectedObjectKey = "temp/" + folderName + "/" + fileName;

        Upload mockUpload = mock(Upload.class);
        CompletedUpload mockCompletedUpload = mock(CompletedUpload.class);
        CompletableFuture<CompletedUpload> successFuture = CompletableFuture.completedFuture(mockCompletedUpload);
        given(mockUpload.completionFuture()).willReturn(successFuture);
        given(s3TransferManager.upload(any(UploadRequest.class))).willReturn(mockUpload);

        // When
        assertDoesNotThrow(() -> s3FileUploader.uploadFileAsync(1L, fileName, folderName, UploadType.TEMP, mockFile));

        successFuture.join();

        verify(s3TransferManager).upload(uploadRequestCaptor.capture());
        UploadRequest capturedUploadRequest = uploadRequestCaptor.getValue();
        PutObjectRequest capturedPutRequest = capturedUploadRequest.putObjectRequest();
        assertThat(capturedPutRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedPutRequest.key()).isEqualTo(expectedObjectKey);
        assertThat(capturedPutRequest.contentType()).isEqualTo("application/image");
        assertThat(capturedPutRequest.contentLength()).isGreaterThanOrEqualTo(content.length());
        assertThat(capturedUploadRequest.requestBody()).isNotNull();

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Event event = eventCaptor.getValue();
        assertThat(event.getPayload()).isInstanceOf(FileUploadedEventPayloadWithNotification.class);

    }


    @Test
    @DisplayName("uploadFileAsync: S3 업로드 실패 시 실패 에러 발생 및 실패 이벤트 발생")
    void uploadFileAsync_s3Failure() throws Exception {
        // Given
        String fileName = "failed-async.data";
        Long folderName = 202L;
        String content = "data content";
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                fileName,
                "application/octet-stream",
                content.getBytes(StandardCharsets.UTF_8)
        );
        String expectedObjectKey = "temp/" + folderName + "/" + fileName;

        Upload mockUpload = mock(Upload.class);
        CompletableFuture<CompletedUpload> failureFuture = new CompletableFuture<>();
        failureFuture.completeExceptionally(new RuntimeException("S3 Upload Failed"));
        given(mockUpload.completionFuture()).willReturn(failureFuture);
        given(s3TransferManager.upload(any(UploadRequest.class))).willReturn(mockUpload);

        // When
        assertDoesNotThrow(() -> s3FileUploader.uploadFileAsync(1L, fileName, folderName, UploadType.TEMP, mockFile));

        // Then
        assertThatThrownBy(failureFuture::join)
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageContaining("S3 Upload Failed");

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Event capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getEventType()).isEqualTo(EventType.FILE_UPLOAD_FAILED);
        assertThat(capturedEvent.getPayload()).isInstanceOf(FileUploadFailedEventPayloadWithNotification.class);
        FileUploadFailedEventPayloadWithNotification payload = (FileUploadFailedEventPayloadWithNotification) capturedEvent.getPayload();
        assertThat(payload.getFilePath()).isEqualTo(expectedObjectKey);

    }

    @Test
    @DisplayName("uploadFileAsync: 임시 파일 생성 실패 시 실패 에러 발생 및 실패 이벤트 발생")
    void uploadFileAsync_tempFileCreationFails() throws IOException {
        // Given
        String fileName = "no-temp-file.log";
        Long folderName = 203L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.getInputStream()).thenThrow(new IOException("Disk full"));
        String expectedObjectKey = "temp/" + folderName + "/" + fileName;

        // When & Then:
        assertThatThrownBy(() -> s3FileUploader.uploadFileAsync(1L, fileName, folderName, UploadType.TEMP, mockFile))
                .isInstanceOf(MosException.class)
                .hasFieldOrPropertyWithValue("errorCode", UploaderErrorCode.FILE_UPLOAD_EXCEPTION);

        verify(s3TransferManager, never()).upload(any(UploadRequest.class));

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Event capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getEventType()).isEqualTo(EventType.FILE_UPLOAD_FAILED);
        FileUploadFailedEventPayloadWithNotification payload = (FileUploadFailedEventPayloadWithNotification) capturedEvent.getPayload();
        assertThat(payload.getFilePath()).isEqualTo(expectedObjectKey);

    }

    @Test
    @DisplayName("deleteFile: 성공 테스트")
    void deleteFile_success() {
        // Given
        String objectKey = "materials/301/to-delete.txt";
        String uriString = BASE_S3_URL + objectKey;

        CompletableFuture<DeleteObjectResponse> successFuture = CompletableFuture.completedFuture(DeleteObjectResponse.builder().build());
        given(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class))).willReturn(successFuture);

        // When
        assertDoesNotThrow(() -> s3FileUploader.deleteFile(uriString));

        // Then
        verify(s3AsyncClient).deleteObject(deleteObjectRequestCaptor.capture());
        DeleteObjectRequest capturedRequest = deleteObjectRequestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedRequest.key()).isEqualTo(objectKey);
    }


    @Test
    @DisplayName("deleteFile: S3 에러 시 mosException 발생")
    void deleteFile_s3ThrowsSyncException() {
        // Given
        String objectKey = "materials/302/delete-fail-sync.jpg";
        String uriString = BASE_S3_URL + objectKey;

        given(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class)))
                .willThrow(SdkClientException.builder().message("Cannot connect").build());

        // When & Then
        assertThatThrownBy(() -> s3FileUploader.deleteFile(uriString))
                .isInstanceOf(MosException.class)
                .hasFieldOrPropertyWithValue("errorCode", UploaderErrorCode.FILE_DELETE_EXCEPTION);
    }

    @Test
    @DisplayName("moveFile: 성공 테스트")
    void moveFile_success() {
        // Given
        String sourceKey = "temp/1/move-me.pdf";
        String destinationKey = "study/2/moved.pdf";

        given(s3Client.copyObject(any(CopyObjectRequest.class))).willReturn(CopyObjectResponse.builder().build());

        CompletableFuture<DeleteObjectResponse> deleteSuccessFuture = CompletableFuture.completedFuture(DeleteObjectResponse.builder().build());
        given(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class))).willReturn(deleteSuccessFuture);

        // When: 예외 미발생 검증
        assertDoesNotThrow(() -> s3FileUploader.moveFile(sourceKey, destinationKey));

        // Then
        verify(s3Client).copyObject(copyObjectRequestCaptor.capture());
        CopyObjectRequest capturedCopyRequest = copyObjectRequestCaptor.getValue();
        assertThat(capturedCopyRequest.sourceBucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedCopyRequest.sourceKey()).isEqualTo(sourceKey);
        assertThat(capturedCopyRequest.destinationBucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedCopyRequest.destinationKey()).isEqualTo(destinationKey);

        verify(s3AsyncClient).deleteObject(deleteObjectRequestCaptor.capture());
        DeleteObjectRequest capturedDeleteRequest = deleteObjectRequestCaptor.getValue();
        assertThat(capturedDeleteRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(capturedDeleteRequest.key()).isEqualTo(sourceKey);
    }

    @Test
    @DisplayName("moveFile: 복사 실패 시 기존 파일을 삭제하지 않음")
    void moveFile_copyThrowsException() {
        // Given
        String sourceKey = "materials/temp/copy-fail.doc";
        String destinationKey = "materials/final/copy-fail-target.doc";

        given(s3Client.copyObject(any(CopyObjectRequest.class)))
                .willThrow(S3Exception.builder().message("Copy failed").build());

        assertThatThrownBy(() -> s3FileUploader.moveFile(sourceKey, destinationKey))
                .isInstanceOf(S3Exception.class);

        verify(s3AsyncClient, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("moveFile: 삭제 실패 시 mosError 발생")
    void moveFile_deleteThrowsException() {
        // Given
        String sourceKey = "temp/1/delete3e-fail.xls";
        String destinationKey = "study/3/delete-fail-target.xls";

        given(s3Client.copyObject(any(CopyObjectRequest.class))).willReturn(CopyObjectResponse.builder().build());

        given(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class)))
                .willThrow(SdkClientException.builder().message("Cannot connect").build());

        // When & Then:
        assertThatThrownBy(() -> s3FileUploader.moveFile(sourceKey, destinationKey))
                .isInstanceOf(MosException.class)
                .hasFieldOrPropertyWithValue("errorCode", UploaderErrorCode.FILE_DELETE_EXCEPTION);

        verify(s3Client).copyObject(any(CopyObjectRequest.class));
        verify(s3AsyncClient).deleteObject(any(DeleteObjectRequest.class));
    }
}