/**
 * 
 */
package com.inferyx.framework.factory2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.operator.IOperator;
import com.inferyx.framework.service2.DataQualExecOperator2;
import com.inferyx.framework.service2.MapExecOperator2;

/**
 * @author joy
 *
 */
@Service
public class SystemOperatorFactory2 {
	
	@Autowired
	MapExecOperator2 mapExecOperator2;
	@Autowired
	DataQualExecOperator2 dataQualExecOperator2;

	/**
	 * 
	 */
	public SystemOperatorFactory2() {
		// TODO Auto-generated constructor stub
	}
	
	public IOperator getOperator(MetaType operatorType) throws Exception {
		switch (operatorType) {
		case map: return mapExecOperator2;
		case dq: return dataQualExecOperator2;
		default: throw new Exception("Invalid choice of Operator"); 
		}
	}

}
