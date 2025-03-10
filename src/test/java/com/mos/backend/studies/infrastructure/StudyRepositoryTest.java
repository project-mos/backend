package com.mos.backend.studies.infrastructure;

import com.mos.backend.studies.entity.Category;
import com.mos.backend.studies.entity.MeetingType;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.StudyTag;
import com.mos.backend.testconfig.AbstractTestContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StudyRepositoryTest extends AbstractTestContainer {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @DisplayName("동시성 테스트 - ViewCount 증가")
    @DirtiesContext
    void testIncreaseViewCountConcurrency() throws InterruptedException {

        Study study = Study.builder()
                .title("Concurrency Test")
                .content("Test Content")
                .maxParticipantsCount(5)
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
}