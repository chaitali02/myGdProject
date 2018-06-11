package com.inferyx.framework.factory;

import com.inferyx.framework.enums.OperatorType;
import com.inferyx.framework.operator.Operator;

public interface IOperatorFactory {
	
	public Operator getOperator (OperatorType operatorType);

}
