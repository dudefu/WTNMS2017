package com.jhw.adm.client.aop;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.jhw.adm.client.actions.DelegateAction;
import com.jhw.adm.client.actions.ShowViewAction;

@Component
@Aspect
public class ProfilingAspect {

//	@Pointcut(PointcutDefinition.ACTION_POINTCUT)
    public void actionPointcut() { }

//	@Around("actionPointcut()")
	public final Object profile(final ProceedingJoinPoint call)
			throws Throwable {
		StopWatch clock = new StopWatch(String.format("Profiling for %s", call
				.getTarget().getClass().getSimpleName()));
		
		if (call.getTarget() instanceof DelegateAction) {
			DelegateAction delegateAction = (DelegateAction)call.getTarget();
			String description = delegateAction.getDescription();
			
			if (StringUtils.isBlank(description)) {
				description = String.format("Method[%s] invoked.", delegateAction.getActionMethod().getName());
			}
			LOG.info("Invoke method: {}", description);
		}
		if (call.getTarget() instanceof ShowViewAction) {
			ShowViewAction showViewAction = (ShowViewAction)call.getTarget();
			String description = showViewAction.getViewId();
			description = String.format("View[%s] opened.", description);
			LOG.info(description);
		}
		try {
			clock.start(call.getTarget().getClass().getSimpleName() + "."
					+ call.toShortString());
			return call.proceed();
		} finally {
			clock.stop();
			System.out.println(clock.prettyPrint());
		}
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(ProfilingAspect.class);
}