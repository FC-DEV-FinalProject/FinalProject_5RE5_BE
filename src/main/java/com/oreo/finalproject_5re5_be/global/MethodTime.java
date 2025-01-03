package com.oreo.finalproject_5re5_be.global;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class MethodTime {
    private static final Logger log = LoggerFactory.getLogger(MethodTime.class);

    @Around("execution(* com.oreo.finalproject_5re5_be.code.controller.*.*(..)) || " +
            "execution(* com.oreo.finalproject_5re5_be.concat.controller.*.*(..)) || " +
            "execution(* com.oreo.finalproject_5re5_be.global.controller.*.*(..)) || " +
            "execution(* com.oreo.finalproject_5re5_be.member.controller.*.*(..)) || " +
            "execution(* com.oreo.finalproject_5re5_be.tts.controller.*.*(..)) || " +
            "execution(* com.oreo.finalproject_5re5_be.vc.controller.*.*(..)) || " +
            "execution(* com.oreo.finalproject_5re5_be.project.controller.*.*(..))")

    public Object executionAspect(ProceedingJoinPoint joinPoint) throws Throwable{
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info("시간측정 time for method [{}]: {} ms",
                    joinPoint.getSignature().toShortString(),
                    stopWatch.getTotalTimeMillis());
        }
    }
}
