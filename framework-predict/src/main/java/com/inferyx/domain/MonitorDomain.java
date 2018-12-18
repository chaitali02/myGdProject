/**
 * 
 */
package com.inferyx.domain;

/**
 * @author joy
 *
 */
public class MonitorDomain {
	
	private float usedMem;
	private float freeMem;
	private float totMem;
	private float maxMem;
	private int availableProcessors;

	/**
	 * 
	 */
	public MonitorDomain() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the usedMem
	 */
	public float getUsedMem() {
		return usedMem;
	}

	/**
	 * @param usedMem the usedMem to set
	 */
	public void setUsedMem(float usedMem) {
		this.usedMem = usedMem;
	}

	/**
	 * @return the freeMem
	 */
	public float getFreeMem() {
		return freeMem;
	}

	/**
	 * @param freeMem the freeMem to set
	 */
	public void setFreeMem(float freeMem) {
		this.freeMem = freeMem;
	}

	/**
	 * @return the totMem
	 */
	public float getTotMem() {
		return totMem;
	}

	/**
	 * @param totMem the totMem to set
	 */
	public void setTotMem(float totMem) {
		this.totMem = totMem;
	}

	/**
	 * @return the maxMem
	 */
	public float getMaxMem() {
		return maxMem;
	}

	/**
	 * @param maxMem the maxMem to set
	 */
	public void setMaxMem(float maxMem) {
		this.maxMem = maxMem;
	}

	/**
	 * @return the availableProcessors
	 */
	public int getAvailableProcessors() {
		return availableProcessors;
	}

	/**
	 * @param availableProcessors the availableProcessors to set
	 */
	public void setAvailableProcessors(int availableProcessors) {
		this.availableProcessors = availableProcessors;
	}

}
