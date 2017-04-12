package com.jhw.adm.client.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class ThreadUtils {

	public static ThreadFactory createThreadFactory(final String factoryName) {
		ThreadFactory threadFactory = 
            new ThreadFactory() {
                final ThreadFactory defaultFactory = 
                    Executors.defaultThreadFactory();
                public Thread newThread(final Runnable r) {
                    Thread thread = defaultFactory.newThread(r);
                    thread.setName(String.format("%s-%s", factoryName, thread.getName()));
                    thread.setDaemon(true);
                    return thread;
                }
            };
            
        return threadFactory;
	}
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// 忽略线程中断异常
		}
	}
}