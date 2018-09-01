/**
 * 
 */
package com.inferyx.framework.common;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inferyx.framework.service.SchedulerService;

/**
 * @author Ganesh
 *
 */
@Configuration
@EnableBatchProcessing
public class BatchUtil {
	
	@Autowired
    private JobBuilderFactory jobs; 
    @Autowired
    private StepBuilderFactory steps;
    @Autowired
    private SchedulerService schedulerService;
    
    static Logger logger = Logger.getLogger(BatchUtil.class);
     
    @Bean
    public Step schedule(){
        return steps.get("stepOne")
                .tasklet(schedulerService)
                .build();
    } 
     
    @Bean
    public Job executeJob(){
        return jobs.get("demoJob")
                .incrementer(new RunIdIncrementer())
                .start(schedule())
                .build();
    }
}
