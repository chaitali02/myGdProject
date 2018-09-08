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

	public DynamicSchedule(TaskScheduler scheduler, Runnable task) {
	      this.scheduler = scheduler;
	      this.task = task;
	   }

	public void setNextExecutionTime(Date nextExecutionTime) throws Exception {
	   System.out.println("Setting nextExecutionTime: " + nextExecutionTime);
	   this.nextExecutionTime = nextExecutionTime;
      if (future != null) {
         System.out.println("Cancelling trigger task...");
         future.cancel(true);
      }
      System.out.println("Starting trigger task...");
      future = scheduler.schedule(task, this);
   }

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
	   System.out.println("Current nextExecutionTime: " + this.nextExecutionTime);
	   return this.nextExecutionTime;
	}

	}