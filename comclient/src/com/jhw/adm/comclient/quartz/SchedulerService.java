package com.jhw.adm.comclient.quartz;

import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

public class SchedulerService {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	private Scheduler quartzScheduler;
	private JobDetail jobDetail;

	public void schedule(Date startTime) {
		schedule(startTime, Scheduler.DEFAULT_GROUP);
	}

	public void schedule(Date startTime, String group) {
		schedule(startTime, null, group);
	}

	public void schedule(String name, Date startTime) {
		schedule(name, startTime, Scheduler.DEFAULT_GROUP);
	}

	public void schedule(String name, Date startTime, String group) {
		schedule(name, startTime, null, group);
	}

	public void schedule(Date startTime, Date endTime) {
		schedule(startTime, endTime, Scheduler.DEFAULT_GROUP);
	}

	public void schedule(Date startTime, Date endTime, String group) {
		schedule(startTime, endTime, 0, group);
	}

	public void schedule(String name, Date startTime, Date endTime) {
		schedule(name, startTime, endTime, Scheduler.DEFAULT_GROUP);
	}

	public void schedule(String name, Date startTime, Date endTime, String group) {
		schedule(name, startTime, endTime, 0, group);
	}

	public void schedule(Date startTime, Date endTime, int repeatCount) {
		schedule(startTime, endTime, 0, Scheduler.DEFAULT_GROUP);
	}

	public void schedule(Date startTime, Date endTime, int repeatCount,
			String group) {
		schedule(null, startTime, endTime, 0, group);
	}

	public void schedule(String name, Date startTime, Date endTime,
			int repeatCount) {
		schedule(name, startTime, endTime, 0, Scheduler.DEFAULT_GROUP);
	}

	public void schedule(String name, Date startTime, Date endTime,
			int repeatCount, String group) {
		schedule(name, startTime, endTime, 0, 1L, group);
	}

	public void schedule(Date startTime, Date endTime, int repeatCount,
			long repeatInterval) {
		schedule(startTime, endTime, repeatCount, repeatInterval,
				Scheduler.DEFAULT_GROUP);
	}

	public void schedule(Date startTime, Date endTime, int repeatCount,
			long repeatInterval, String group) {
		schedule(null, startTime, endTime, repeatCount, repeatInterval, group);
	}

	public void schedule(String name, Date startTime, Date endTime,
			int repeatCount, long repeatInterval) {
		this.schedule(name, startTime, endTime, repeatCount, repeatInterval,
				Scheduler.DEFAULT_GROUP);
	}

	public void schedule(String name, Date startTime, Date endTime,
			int repeatCount, long repeatInterval, String group) {
		if (name == null || name.trim().equals("")) {
			name = UUID.randomUUID().toString();
		} else {
			// 在名称后添加UUID，保证名称的唯一
			name += "&" + UUID.randomUUID().toString();
		}

		try {
			quartzScheduler.addJob(jobDetail, true);

			SimpleTrigger SimpleTrigger = new SimpleTrigger(name, group,
					jobDetail.getName(), Scheduler.DEFAULT_GROUP, startTime,
					endTime, repeatCount, repeatInterval);
			quartzScheduler.scheduleJob(SimpleTrigger);
			quartzScheduler.rescheduleJob(SimpleTrigger.getName(),
					SimpleTrigger.getGroup(), SimpleTrigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void resumeTrigger(String triggerName, String group) {
		try {
			// Trigger trigger = scheduler.getTrigger(triggerName, group);

			quartzScheduler.resumeTrigger(triggerName, group);// 重启触发
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean removeTrigdger(String triggerName, String group) {
		try {
			quartzScheduler.pauseTrigger(triggerName, group);// 停止触发
			return quartzScheduler.unscheduleJob(triggerName, group);// 移除触发
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

}
