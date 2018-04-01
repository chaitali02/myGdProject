package com.inferyx.framework.test;

public class Instrument {
	protected Double minValue;
	protected Double maxValue;
	protected Double[] factorWeights;

	/**
	 *
	 * @Author Ganesh
	 *
	 * @param factorWeights
	 * @param minValue
	 * @param maxValue
	 */
	public Instrument(Double minValue, Double maxValue, Double[] factorWeights) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.factorWeights = factorWeights;
	}

	/**
	 * @Ganesh
	 *
	 * @return the minValue
	 */
	public Double getMinValue() {
		return minValue;
	}

	/**
	 * @Ganesh
	 *
	 * @param minValue
	 *            the minValue to set
	 */
	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	/**
	 * @Ganesh
	 *
	 * @return the maxValue
	 */
	public Double getMaxValue() {
		return maxValue;
	}

	/**
	 * @Ganesh
	 *
	 * @param maxValue
	 *            the maxValue to set
	 */
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * @Ganesh
	 *
	 * @return the factorWeights
	 */
	public Double[] getFactorWeights() {
		return factorWeights;
	}

	/**
	 * @Ganesh
	 *
	 * @param factorWeights
	 *            the factorWeights to set
	 */
	public void setFactorWeights(Double[] factorWeights) {
		this.factorWeights = factorWeights;
	}

}
