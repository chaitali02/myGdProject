/**
 * 
 */
package com.inferyx.framework.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author joy
 *
 */
@Service
public class OperatorAbstractFactory {
	
	@Autowired
	CustomOperatorFactory customOperatorFactory;
	@Autowired
	SystemOperatorFactory systemOperatorFactory;

	/**
	 * 
	 */
	public OperatorAbstractFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public IOperatorFactory getOperatorFactory (String operatorFactoryTypeName) {
		switch(operatorFactoryTypeName) {
		case "system" : return systemOperatorFactory;
		case "custom" : return customOperatorFactory;
		default : throw new IllegalArgumentException("Invalid Operator Factory Type");
		}
	}

}
