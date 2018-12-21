/**
 * 
 */
package com.inferyx.domain;

import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.TrainExec;

/**
 * @author joy
 *
 */
public class ModelTrainDomain {
	
	private Model model;
	private TrainExec trainExec;

	/**
	 * 
	 */
	public ModelTrainDomain() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param model
	 * @param train
	 */
	public ModelTrainDomain(Model model, TrainExec trainExec) {
		super();
		this.model = model;
		this.trainExec = trainExec;
	}



	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @return the trainExec
	 */
	public TrainExec getTrainExec() {
		return trainExec;
	}

	/**
	 * @param train the trainExec to set
	 */
	public void setTrainExec(TrainExec trainExec) {
		this.trainExec = trainExec;
	}
	
}
