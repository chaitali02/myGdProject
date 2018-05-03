/**
 * 
 */
package com.inferyx.framework.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.operator.JoinTablesOperator;
import com.inferyx.framework.operator.Operator;

/**
 * @author joy
 *
 */
@Service
public class OperatorFactory {
	
	@Autowired
	JoinTablesOperator joinTablesOperator;

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
			default : return joinTablesOperator;
		}
	}

}
