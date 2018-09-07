/**
 * 
 */
package com.inferyx.framework.demo;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;


/**
 * @author joy
 *
 */
@Component
public class DynamicSchedule implements Trigger {

	   private TaskScheduler scheduler;
	   private Runnable task;
	   private ScheduledFuture<?> future;
	   private int delay;
	   private Date nextExecutionTime;
	   
	public DynamicSchedule() {
		super();
	}

	public DynamicSchedule(TaskScheduler scheduler, Runnable task, int delay) {
	      this.scheduler = scheduler;
	      this.task = task;
	      this.delay = delay;
	   }

	public void setNextExecutionTime(Date nextExecutionTime) throws Exception {
	   System.out.println("Setting nextExecutionTime: " + nextExecutionTime);
	   this.nextExecutionTime = nextExecutionTime;
   }

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
	   System.out.println("Fetching nextExecutionTime: " + nextExecutionTime);
	   return null;
	}

	}