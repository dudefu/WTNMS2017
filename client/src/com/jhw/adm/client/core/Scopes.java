package com.jhw.adm.client.core;

/**
 * Spring Bean的生命周期定义
 */
public final class Scopes {
	/**
	 * 单例
	 */	
	public static final String SINGLETON = "singleton";
	/**
	 * 原型
	 */	
	public static final String PROTOTYPE = "prototype";
	/**
	 * 桌面，使用在ViewPart上，ViewPart关闭后Bean生命周期结束
	 */	
	public static final String DESKTOP = "desktop";
}
