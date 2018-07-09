package com.inferyx.framework.factory;

import com.inferyx.framework.enums.OperatorType;
import com.inferyx.framework.operator.IOperator;

public interface IOperatorFactory {
	
	public IOperator getOperator (OperatorType operatorType);

}
