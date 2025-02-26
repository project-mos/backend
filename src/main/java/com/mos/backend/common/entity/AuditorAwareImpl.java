package com.mos.backend.common.entity;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    private static final ThreadLocal<String> asyncUserId = new ThreadLocal<>();
    @Override
    public Optional<String> getCurrentAuditor() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String updatedBy = request.getHeader("X-USER-ID");
            if (updatedBy == null) {
                throw new SecurityException("X-USER-ID is required");
            }
            return Optional.of(updatedBy);
        }

        // 비동기 작업 중인 경우 ThreadLocal에서 사용자 정보 가져오기
        String userId = asyncUserId.get();
        if (userId == null) {
            throw new SecurityException("X-USER-ID is required");
        }

        // 기본 사용자 설정
        return Optional.of(userId);
    }

    public static void setAsyncUserId(String userId) {
        asyncUserId.set(userId);
    }

    public static void clearAsyncUserId() {
        asyncUserId.remove();
    }

}
