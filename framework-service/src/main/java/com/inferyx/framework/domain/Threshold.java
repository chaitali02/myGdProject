package com.inferyx.framework.domain;

import com.inferyx.framework.enums.ThresholdType;

public class Threshold {
	
	private String low = "25";
	private String medium = "50";
	private String high = "75";
	private ThresholdType type = ThresholdType.NUMERIC;
	
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getMedium() {
		return medium;
	}
	public void setMedium(String medium) {
		this.medium = medium;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public ThresholdType getType() {
		return type;
	}
	public void setType(ThresholdType type) {
		this.type = type;
	}

}
