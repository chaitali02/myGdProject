/**
 * 
 */
package com.inferyx.framework.datascience.distribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author joy
 *
 */
@Service
public class RandomDistributionFactory {
	
	@Autowired
	private Math3RandDistribution math3RandDistribution;
	@Autowired
	private SparkMLRandDistribution sparkMLRandDistribution;

	/**
	 * 
	 */
	public RandomDistributionFactory() {
		// TODO Auto-generated constructor stub
	}

	public RandomDistribution getRandomDistribution(String distroLibraries) throws Exception {
		switch(distroLibraries) {
			case "MATH3" : return math3RandDistribution; 
			case "SPARKML" : return sparkMLRandDistribution; 
			default: throw new Exception ("Invalid distribution. Please choose a valid distribution to proceed. ");
		}
	}
	
}
