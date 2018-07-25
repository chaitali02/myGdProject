/**
 * 
 */
package com.inferyx.framework.service2;

import org.apache.log4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import com.inferyx.framework.domain2.Events;
import com.inferyx.framework.domain2.Stages;

/**
 * @author joy
 *
 */
public class StateMachineListenerServiceImpl implements StateMachineListener<Stages, Events> {
	
	static final Logger logger = Logger.getLogger(StateMachineListenerServiceImpl.class);

	/**
	 * 
	 */
	public StateMachineListenerServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void eventNotAccepted(Message<Events> event) {
		logger.info("Event not accepted : " + event.getPayload());
	}

	@Override
	public void extendedStateChanged(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChanged(State<Stages, Events> from, State<Stages, Events> to) {
		logger.info("State changed to  : " + to.getId());
		
	}

	@Override
	public void stateContext(StateContext<Stages, Events> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateEntered(State<Stages, Events> state) {
		logger.info("Entered State : " + state.getId());
		
	}

	@Override
	public void stateExited(State<Stages, Events> state) {
		logger.info("Exited State : " + state.getId());
	}

	@Override
	public void stateMachineError(StateMachine<Stages, Events> stateMachine, Exception exception) {
		exception.printStackTrace();
	}

	@Override
	public void stateMachineStarted(StateMachine<Stages, Events> stateMachine) {
		logger.info("State Machine start : " + stateMachine.getId());
		stateMachine.getStates().forEach(t -> logger.info("State : " + t.getId()));
		stateMachine.getTransitions().forEach(t -> logger.info("Transition : " + t.getSource().getId() + ":" + t.getTarget().getId() + ":" + t.getTrigger().getEvent().name()));
	}

	@Override
	public void stateMachineStopped(StateMachine<Stages, Events> stateMachine) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transition(Transition<Stages, Events> stateMachine) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transitionEnded(Transition<Stages, Events> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transitionStarted(Transition<Stages, Events> arg0) {
		// TODO Auto-generated method stub
		
	}

}
