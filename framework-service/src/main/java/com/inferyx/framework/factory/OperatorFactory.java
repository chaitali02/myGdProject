/**
 * 
 */
package com.inferyx.framework.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.operator.GenerateDataOperator;
import com.inferyx.framework.operator.JoinTablesOperator;
import com.inferyx.framework.operator.Operator;
import com.inferyx.framework.operator.TransposeOperator;

import scala.util.control.Exception;

/**
 * @author joy
 *
 */
@Service
public class OperatorFactory {
	
	@Autowired
	GenerateDataOperator generateDataOperator;
	@Autowired
	TransposeOperator transposeOperator;

	/**
	 * 
	 */
	public OperatorFactory() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param operatorTypeName
	 * @return
	 */
	public Operator getOperator (String operatorTypeName) {
		switch(operatorTypeName) {
			case "Generate Data" : return generateDataOperator;
			case "Transpose" : return transposeOperator;
			default : throw new IllegalArgumentException("Invalid Operator Type");
		}
	}

}
