package com.jhw.adm.client.aop;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.DelegateAction;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.server.entity.util.LogEntity;

@Component
@Aspect
public class LoggingAspect {

	@Pointcut(PointcutDefinition.ACTION_POINTCUT)
    public void actionPointcut() { }

	@Before("actionPointcut()")
	public void beforeExecution(JoinPoint joinpoint) {
		if (joinpoint.getTarget() instanceof DelegateAction) {
			DelegateAction delegateAction = (DelegateAction)joinpoint.getTarget();
			String description = delegateAction.getDescription();
			
			if (StringUtils.isBlank(description)) {
				description = String.format("调用[%s]方法", delegateAction.getActionMethod().getName());
			}
			LOG.info(description);
			LogEntity logEntity = new LogEntity();
			logEntity.setUserName(clientModel.getCurrentUser().getUserName());
			logEntity.setClientIp(clientModel.getLocalAddress());
			logEntity.setDoTime(new Date());
			logEntity.setContents(description);
			try {
				remoteServer.getService().saveEntity(logEntity);
			} catch (Exception ex) {
				LOG.error("保存操作日志出错", ex);
			}
		}
	}
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);
}