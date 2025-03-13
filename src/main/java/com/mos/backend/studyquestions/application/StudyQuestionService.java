package com.mos.backend.studyquestions.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studyquestions.entity.QuestionOption;
import com.mos.backend.studyquestions.entity.QuestionType;
import com.mos.backend.studyquestions.entity.StudyQuestion;
import com.mos.backend.studyquestions.entity.StudyQuestionErrorCode;
import com.mos.backend.studyquestions.infrastructure.StudyQuestionRepository;
import com.mos.backend.studyquestions.presentation.requestdto.StudyQuestionCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyQuestionService {

    private final StudyQuestionRepository studyQuestionRepository;
    private final StudyRepository studyRepository;

    @Transactional
    public void create(Long studyId, List<StudyQuestionCreateRequestDto> studyQuestionCreateRequestDtoList) {
        if (studyQuestionCreateRequestDtoList.isEmpty()) {
            return;
        }

        Study study = getStudyById(studyId);

        // 검증 메서드 호출
        validateStudyQuestions(studyQuestionCreateRequestDtoList);

        // DTO -> Entity 변환 후 저장
        List<StudyQuestion> studyQuestions = studyQuestionCreateRequestDtoList.stream()
                .map(dto -> convertToEntity(study, dto))
                .toList();

        studyQuestionRepository.saveAll(studyQuestions);
    }

    private Study getStudyById(Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new MosException(StudyErrorCode.STUDY_NOT_FOUND));
    }

    private void validateStudyQuestions(List<StudyQuestionCreateRequestDto> studyQuestionCreateRequestDtoList) {
        for (StudyQuestionCreateRequestDto dto : studyQuestionCreateRequestDtoList) {
            validateStudyQuestion(dto);
        }
    }

    private void validateStudyQuestion(StudyQuestionCreateRequestDto dto) {
        QuestionType questionType = QuestionType.fromDescription(dto.getType());
        List<String> options = dto.getOptions();

        if (QuestionType.MULTIPLE_CHOICE.equals(questionType) && (options == null || options.size() < 2)) {
            throw new MosException(StudyQuestionErrorCode.INVALID_MULTIPLE_CHOICE_OPTIONS);
        }
    }

    private StudyQuestion convertToEntity(Study study, StudyQuestionCreateRequestDto dto) {
        return StudyQuestion.create(
                study,
                dto.getQuestionNum(),
                dto.getQuestion(),
                QuestionType.fromDescription(dto.getType()),
                QuestionOption.fromList(dto.getOptions()),
                dto.isRequired()
        );
    }
}
