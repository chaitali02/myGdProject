/**
 * 
 */
package com.inferyx.framework.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.operator.GenerateDistributionData;
import com.inferyx.framework.operator.JoinTablesOperator;
import com.inferyx.framework.operator.Operator;
import com.inferyx.framework.operator.SpecialTransposeOperator;

/**
 * @author joy
 *
 */
@Service
public class OperatorFactory {
	
	@Autowired
	JoinTablesOperator joinTablesOperator;
	@Autowired
	GenerateDistributionData generateDistributionData;
	@Autowired
	SpecialTransposeOperator specialTransposeOperator;

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
			case "JOIN_TABLES" : return joinTablesOperator;
			case "GENERATE_DIST_DATA" : return generateDistributionData;
			case "operator_transpose" : return specialTransposeOperator;
			default : return joinTablesOperator;
		}
	}

}
