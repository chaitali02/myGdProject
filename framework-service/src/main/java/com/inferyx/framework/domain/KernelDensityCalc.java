package com.inferyx.framework.domain;

import java.io.Serializable;

public class KernelDensityCalc implements Serializable {

	public KernelDensityCalc(Double i, Double j) {
		total=i;
		num=j;
	}
	
	public Double total;
	public Double num;
		
}
