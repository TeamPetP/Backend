package petPeople.pet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import petPeople.pet.Trace.logtrace.LogTrace;
import petPeople.pet.Trace.logtrace.ThreadLocalLogTrace;
import petPeople.pet.aspect.LogTraceAspect;

@Configuration
public class AopConfig {

    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }

    @Bean
    public LogTraceAspect logTraceAspect(LogTrace logTrace) {
        return new LogTraceAspect(logTrace);
    }

}
