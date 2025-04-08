package com.mos.backend.studymaterials.application.fileuploader;

import com.mos.backend.studymaterials.application.UploadType;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface Uploader {

    String generateFileUrl(UploadType type, Long folderName, String fileName);

    String uploadFileSync(String fileName, Long folderName, UploadType type, MultipartFile file);
    void uploadFileAsync(Long userId, String fileName, Long folderName, UploadType type, MultipartFile file);

    Path createTempFile(MultipartFile file);
    void deleteFile(String uriString);

    String generateUUIDFileName(MultipartFile file);
    String uriToFileObjectKey(String uriString);
    String generateFileObjectKey(UploadType type, Long folderName, String fileName);

    void moveFile(String sourceKey, String destinationKey);
}
