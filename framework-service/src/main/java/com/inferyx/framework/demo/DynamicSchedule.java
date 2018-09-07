/**
 * 
 */
package com.inferyx.framework.demo;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Batch;
import com.inferyx.framework.domain.BatchExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.BatchServiceImpl;


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
	   @Autowired
	   BatchServiceImpl batchServiceImpl;
	   
	public DynamicSchedule() {
		super();
	}

	public DynamicSchedule(TaskScheduler scheduler, Runnable task, int delay, BatchServiceImpl batchServiceImpl) {
	      this.scheduler = scheduler;
	      this.task = task;
	      reset(delay);
	      this.batchServiceImpl = batchServiceImpl;
	   }

	   public void reset(int delay) {
	      if (future != null) {
	         System.out.println("Cancelling task...");
	         future.cancel(true);
	      }
	      this.delay = delay;
	      System.out.println("Starting task...");
	      future = scheduler.schedule(task, this);
	   }

	   public void setNextExecutionTime(Date nextExecutionTime, List<Batch> batchs) throws Exception {
		   this.nextExecutionTime = nextExecutionTime;
		   if (future != null) {
		         System.out.println("Cancelling task...");
		         future.cancel(true);
		      }
//		      this.delay = 10000;
		      System.out.println("Starting task...");
		      future = scheduler.schedule(task, this);
		      for(Batch batch : batchs) {
		    	  BatchExec batchExec = batchServiceImpl.create(batch.getUuid(), batch.getVersion(), null, null, RunMode.BATCH);
		    	  batchServiceImpl.submitBatch(batch.getUuid(), batch.getVersion(), batchExec, null, null, RunMode.BATCH);
		      }
//		   future = scheduler.schedule(task, this);
	   }
	   
	   @Override
	   public Date nextExecutionTime(TriggerContext triggerContext) {
		  Date lastTime = triggerContext.lastActualExecutionTime();
		   if(lastTime != null)
			   future = scheduler.schedule(task, this);
//	      delay = 30000;
//	      batchServiceImpl.submitBatch(batchUuid, batchVersion, batchExec, execParams, type, runMode);
	      Date nextExecutionTime = this.nextExecutionTime;
	      /*nextExecutionTime = (lastTime == null)
	         ? new Date()
	         : new Date(lastTime.getTime() + delay);
	         System.out.println("DynamicSchedule -- delay: " + delay +
	              ", lastActualExecutionTime: " + lastTime +
	              "; nextExecutionTime: " + nextExecutionTime);*/
	      System.out.println("nextExecutionTime >>>>>>>>>>>>>>> "+nextExecutionTime);
	      return nextExecutionTime;
	   }

	}