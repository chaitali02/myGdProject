/**
 * 
 */
package com.inferyx.framework.service;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Ganesh
 *
 */
@Service
public class SchedulerService implements Tasklet {
	@Autowired
	BatchServiceImpl batchServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static Logger logger = Logger.getLogger(SchedulerService.class);
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		return RepeatStatus.FINISHED;
	}

}
