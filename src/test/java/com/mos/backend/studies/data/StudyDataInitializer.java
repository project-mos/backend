package com.mos.backend.studies.data;

import com.mos.backend.common.utils.RandomColorGenerator;
import com.mos.backend.securityuser.WithMockCustomUser;
import com.mos.backend.studies.entity.Category;
import com.mos.backend.studies.entity.MeetingType;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.StudyTag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@WithMockCustomUser
@Disabled
public class StudyDataInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final int BULK_INSERT_SIZE = 2000;
    private static final int EXECUTE_COUNT = 5000;

    private final CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

    @Test
    void initializeStudies() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // EXECUTE_COUNT(= 5000)번 반복하며 스레드에게 insert 작업 할당
        for (int i = 0; i < EXECUTE_COUNT; i++) {
            final int batchIndex = i;
            executorService.submit(() -> {
                try {
                    insertStudies(batchIndex);
                } finally {
                    // 작업이 끝나면 latch 카운트 다운
                    latch.countDown();
                    System.out.println("latch.getCount() = " + latch.getCount());
                }
            });
        }

        latch.await();
        executorService.shutdown();

        System.out.println("=== 모든 스터디 데이터 초기화 완료 ===");
    }

    /**
     * 하나의 batchIndex마다 BULK_INSERT_SIZE 만큼의 Study 엔티티를 생성, DB에 저장
     */
    private void insertStudies(int batchIndex) {
        // 트랜잭션 경계 설정 (bulkInsertSize건을 하나의 트랜잭션에서 처리)
        transactionTemplate.executeWithoutResult(status -> {
            Random random = new Random();
            for (int j = 0; j < BULK_INSERT_SIZE; j++) {
                // 전역 순번 계산 (i*bulkInsertSize + j + 1)
                int seq = (batchIndex * BULK_INSERT_SIZE) + j + 1;

                // 랜덤 데이터로 Study 엔티티 생성
                Study study = Study.builder()
                        .title("스터디 " + seq)
                        .content("스터디 내용 " + seq)
                        .maxStudyMemberCount(random.nextInt(10) + 5)
                        .category(Category.values()[random.nextInt(Category.values().length)])
                        .schedule("매주 " + pickRandomDay(random) + "요일")
                        .recruitmentStartDate(LocalDate.now().minusDays(random.nextInt(30)))
                        .recruitmentEndDate(LocalDate.now().plusDays(random.nextInt(30)))
                        .viewCount(random.nextInt(1000))
                        .color(RandomColorGenerator.generateRandomColor())
                        .meetingType(MeetingType.values()[random.nextInt(MeetingType.values().length)])
                        .tags(StudyTag.fromList(Arrays.asList("tag" + random.nextInt(10),
                                "tag" + (random.nextInt(10) + 1))))
                        .requirements("requirement" + random.nextInt(10))
                        .build();

                entityManager.persist(study);
            }
        });
    }

    private String pickRandomDay(Random random) {
        // 예: 0=월, 1=화, 2=수...
        int dayIndex = random.nextInt(7);
        switch (dayIndex) {
            case 0: return "월";
            case 1: return "화";
            case 2: return "수";
            case 3: return "목";
            case 4: return "금";
            case 5: return "토";
            default: return "일";
        }
    }
}