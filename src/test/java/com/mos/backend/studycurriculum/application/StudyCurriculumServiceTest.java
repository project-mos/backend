package com.mos.backend.studycurriculum.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studycurriculum.application.responsedto.StudyCurriculumResponseDto;
import com.mos.backend.studycurriculum.entity.StudyCurriculum;
import com.mos.backend.studycurriculum.infrastructure.StudyCurriculumRepository;
import com.mos.backend.studycurriculum.presentation.requestdto.StudyCurriculumCreateRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyCurriculumServiceTest {

    @Mock
    private StudyCurriculumRepository studyCurriculumRepository;

    @Mock
    private EntityFacade entityFacade;

    @InjectMocks
    private StudyCurriculumService studyCurriculumService;


    @Nested
    @DisplayName("스터디 커리큘럼 생성, 수정, 삭제 성공 시나리오")
    class SuccessScenarios {
        @Test
        @DisplayName("새로운 스터디 커리큘럼 생성")
        void createStudyCurriculum_Success() {
            // Given
            Long studyId = 1L;
            List<StudyCurriculumCreateRequestDto> contents = List.of(
                    new StudyCurriculumCreateRequestDto(null, 1L, "커리큘렴 제목 1", "커리큘럼 내용 2"),
                    new StudyCurriculumCreateRequestDto(null, 2L, "커리큘럼 제목 2", "커리큘럼 내용 2")
            );
            Study mockStudy = Study.builder().id(studyId).build();
            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(studyCurriculumRepository.findAllByStudy(mockStudy)).thenReturn(new ArrayList<>());
            // When
            studyCurriculumService.createOrUpdateOrDelete(studyId, contents);

            // Then
            verify(studyCurriculumRepository, times(2)).save(any(StudyCurriculum.class));
        }

        @Test
        @DisplayName("기존 스터디 커리큘럼 수정")
        void updateStudyCurriculum_Success() {
            // Given
            Long studyId = 1L;
            Study mockStudy = Study.builder().id(studyId).build();

            StudyCurriculumCreateRequestDto curriculumCreateRequestDto = new StudyCurriculumCreateRequestDto(1L, 1L, "제목", "내용");

            StudyCurriculum mockStudyCurriculum = mock(StudyCurriculum.class);
            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(studyCurriculumRepository.findByIdAndStudy(studyId, mockStudy)).thenReturn(Optional.of(mockStudyCurriculum));

            // when
            studyCurriculumService.createOrUpdateOrDelete(studyId, List.of(curriculumCreateRequestDto));

            verify(mockStudyCurriculum).update(curriculumCreateRequestDto.getTitle(), curriculumCreateRequestDto.getSectionId(), curriculumCreateRequestDto.getContent());
        }

        @Test
        @DisplayName("기존 스터디 혜택 삭제")
        void deleteStudyBenefit_Success() {
            // Given
            Long studyId = 1L;
            Study study = Study.builder().id(studyId).build();

            List<StudyCurriculumCreateRequestDto> contents = List.of(
                    new StudyCurriculumCreateRequestDto(null, 1L, "커리큘렴 제목 1", "커리큘럼 내용 2")
            );

            StudyCurriculum studyCurriculum1 = spy(StudyCurriculum.create(study, "제목1",1L, "혜택1"));
            doReturn(1L).when(studyCurriculum1).getId();
            StudyCurriculum studyCurriculum2 = spy(StudyCurriculum.create(study, "제목2",2L, "혜택2"));
            doReturn(2L).when(studyCurriculum2).getId();

            when(studyCurriculumRepository.findAllByStudy(study)).thenReturn(new ArrayList<>(List.of(studyCurriculum1, studyCurriculum2)));
            when(entityFacade.getStudy(studyId)).thenReturn(study);

            // When
            studyCurriculumService.createOrUpdateOrDelete(studyId, contents);

            // Then
            verify(studyCurriculumRepository).deleteAll(List.of(studyCurriculum1, studyCurriculum2));
        }
    }

    @DisplayName("스터디 단 건 조회 테스트 성공")
    @Test
    void getStudyCurriculum_Success() {

        // given
        Long studyId = 1L;
        Long studyCurriculumId = 1L;

        Study study = Study.builder().id(studyId).build();
        StudyCurriculum studyCurriculum = StudyCurriculum.create(study, "제목", 1L, "내용");
        when(entityFacade.getStudy(studyId)).thenReturn(study);
        when(studyCurriculumRepository.findByIdAndStudy(studyCurriculumId, study)).thenReturn(Optional.of(studyCurriculum));

        // when
        StudyCurriculumResponseDto responseDto = studyCurriculumService.get(studyId, studyCurriculumId);

        // then
        Assertions.assertThat(responseDto.getSectionId()).isEqualTo(1L);
        Assertions.assertThat(responseDto.getTitle()).isEqualTo("제목");
        Assertions.assertThat(responseDto.getContent()).isEqualTo("내용");
    }

    @DisplayName("스터디 다 건 조회 테스트 성공")
    @Test
    void getAllStudyCurriculums_Success() {

        // given
        Long studyId = 1L;

        Study study = Study.builder().id(studyId).build();
        StudyCurriculum studyCurriculum1 = StudyCurriculum.create(study, "제목1", 1L, "내용1");
        StudyCurriculum studyCurriculum2 = StudyCurriculum.create(study, "제목2", 2L, "내용2");
        when(entityFacade.getStudy(studyId)).thenReturn(study);
        when(studyCurriculumRepository.findAllByStudy(study)).thenReturn(new ArrayList<>(List.of(studyCurriculum1, studyCurriculum2)));
        // when
        List<StudyCurriculumResponseDto> responseDto = studyCurriculumService.getAll(studyId);

        // then
        Assertions.assertThat(responseDto).hasSize(2);
    }
}