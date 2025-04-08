package com.mos.backend.studymaterials.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymaterials.application.fileuploader.Uploader;
import com.mos.backend.studymaterials.application.responsedto.ReadAllStudyMaterialResponseDto;
import com.mos.backend.studymaterials.application.responsedto.ReadStudyMaterialResponseDto;
import com.mos.backend.studymaterials.entity.StudyMaterial;
import com.mos.backend.studymaterials.entity.StudyMaterialErrorCode;
import com.mos.backend.studymaterials.infrastructure.StudyMaterialRepository;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyMaterialService {

    private static final long MAX_FILE_SIZE_BYTES = 1024 * 1024 * 1024; // 1GB
    private static final long MAX_TOTAL_STUDY_SIZE_BYTES = 10L * 1024 * 1024 * 1024; // 10GB

    private final StudyMaterialRepository studyMaterialRepository;
    private final StudyMemberService studyMemberService;
    private final Uploader uploader;
    private final EntityFacade entityFacade;

    /**
     * StudyMaterial 생성
     */

    @Transactional
    public ReadStudyMaterialResponseDto create(Long studyId, Long userId, UploadType type, MultipartFile file) {
        Study study = entityFacade.getStudy(studyId);
        User user = entityFacade.getUser(userId);
        StudyMember studyMember = studyMemberService.findByStudyAndUser(study, user);

        // 단일 파일, 전체 파일 용량 검증
        validateCreateStudyMaterialRequest(study, type, file);

        String uuidFileName = uploader.generateUUIDFileName(file);
        String filePath = uploader.generateFileUrl(type, studyId, uuidFileName);

        StudyMaterial studyMaterial = StudyMaterial.create(study, studyMember, filePath, file.getOriginalFilename(), file.getSize());
        studyMaterialRepository.save(studyMaterial);

        uploader.uploadFileAsync(userId, uuidFileName, studyId, type, file);

        return ReadStudyMaterialResponseDto.from(studyMaterial);
    }

    /**
     * StudyMaterial 삭제 by studyId && studyMaterialId
     */

    @Transactional
    public void delete(Long studyId, Long studyMaterialId) {
        Study study = entityFacade.getStudy(studyId);
        StudyMaterial studyMaterial = entityFacade.getStudyMaterial(studyMaterialId);
        validateStudyMaterialRequest(study, studyMaterial);
        studyMaterialRepository.delete(studyMaterial);
        uploader.deleteFile(studyMaterial.getFilePath());
    }

    /**
     * StudyMaterial 삭제 by filePath
     */

    @Transactional
    public void delete(String filePath) {
        studyMaterialRepository.deleteByFilePath(filePath);
        uploader.deleteFile(filePath);
    }

    /**
     * StudyMaterial 단 건 조회
     */

    public ReadStudyMaterialResponseDto read(Long studyId, Long studyMaterialId) {
        Study study = entityFacade.getStudy(studyId);
        StudyMaterial studyMaterial = entityFacade.getStudyMaterial(studyMaterialId);
        validateStudyMaterialRequest(study, studyMaterial);
        return ReadStudyMaterialResponseDto.from(studyMaterial);
    }

    /**
     * StudyMaterial 다 건 조회
     */

    public ReadAllStudyMaterialResponseDto readAll(Long studyId) {
        Study study = entityFacade.getStudy(studyId);
        List<ReadStudyMaterialResponseDto> readStudyMaterialResponseDtoList = studyMaterialRepository.findByStudy(study).stream().map(ReadStudyMaterialResponseDto::from).toList();
        Long totalFileSize = getTotalFileSize(study);
        return ReadAllStudyMaterialResponseDto.of(totalFileSize, readStudyMaterialResponseDtoList);
    }

    private void validateStudyMaterialRequest(Study study, StudyMaterial studyMaterial) {
        if (studyMaterial.getStudy() != study) {
            throw new MosException(StudyMaterialErrorCode.STUDY_NOT_MATCH);
        }
    }

    public Long getTotalFileSize(Study study) {
        return studyMaterialRepository.sumTotalFileSizeByStudy(study).orElse(0L);
    }

    private void validateCreateStudyMaterialRequest(Study study, UploadType type, MultipartFile file) {
        if (UploadType.STUDY.equals(type)) {
            if (file.getSize() > MAX_FILE_SIZE_BYTES) {
                throw new MosException(StudyMaterialErrorCode.FILE_SIZE_EXCEEDED);
            }
            Long currentTotalSize = getTotalFileSize(study);
            if (currentTotalSize + file.getSize() > MAX_TOTAL_STUDY_SIZE_BYTES) {
                throw new MosException(StudyMaterialErrorCode.TOTAL_STUDY_SIZE_EXCEEDED);
            }
        }
    }

}
