/**
 * 
 */
package com.inferyx.framework.service2;

import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain2.Events;
import com.inferyx.framework.domain2.Stages;

/**
 * @author joy
 *
 */
@Service
public class StateMachineBuilderServiceImpl {

	/**
	 * 
	 */
	public StateMachineBuilderServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	StateMachine<Stages, Events> build() throws Exception {
	    Builder<Stages, Events> builder = StateMachineBuilder.builder();
	    builder.configureConfiguration()
	        .withConfiguration()
	            .autoStartup(false)
	            .beanFactory(null)
	            .taskExecutor(new SyncTaskExecutor())
	            .taskScheduler(null)
	            .listener(new StateMachineListenerServiceImpl());
	    
	    builder.configureStates()
	    		.withStates()
	    		.initial(Stages.PENDING)
	    		.state(Stages.RUNNING)
	    		.state(Stages.COMPLETED)
	    		.state(Stages.FAILED)
	    		.state(Stages.TERMINATING)
	    		.state(Stages.KILLED)
	    		.state(Stages.PAUSE)
	    		.state(Stages.RESUME);
	    builder.configureTransitions()
	    		.withExternal().source(Stages.PENDING).event(Events.PROGRESS).target(Stages.RUNNING)
	    		.and()
	    		.withExternal().source(Stages.PENDING).event(Events.FAILED).target(Stages.FAILED)
	    		.and()
	    		.withExternal().source(Stages.PENDING).event(Events.TERMINATE).target(Stages.TERMINATING)
	    		.and()
	    		.withExternal().source(Stages.PENDING).event(Events.PAUSE).target(Stages.PAUSE)
	    		.and()
	    		.withExternal().source(Stages.RUNNING).event(Events.SUCCEED).target(Stages.COMPLETED)
	    		.and()
	    		.withExternal().source(Stages.RUNNING).event(Events.FAILED).target(Stages.FAILED)
	    		.and()
	    		.withExternal().source(Stages.RUNNING).event(Events.TERMINATE).target(Stages.TERMINATING)
	    		/*.and()
	    		.withExternal().source(Stages.FAILED).event(Events.RESTART).target(Stages.PENDING)*/
	    		.and()
	    		.withExternal().source(Stages.TERMINATING).event(Events.KILL).target(Stages.KILLED)
	    		/*.and()
	    		.withExternal().source(Stages.KILLED).event(Events.RESTART).target(Stages.PENDING)*/
	    		.and()
	    		.withExternal().source(Stages.PAUSE).event(Events.RESUME).target(Stages.RESUME)
	    		.and()
	    		.withExternal().source(Stages.RESUME).event(Events.RESUME_NS).target(Stages.PENDING);
	    return builder.build();
	}


}
