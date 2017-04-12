package com.jhw.adm.client.aop;

public class PointcutDefinition {

	public static final String ACTION_POINTCUT = 
		"execution(* com.jhw.adm.client.actions.*Action.actionPerformed(..)) && " +
		"@annotation(com.jhw.adm.client.aop.LoggingRequired)";
}