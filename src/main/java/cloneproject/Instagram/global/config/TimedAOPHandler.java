package cloneproject.Instagram.global.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@Profile({"local", "dev"})
public class TimedAOPHandler {

	@Around("@annotation(cloneproject.Instagram.global.annotation.Timed)")
	public Object calculateTime(ProceedingJoinPoint pjp) throws Throwable {
		final long startTime = System.nanoTime();
		final Object result = pjp.proceed();
		final long endTime = System.nanoTime();
		final long takenTime = endTime - startTime;
		log.info("@Timed Method '{}' takes {}ns.", pjp.getSignature().getDeclaringTypeName() , takenTime);
		return result;
	}

}
