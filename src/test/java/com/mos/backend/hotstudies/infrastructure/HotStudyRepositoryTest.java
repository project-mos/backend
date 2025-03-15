package com.mos.backend.hotstudies.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HotStudyRepositoryTest extends AbstractMethodError{

    @Autowired
    private HotStudyRepository hotStudyRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String KEY_FORMAT = "HOT-STUDY::LIST::%s";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Long LIMIT = 5L;
    private static final Duration TTL = Duration.ofDays(1);
    private String key;

    @BeforeEach
    void setup() {
        key = generateKey(LocalDateTime.now());
        redisTemplate.delete(key); // 이전 테스트 데이터 삭제
    }

    @Test
    @DisplayName("스터디 추가")
    void addStudy() {
        //given
        Long studyId = 1L;
        Long score = 10L;

        //when
        hotStudyRepository.add(studyId, score, LIMIT, TTL);

        //then
        Set<ZSetOperations.TypedTuple<String>> result = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        assertThat(result.size()).isEqualTo(1);
        result.forEach(tuple -> {
            assertThat(Long.valueOf(tuple.getValue())).isEqualTo(studyId);
            assertThat(tuple.getScore()).isEqualTo(score.doubleValue());
        });
        assertThat(redisTemplate.getExpire(key)).isGreaterThan(0);

    }

    @Test
    @DisplayName("스터디 삭제")
    void removeStudy() {
        //given
        Long studyId = 1L;
        Long score = 10L;
        hotStudyRepository.add(studyId, score, LIMIT, TTL);

        //when
        hotStudyRepository.remove(studyId);

        //then
        Set<ZSetOperations.TypedTuple<String>> result = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("스터디 목록 전체 조회")
    void readAllStudies() {
        //given
        Long studyId1 = 1L;
        Long studyId2 = 2L;
        Long studyId3 = 3L;
        hotStudyRepository.add(studyId1, 10L, LIMIT, TTL);
        hotStudyRepository.add(studyId2, 20L, LIMIT, TTL);
        hotStudyRepository.add(studyId3, 30L, LIMIT, TTL);

        //when
        List<Long> allStudies = hotStudyRepository.readAll();

        //then
        assertThat(allStudies.size()).isEqualTo(3);
        assertThat(allStudies).containsExactly(studyId3, studyId2, studyId1);

    }

    @Test
    @DisplayName("limit를 넘었을 때 삭제되는지 확인")
    void removeOverLimitStudies() {
        //given
        Long studyId1 = 1L;
        Long studyId2 = 2L;
        Long studyId3 = 3L;
        Long studyId4 = 4L;
        Long studyId5 = 5L;
        Long studyId6 = 6L;
        hotStudyRepository.add(studyId1, 10L, LIMIT, TTL);
        hotStudyRepository.add(studyId2, 20L, LIMIT, TTL);
        hotStudyRepository.add(studyId3, 30L, LIMIT, TTL);
        hotStudyRepository.add(studyId4, 40L, LIMIT, TTL);
        hotStudyRepository.add(studyId5, 50L, LIMIT, TTL);

        //when
        hotStudyRepository.add(studyId6, 60L, LIMIT, TTL);

        //then
        Set<ZSetOperations.TypedTuple<String>> result = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        assertThat(result.size()).isEqualTo(5);
        assertThat(hotStudyRepository.readAll()).containsExactly(studyId6, studyId5, studyId4, studyId3, studyId2);
    }

    private String generateKey(LocalDateTime time) {
        return generateKey(TIME_FORMATTER.format(time));
    }

    private String generateKey(String dateStr) {
        return KEY_FORMAT.formatted(dateStr);
    }
}