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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class StudyMaterialServiceTest {

    @InjectMocks
    private StudyMaterialService studyMaterialService;

    @Mock private StudyMaterialRepository studyMaterialRepository;
    @Mock private StudyMemberService studyMemberService;
    @Mock private Uploader uploader;
    @Mock private EntityFacade entityFacade;

    @Mock private Study mockStudy;
    @Mock private User mockUser;
    @Mock private StudyMember mockStudyMember;
    @Mock private StudyMaterial mockStudyMaterial;

    @Captor private ArgumentCaptor<StudyMaterial> studyMaterialCaptor;
    @Captor private ArgumentCaptor<String> stringCaptor;

    private final Long STUDY_ID = 1L;
    private final Long USER_ID = 10L;
    private final Long STUDY_MEMBER_ID = 100L;
    private final Long STUDY_MATERIAL_ID = 1000L;
    private final String FILE_PATH = "study/1/dummy-uuid.txt";
    private final String ORIGINAL_FILENAME = "original.txt";
    private final long FILE_SIZE = 1024L; // 1KB
    private final long MAX_FILE_SIZE = 1024 * 1024 * 1024;
    private final long MAX_TOTAL_SIZE = 10L * 1024 * 1024 * 1024;


    @Test
    @DisplayName("create: 스터디 자료 생성 및 비동기 업로드 요청 성공")
    void create_Success() {
        // Given
        given(entityFacade.getStudy(STUDY_ID)).willReturn(mockStudy);
        given(mockStudy.getId()).willReturn(STUDY_ID);
        given(entityFacade.getUser(USER_ID)).willReturn(mockUser);
        given(mockUser.getId()).willReturn(USER_ID);
        given(studyMemberService.findByStudyAndUser(mockStudy, mockUser)).willReturn(mockStudyMember);
        given(mockStudyMember.getId()).willReturn(STUDY_MEMBER_ID);
        given(mockStudyMember.getUser()).willReturn(mockUser);
        given(studyMaterialRepository.sumTotalFileSizeByStudy(mockStudy)).willReturn(Optional.of(0L));
        given(uploader.generateUUIDFileName(any(MultipartFile.class))).willReturn("dummy-uuid.txt");
        given(uploader.generateFileUrl(UploadType.STUDY, STUDY_ID, "dummy-uuid.txt")).willReturn(FILE_PATH);


        MultipartFile mockMultipartFile = new MockMultipartFile("file", ORIGINAL_FILENAME, "text/plain", new byte[(int)FILE_SIZE]);

        // When
        ReadStudyMaterialResponseDto resultDto = studyMaterialService.create(STUDY_ID, USER_ID, UploadType.STUDY, mockMultipartFile);

        // Then
        verify(studyMaterialRepository).save(studyMaterialCaptor.capture());
        StudyMaterial savedMaterial = studyMaterialCaptor.getValue();
        assertThat(savedMaterial.getStudy()).isEqualTo(mockStudy);
        assertThat(savedMaterial.getStudyMember()).isEqualTo(mockStudyMember);
        assertThat(savedMaterial.getFilePath()).isEqualTo(FILE_PATH);
        assertThat(savedMaterial.getOriginalName()).isEqualTo(ORIGINAL_FILENAME);
        assertThat(savedMaterial.getFileSize()).isEqualTo(FILE_SIZE);

        verify(uploader).uploadFileAsync(eq(USER_ID), eq("dummy-uuid.txt"), eq(STUDY_ID), eq(UploadType.STUDY), eq(mockMultipartFile));

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getFilePath()).isEqualTo(FILE_PATH);
        assertThat(resultDto.getStudyId()).isEqualTo(STUDY_ID);
        assertThat(resultDto.getStudyMemberId()).isEqualTo(STUDY_MEMBER_ID);
        assertThat(resultDto.getOriginalName()).isEqualTo(ORIGINAL_FILENAME);
        assertThat(resultDto.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("create: 단일 파일 크기 제한 초과 시 예외 발생")
    void create_Fail_FileSizeExceeded() {
        // Given
        given(entityFacade.getStudy(STUDY_ID)).willReturn(mockStudy);
        given(entityFacade.getUser(USER_ID)).willReturn(mockUser);
        given(studyMemberService.findByStudyAndUser(mockStudy, mockUser)).willReturn(mockStudyMember);
        MultipartFile largeFile = spy(new MockMultipartFile("file", "large.zip", "application/zip", new byte[0]));
        doReturn(MAX_FILE_SIZE + 1L).when(largeFile).getSize();

        // When & Then
        assertThatThrownBy(() -> studyMaterialService.create(STUDY_ID, USER_ID, UploadType.STUDY, largeFile))
                .isInstanceOf(MosException.class)
                .hasFieldOrPropertyWithValue("errorCode", StudyMaterialErrorCode.FILE_SIZE_EXCEEDED);

        verify(studyMaterialRepository, never()).save(any());
        verify(uploader, never()).uploadFileAsync(anyLong(), any(), anyLong(), any(), any());
    }

    @Test
    @DisplayName("create: 총 파일 크기 제한 초과 시 예외 발생")
    void create_Fail_TotalSizeExceeded() {
        // Given
        given(entityFacade.getStudy(STUDY_ID)).willReturn(mockStudy);
        given(entityFacade.getUser(USER_ID)).willReturn(mockUser);
        given(studyMemberService.findByStudyAndUser(mockStudy, mockUser)).willReturn(mockStudyMember);
        given(studyMaterialRepository.sumTotalFileSizeByStudy(mockStudy)).willReturn(Optional.of(MAX_TOTAL_SIZE - FILE_SIZE + 1));

        MultipartFile mockMultipartFile = new MockMultipartFile("file", ORIGINAL_FILENAME, "text/plain", new byte[(int)FILE_SIZE]);

        // When & Then
        assertThatThrownBy(() -> studyMaterialService.create(STUDY_ID, USER_ID, UploadType.STUDY, mockMultipartFile))
                .isInstanceOf(MosException.class)
                .hasFieldOrPropertyWithValue("errorCode", StudyMaterialErrorCode.TOTAL_STUDY_SIZE_EXCEEDED);

        verify(studyMaterialRepository, never()).save(any());
        verify(uploader, never()).uploadFileAsync(any(), any(), anyLong(), any(), any());
    }
//
    @Test
    @DisplayName("create: UploadType이 STUDY가 아닐 경우 크기 검증 건너뜀")
    void create_Success_NonStudyTypeSkipsValidation() {
        // Given: 필요한 Mock 객체들의 동작 설정 (성공 케이스와 유사)
        given(entityFacade.getStudy(STUDY_ID)).willReturn(mockStudy);
        given(entityFacade.getUser(USER_ID)).willReturn(mockUser);
        given(studyMemberService.findByStudyAndUser(mockStudy, mockUser)).willReturn(mockStudyMember);
        given(mockStudyMember.getUser()).willReturn(mockUser);
        given(mockStudyMember.getId()).willReturn(STUDY_MEMBER_ID);
        given(uploader.generateUUIDFileName(any(MultipartFile.class))).willReturn("uuid-other.png");

        MultipartFile largeFileButNotStudyType = spy(new MockMultipartFile("file", "profile.png", "image/png", new byte[(int) (0)]));
        doReturn(MAX_FILE_SIZE + 10L).when(largeFileButNotStudyType).getSize();


        // When
        ReadStudyMaterialResponseDto resultDto = studyMaterialService.create(STUDY_ID, USER_ID, UploadType.TEMP, largeFileButNotStudyType);

        // Then
        verify(studyMaterialRepository).save(any(StudyMaterial.class));
        verify(uploader).uploadFileAsync(eq(USER_ID), eq("uuid-other.png"), eq(STUDY_ID), eq(UploadType.TEMP), eq(largeFileButNotStudyType));
        verify(studyMaterialRepository, never()).sumTotalFileSizeByStudy(any());
        assertThat(resultDto).isNotNull();
    }

    @Test
    @DisplayName("deleteById: 스터디 자료 ID로 삭제 성공")
    void deleteById_Success() {
        // Given
        given(entityFacade.getStudy(STUDY_ID)).willReturn(mockStudy);
        given(entityFacade.getStudyMaterial(STUDY_MATERIAL_ID)).willReturn(mockStudyMaterial);
        given(mockStudyMaterial.getStudy()).willReturn(mockStudy);
        given(mockStudyMaterial.getFilePath()).willReturn(FILE_PATH);
        willDoNothing().given(studyMaterialRepository).delete(any(StudyMaterial.class));
        willDoNothing().given(uploader).deleteFile(anyString());

        // When
        studyMaterialService.delete(STUDY_ID, STUDY_MATERIAL_ID);

        // Then
        verify(studyMaterialRepository).delete(mockStudyMaterial);
        verify(uploader).deleteFile(FILE_PATH);
    }

    @Test
    @DisplayName("deleteById: 스터디 ID와 자료의 스터디가 불일치 시 예외 발생")
    void deleteById_Fail_StudyNotMatch() {
        // Given
        Study anotherStudy = mock(Study.class);
        given(entityFacade.getStudy(STUDY_ID)).willReturn(mockStudy);
        given(entityFacade.getStudyMaterial(STUDY_MATERIAL_ID)).willReturn(mockStudyMaterial);
        given(mockStudyMaterial.getStudy()).willReturn(anotherStudy);

        // When & Then:
        assertThatThrownBy(() -> studyMaterialService.delete(STUDY_ID, STUDY_MATERIAL_ID))
                .isInstanceOf(MosException.class)
                .hasFieldOrPropertyWithValue("errorCode", StudyMaterialErrorCode.STUDY_NOT_MATCH);

        verify(studyMaterialRepository, never()).delete(any());
        verify(uploader, never()).deleteFile(anyString());
    }

    @Test
    @DisplayName("deleteByPath: 파일 경로로 삭제 성공")
    void deleteByPath_Success() {
        // Given
        willDoNothing().given(studyMaterialRepository).deleteByFilePath(anyString());
        willDoNothing().given(uploader).deleteFile(anyString());

        // When
        studyMaterialService.delete(FILE_PATH);

        // Then
        verify(studyMaterialRepository).deleteByFilePath(FILE_PATH);
        verify(uploader).deleteFile(FILE_PATH);
    }

    @Test
    @DisplayName("read: 스터디 자료 단건 조회 성공")
    void read_Success() {
        // Given
        given(entityFacade.getStudy(STUDY_ID)).willReturn(mockStudy);
        given(entityFacade.getStudyMaterial(STUDY_MATERIAL_ID)).willReturn(mockStudyMaterial);
        given(mockStudyMaterial.getStudy()).willReturn(mockStudy);
        given(mockStudyMaterial.getId()).willReturn(STUDY_MATERIAL_ID);
        given(mockStudyMaterial.getFilePath()).willReturn(FILE_PATH);
        given(mockStudyMaterial.getOriginalName()).willReturn(ORIGINAL_FILENAME);
        given(mockStudyMaterial.getFileSize()).willReturn(FILE_SIZE);
        given(mockStudyMaterial.getStudyMember()).willReturn(mockStudyMember);
        given(mockStudyMember.getId()).willReturn(STUDY_MEMBER_ID);
        given(mockStudyMember.getUser()).willReturn(mockUser);

        // When
        ReadStudyMaterialResponseDto resultDto = studyMaterialService.read(STUDY_ID, STUDY_MATERIAL_ID);

        // Then
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getFilePath()).isEqualTo(FILE_PATH);
        assertThat(resultDto.getOriginalName()).isEqualTo(ORIGINAL_FILENAME);
        assertThat(resultDto.getFileSize()).isEqualTo(FILE_SIZE);
        assertThat(resultDto.getStudyMemberId()).isEqualTo(STUDY_MEMBER_ID);
    }

    @Test
    @DisplayName("read: 스터디 ID와 자료의 스터디가 불일치 시 예외 발생")
    void read_Fail_StudyNotMatch() {
        // Given
        Study anotherStudy = mock(Study.class);
        given(entityFacade.getStudy(STUDY_ID)).willReturn(mockStudy);
        given(entityFacade.getStudyMaterial(STUDY_MATERIAL_ID)).willReturn(mockStudyMaterial);
        given(mockStudyMaterial.getStudy()).willReturn(anotherStudy); // 스터디 불일치

        // When & Then
        assertThatThrownBy(() -> studyMaterialService.read(STUDY_ID, STUDY_MATERIAL_ID))
                .isInstanceOf(MosException.class)
                .hasFieldOrPropertyWithValue("errorCode", StudyMaterialErrorCode.STUDY_NOT_MATCH);
    }

    @Test
    @DisplayName("readAll: 특정 스터디의 모든 자료 조회 성공")
    void readAll_Success() {
        // Given
        StudyMaterial material1 = mock(StudyMaterial.class);
        StudyMaterial material2 = mock(StudyMaterial.class);

        User mockUser = mock(User.class);

        StudyMember mockStudyMember = mock(StudyMember.class);
        given(mockStudyMember.getUser()).willReturn(mockUser);

        List<StudyMaterial> materialList = List.of(material1, material2);
        long totalSize = 1500L;

        given(entityFacade.getStudy(STUDY_ID)).willReturn(mockStudy);
        given(studyMaterialRepository.findByStudy(mockStudy)).willReturn(materialList);
        given(studyMaterialRepository.sumTotalFileSizeByStudy(mockStudy)).willReturn(Optional.of(totalSize));

        given(material1.getId()).willReturn(1001L);
        given(material1.getStudyMember()).willReturn(mockStudyMember);
        given(material1.getStudy()).willReturn(mockStudy);

        given(material2.getId()).willReturn(1002L);
        given(material2.getStudyMember()).willReturn(mockStudyMember);
        given(material2.getStudy()).willReturn(mockStudy);

        // When
        ReadAllStudyMaterialResponseDto resultDto = studyMaterialService.readAll(STUDY_ID);

        // Then
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getTotalFileSize()).isEqualTo(totalSize);
        assertThat(resultDto.getFileList()).hasSize(2);
        assertThat(resultDto.getFileList().get(0).getId()).isEqualTo(1001L);
        assertThat(resultDto.getFileList().get(1).getId()).isEqualTo(1002L);
    }

    @Test
    @DisplayName("readAll: 스터디 자료가 없을 경우 빈 리스트와 총 크기 0 반환")
    void readAll_Success_NoMaterials() {
        // Given
        given(entityFacade.getStudy(STUDY_ID)).willReturn(mockStudy);
        given(studyMaterialRepository.findByStudy(mockStudy)).willReturn(List.of());
        given(studyMaterialRepository.sumTotalFileSizeByStudy(mockStudy)).willReturn(Optional.empty());

        // When
        ReadAllStudyMaterialResponseDto resultDto = studyMaterialService.readAll(STUDY_ID);

        // Then
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getTotalFileSize()).isEqualTo(0L);
        assertThat(resultDto.getFileList()).isEmpty();
    }

    @Test
    @DisplayName("getTotalFileSize: 스터디의 총 파일 크기 합계 반환 성공")
    void getTotalFileSize_Success() {
        // Given
        long expectedSize = 12345L;
        given(studyMaterialRepository.sumTotalFileSizeByStudy(mockStudy)).willReturn(Optional.of(expectedSize));

        // When
        Long actualSize = studyMaterialService.getTotalFileSize(mockStudy);

        // Then
        assertThat(actualSize).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("getTotalFileSize: 자료가 없어 합계가 없을 경우 0L 반환")
    void getTotalFileSize_Success_NoSumResult() {
        // Given
        given(studyMaterialRepository.sumTotalFileSizeByStudy(mockStudy)).willReturn(Optional.empty());

        // When
        Long actualSize = studyMaterialService.getTotalFileSize(mockStudy);

        // Then
        assertThat(actualSize).isEqualTo(0L);
    }
}