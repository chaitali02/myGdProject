/**
 * 
 */
package com.inferyx.framework.datascience;


import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Simulate;

/**
 * @author Ganesh
 *
 */
@Service
public class MonteCarloSimulation {
	
	
	/*************************Unused*****************************/
	/*public Double[] trialValues(long seed, int numIterations, Row[] dataset, double[] factorMeans,
			double[][] factorCovariances) {
		RandomGenerator rand = new MersenneTwister(seed);
		Double[] trialValues = new Double[] { (double) numIterations };
		MultivariateNormalDistribution multivariateNormal = new MultivariateNormalDistribution(rand, factorMeans,
				factorCovariances);

		for (int i = 0; i <= numIterations; i++) {
			double[] trial = multivariateNormal.sample();
			trialValues[i] = trialValue(trial, dataset);
		}

		return trialValues;
	}*/

	
	
	/*************************Unused**********************/
	/*public Double trialValue(double[] trial, Row[] datasets) {
		Double totalValue = 0.0;
		for (Row dataset : datasets) {
			totalValue += datasetTrialValue(dataset, trial);
		}
		return totalValue;
	}*/

	
	/****************************Unused***************************/
	/*public Double datasetTrialValue(Row dataset, double[] trial) {
		Double trialValue = 0.0;
		int i = 0;
		while (i < trial.length) {
			//trialValue += trial[i] * (Double)dataset.factorWeights[i];
			i += 1;
		}
		return null;//Math.min(Math.max(trialValue, dataset.minValue), dataset.maxValue);
	}
	*/
	public Object simulateMonteCarlo(Simulate simulate, ExecParams simExecParam, ExecParams distExecParam, String filePathUrl) {
		
		return null;
	}
}
