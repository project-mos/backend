package com.mos.backend.hotstudies.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class HotStudyRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String KEY_FORMAT = "HOT-STUDY::LIST::%s";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public void add(Long studyId, Long score, Long limit, Duration ttl) {
        redisTemplate.executePipelined( (RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;
            String key = generateKey(LocalDateTime.now());
            conn.zAdd(key, score, String.valueOf(studyId));
            conn.zRemRange(key, 0, - limit - 1);
            conn.expire(key, ttl.toSeconds());
            return null;
        });
    }

    public void remove(Long studyId) {
        redisTemplate.opsForZSet().remove(generateKey(LocalDateTime.now()), String.valueOf(studyId));
    }

    public List<Long> readAll() {
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(generateKey(LocalDateTime.now()), 0, -1);
        if (tuples == null) {
            return Collections.emptyList();
        }
        return tuples.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();
    }

    private String generateKey(LocalDateTime time) {
        return generateKey(TIME_FORMATTER.format(time));
    }

    private String generateKey(String dateStr) {
        return KEY_FORMAT.formatted(dateStr);
    }

}
