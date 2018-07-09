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
	    		.initial(Stages.NotStarted)
	    		.state(Stages.InProgress)
	    		.state(Stages.Completed)
	    		.state(Stages.Failed)
	    		.state(Stages.Terminating)
	    		.state(Stages.Killed)
	    		.state(Stages.OnHold)
	    		.state(Stages.Resume);
	    builder.configureTransitions()
	    		.withExternal().source(Stages.NotStarted).event(Events.PROGRESS).target(Stages.InProgress)
	    		.and()
	    		.withExternal().source(Stages.NotStarted).event(Events.FAILED).target(Stages.Failed)
	    		.and()
	    		.withExternal().source(Stages.NotStarted).event(Events.TERMINATE).target(Stages.Terminating)
	    		.and()
	    		.withExternal().source(Stages.NotStarted).event(Events.ONHOLD).target(Stages.OnHold)
	    		.and()
	    		.withExternal().source(Stages.InProgress).event(Events.SUCCEED).target(Stages.Completed)
	    		.and()
	    		.withExternal().source(Stages.InProgress).event(Events.FAILED).target(Stages.Failed)
	    		.and()
	    		.withExternal().source(Stages.InProgress).event(Events.TERMINATE).target(Stages.Terminating)
	    		/*.and()
	    		.withExternal().source(Stages.Failed).event(Events.RESTART).target(Stages.NotStarted)*/
	    		.and()
	    		.withExternal().source(Stages.Terminating).event(Events.KILL).target(Stages.Killed)
	    		/*.and()
	    		.withExternal().source(Stages.Killed).event(Events.RESTART).target(Stages.NotStarted)*/
	    		.and()
	    		.withExternal().source(Stages.OnHold).event(Events.RESUME).target(Stages.Resume)
	    		.and()
	    		.withExternal().source(Stages.Resume).event(Events.RESUME_NS).target(Stages.NotStarted);
	    return builder.build();
	}


}
