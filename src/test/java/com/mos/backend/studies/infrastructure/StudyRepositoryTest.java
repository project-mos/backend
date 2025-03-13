package com.mos.backend.studies.infrastructure;

import com.mos.backend.common.argumentresolvers.pageable.CustomPageRequest;
import com.mos.backend.securityuser.WithMockCustomUser;
import com.mos.backend.studies.StudyFixture;
import com.mos.backend.studies.application.responsedto.StudiesResponseDto;
import com.mos.backend.studies.entity.Category;
import com.mos.backend.studies.entity.MeetingType;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.StudyTag;
import com.mos.backend.testconfig.AbstractTestContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@WithMockCustomUser(userId = 1L)
class StudyRepositoryTest extends AbstractTestContainer{

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @DisplayName("동시성 테스트 - ViewCount 증가")
    @DirtiesContext
    void increaseViewCountConcurrencyTest() throws InterruptedException {

        Study study = Study.builder()
                .title("Concurrency Test")
                .content("Test Content")
                .maxStudyMemberCount(5)
                .category(Category.PROGRAMMING)
                .schedule("Test Schedule")
                .recruitmentStartDate(now())
                .recruitmentEndDate(now())
                .viewCount(0)
                .color("red")
                .meetingType(MeetingType.OFFLINE)
                .tags(StudyTag.fromList(Arrays.asList("tag1", "tag2")))
                .requirements("None")
                .build();

        Study savedStudy = studyRepository.save(study);

        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 각 스레드에서 새로운 트랜잭션 범위 내에서 increaseViewCount 호출
                    transactionTemplate.execute(status -> {
                        studyRepository.increaseViewCount(savedStudy.getId());
                        return null;
                    });
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Study updatedStudy = studyRepository.findById(savedStudy.getId()).orElseThrow();
        assertEquals(numberOfThreads, updatedStudy.getViewCount());
    }

    @Test
    @DisplayName("스터디 목록 조회 테스트")
    @DirtiesContext
    void findStudiesQueryTest() {
        // given
        int studyCountPerCategory = 70;
        int categorySize = Category.values().length;
        int pageSize = 12;
        int pageNum = 5;
        String category = Category.PROGRAMMING.getDescription();
//        String category = null;
        List<Study> studies = StudyFixture.createStudiesPerCategoryWithCount(studyCountPerCategory);
        for (Study study : studies) {
            studyRepository.save(study);
        }

        PageRequest pageRequest = CustomPageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        LocalDateTime start = LocalDateTime.now();
        Page<StudiesResponseDto> findStudies = studyRepository.findStudies(pageRequest, category, null, null, null);
        LocalDateTime end = LocalDateTime.now();
        Duration between = Duration.between(start, end);
        System.out.printf("time1 : %d ms \n", between.toMillis());

        List<StudiesResponseDto> content = findStudies.getContent();
        long totalCount = findStudies.getTotalElements();
        System.out.println("totalCount = " + totalCount);
        int totalPages = findStudies.getTotalPages();
        int pageNumber = findStudies.getPageable().getPageNumber();

        // then
        assertThat(studyRepository.count()).isEqualTo(studies.size());
        assertThat(findStudies).hasSizeLessThanOrEqualTo(pageSize);
        int totalCountAnswer = category == null ? categorySize * studyCountPerCategory : studyCountPerCategory;
        assertThat(totalCount).isEqualTo(totalCountAnswer);
        long totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : (totalCount / pageSize) + 1;
        assertThat(totalPages).isEqualTo(totalPage);
        assertThat(pageNumber).isEqualTo(pageNum);

        int count = pageNum * pageSize;
        for (StudiesResponseDto studiesResponseDto : content) {
            assertThat(studiesResponseDto.getStudyId()).isEqualTo(totalCountAnswer - count);
            System.out.println("studiesResponseDto = " + studiesResponseDto);
            count++;
        }
    }

}