package com.mos.backend.hotstudies.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class HotStudyViewRepository {

    private final RedisTemplate<String, Long> redisTemplate;

    private static final String KEY_FORMAT = "HOT-STUDY::STUDY::%s::VIEW-COUNT";

    public void createOrUpdate(Long studyId, Long count, Duration ttl) {
        String key = generateKey(studyId);
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, 1L, ttl);
        if (Boolean.FALSE.equals(isNew)) {
            Long newValue = redisTemplate.opsForValue().increment(key, count);
            if (newValue < 0) {
                redisTemplate.opsForValue().set(key, 0L, ttl);
            }
        }
    }

    public Long read(Long studyId) {
        Long count = redisTemplate.opsForValue().get(generateKey(studyId));
        return count != null ? count : 0L;
    }

    private String generateKey(Long studyId) {
        return KEY_FORMAT.formatted(studyId);
    }


}
