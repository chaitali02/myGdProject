package com.inferyx.framework.factory;

import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.operator.Operator;

public interface IOperatorFactory {
	
	public Operator getOperator (MetaType operatorType);

}
