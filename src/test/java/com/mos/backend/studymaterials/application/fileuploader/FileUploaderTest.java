package com.mos.backend.studymaterials.application.fileuploader;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.studymaterials.application.UploadType;
import com.mos.backend.studymaterials.entity.UploaderErrorCode;
import com.mos.backend.studymaterials.infrastructure.fileuploader.FileUploader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class FileUploaderTest {

    private static final String UUID_VALUE = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    static class TestFileUploader extends FileUploader {
        @Override
        public String generateFileUrl(UploadType type, Long folderName, String fileName) {
            return "";
        }

        @Override
        public String uploadFileSync(String fileName, Long folderName, UploadType type, MultipartFile file) {
            return "";
        }

        @Override
        public void uploadFileAsync(Long userId, String fileName, Long folderName, UploadType type, MultipartFile file) {

        }

        @Override
        public void deleteFile(String uriString) {

        }

        @Override
        public String uriToFileObjectKey(String uriString) {
            return "";
        }

        @Override
        public String generateFileObjectKey(UploadType type, Long folderName, String fileName) {
            return "";
        }

        @Override
        public void moveFile(String sourceKey, String destinationKey) {

        }


    }

    private TestFileUploader uploader;

    @Mock
    private MultipartFile mockFile;

    @TempDir
    Path sharedTempDir;

    private final List<Path> filesToDelete = new ArrayList<>();

    @BeforeEach
    void setUp() {
        uploader = new TestFileUploader();
    }

    @AfterEach
    void tearDown() {

        filesToDelete.forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("Failed to delete temporary file in tearDown: " + path);
                e.printStackTrace();
            }
        });
        filesToDelete.clear();
    }

    @Test
    @DisplayName("getUUIDFileName: 파일 확장자가 있을 때 UUID와 확장자를 포함한 파일명 반환")
    void generateUUIDFileName_withExtension() {
        // Given
        String originalFilename = "image.jpeg";
        String expectedExtension = ".jpeg";
        given(mockFile.getOriginalFilename()).willReturn(originalFilename);

        // When
        String uuidFileName = uploader.generateUUIDFileName(mockFile);

        // Then
        assertThat(uuidFileName).isNotNull();
        assertThat(uuidFileName).endsWith(expectedExtension);

        String uuidPart = uuidFileName.substring(0, uuidFileName.lastIndexOf(expectedExtension));
        assertThat(uuidPart).matches(UUID_VALUE);
        assertDoesNotThrow(() -> UUID.fromString(uuidPart)); // Verify it's a parsable UUID
    }

    @Test
    @DisplayName("getUUIDFileName: 파일 확장자가 없을 때 UUID 파일명 반환")
    void generateUUIDFileName_withoutExtension() {
        // Given
        String originalFilename = "myfile";
        given(mockFile.getOriginalFilename()).willReturn(originalFilename);

        // When
        String uuidFileName = uploader.generateUUIDFileName(mockFile);

        // Then
        assertThat(uuidFileName).isNotNull();
        assertThat(uuidFileName).doesNotContain(".");

        assertThat(uuidFileName).matches(UUID_VALUE);
        assertDoesNotThrow(() -> UUID.fromString(uuidFileName));
    }

    @Test
    @DisplayName("getUUIDFileName: 원본 파일명이 null일 때 UUID 파일명 반환")
    void generateUUIDFileName_withNullOriginalFilename() {
        // Given
        given(mockFile.getOriginalFilename()).willReturn(null);

        // When
        String uuidFileName = uploader.generateUUIDFileName(mockFile);

        // Then
        assertThat(uuidFileName).isNotNull();
        assertThat(uuidFileName).doesNotContain(".");
        assertThat(uuidFileName).matches(UUID_VALUE);
        assertDoesNotThrow(() -> UUID.fromString(uuidFileName));
    }

    @Test
    @DisplayName("getUUIDFileName: 원본 파일명이 '.'으로 시작할 때 UUID와 확장자 포함 파일명 반환")
    void generateUUIDFileName_withDotfile() {
        // Given
        String originalFilename = ".gitignore";
        String expectedExtension = ".gitignore";
        given(mockFile.getOriginalFilename()).willReturn(originalFilename);

        // When
        String uuidFileName = uploader.generateUUIDFileName(mockFile);

        // Then
        assertThat(uuidFileName).isNotNull();
        assertThat(uuidFileName).endsWith(expectedExtension);
        String uuidPart = uuidFileName.substring(0, uuidFileName.lastIndexOf(expectedExtension));
        assertThat(uuidPart).matches(UUID_VALUE);
        assertDoesNotThrow(() -> UUID.fromString(uuidPart));
    }


    @Test
    @DisplayName("createTempFile: 성공적으로 임시 파일을 생성하고 내용을 복사")
    void createTempFile_success() throws IOException {
        // Given
        String fileContent = "This is test content.";
        String originalFilename = "test-file.txt";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));

        given(mockFile.getOriginalFilename()).willReturn(originalFilename);
        given(mockFile.getInputStream()).willReturn(inputStream);

        // When
        Path tempFilePath = null;
        try {
            tempFilePath = uploader.createTempFile(mockFile);
            if (tempFilePath != null) {
                filesToDelete.add(tempFilePath);
            }

            // Then
            assertThat(tempFilePath).isNotNull();
            assertThat(Files.exists(tempFilePath)).isTrue();

            String readContent = Files.readString(tempFilePath, StandardCharsets.UTF_8);
            assertThat(readContent).isEqualTo(fileContent);

            assertThat(tempFilePath.getFileName().toString()).startsWith("upload-async-");
            assertThat(tempFilePath.getFileName().toString()).endsWith("-" + originalFilename);

        } finally {
            inputStream.close();
        }
    }

    @Test
    @DisplayName("createTempFile: 파일 스트림 읽기 중 IOException 발생 시 MosException 발생")
    void createTempFile_throwsIOExceptionOnInputStream() throws IOException {
        // Given
        String originalFilename = "bad-stream.txt";
        given(mockFile.getInputStream()).willThrow(new IOException("Simulated getInputStream error"));
        given(mockFile.getOriginalFilename()).willReturn(originalFilename);

        // When & Then
        MosException exception = assertThrows(MosException.class, () -> {
            Path tempFile = null;
            try {
                tempFile = uploader.createTempFile(mockFile);

                if (tempFile != null) filesToDelete.add(tempFile);
            } catch (MosException e) {
                assertThat(e.getErrorCode()).isEqualTo(UploaderErrorCode.FILE_UPLOAD_EXCEPTION);
                throw e;
            }
        });

        assertThat(exception.getErrorCode()).isEqualTo(UploaderErrorCode.FILE_UPLOAD_EXCEPTION);
    }


    @Test
    @DisplayName("deleteTemporaryFile: 존재하는 임시 파일 삭제 성공")
    void deleteTemporaryFile_success() throws IOException {
        // Given
        Path tempFile = Files.createFile(sharedTempDir.resolve("deletable-file.tmp"));
        assertThat(Files.exists(tempFile)).isTrue();

        // When
        uploader.deleteTemporaryFile(tempFile);

        // Then
        assertThat(Files.exists(tempFile)).isFalse();
    }

    @Test
    @DisplayName("deleteTemporaryFile: 존재하지 않는 파일 삭제 시도 시 에러 없이 완료")
    void deleteTemporaryFile_nonExistentFile() {
        // Given
        Path nonExistentFile = sharedTempDir.resolve("non-existent-file.tmp");
        assertThat(Files.exists(nonExistentFile)).isFalse();

        // When & Then
        assertDoesNotThrow(() -> {
            uploader.deleteTemporaryFile(nonExistentFile);
        });

        assertThat(Files.exists(nonExistentFile)).isFalse();
    }


}