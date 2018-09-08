/**
 * 
 */
package com.inferyx.framework.service;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;


/**
 * @author joy
 *
 */
@Component
public class BatchTriggerServiceImpl implements Trigger {

	   private TaskScheduler scheduler;
	   private Runnable task;
	   private ScheduledFuture<?> future;
//	   private int delay;
	   private Date nextExecutionTime;
	
	   static Logger logger = Logger.getLogger(BatchTriggerServiceImpl.class);
	   
	public BatchTriggerServiceImpl() {
		super();
	}

	public BatchTriggerServiceImpl(TaskScheduler scheduler, Runnable task) {
	      this.scheduler = scheduler;
	      this.task = task;
	   }

	public void setNextExecutionTime(Date nextExecutionTime) throws Exception {
		logger.info("Setting nextExecutionTime: " + nextExecutionTime);
	   this.nextExecutionTime = nextExecutionTime;
      if (future != null) {
    	  logger.info("Cancelling trigger task...");
         future.cancel(true);
      }
      if (nextExecutionTime != null) {
    	  logger.info("Starting trigger task...");
    	  future = scheduler.schedule(task, this);
      }
   }

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		logger.info("Current nextExecutionTime: " + this.nextExecutionTime);
	   return this.nextExecutionTime;
	}

	}