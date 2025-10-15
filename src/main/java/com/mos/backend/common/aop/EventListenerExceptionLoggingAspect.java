package com.mos.backend.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class EventListenerExceptionLoggingAspect {

    @AfterThrowing(pointcut = "@annotation(com.mos.backend.common.aop.LogOnException)", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("Exception in {}() with cause = '{}'",
                joinPoint.getSignature().toShortString(), ex.getMessage(), ex);
    }
}
