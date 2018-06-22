/**
 * 
 */
package com.inferyx.framework.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inferyx.framework.enums.OperatorType;
import com.inferyx.framework.operator.IOperator;
import com.inferyx.framework.operator.RuleOperator;

/**
 * @author joy
 *
 */
@Service
public class SystemOperatorFactory implements IOperatorFactory {
	
	@Autowired
	RuleOperator ruleOperator;
	

	/**
	 * 
	 */
	public SystemOperatorFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IOperator getOperator(OperatorType operatorType) {
		switch(operatorType) {
		case rule: return ruleOperator;
		default: throw new IllegalArgumentException("Invalid Operator Type");
		}
	}

}
