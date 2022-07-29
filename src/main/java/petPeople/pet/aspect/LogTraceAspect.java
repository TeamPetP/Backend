package petPeople.pet.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import petPeople.pet.Trace.TraceStatus;
import petPeople.pet.Trace.logtrace.LogTrace;


@Slf4j
@Aspect//자동 프록시 생성기가 @Aspect 를 Advisor 로 변환
public class LogTraceAspect {

    private final LogTrace logTrace;

    public LogTraceAspect(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    @Around("execution(public * petPeople.pet.controller..*(..))")//포인트 컷
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {//어드바이스
        TraceStatus status = null;

        try {
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            //로직 호출
            Object result = joinPoint.proceed();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

}
