/**
 * 
 */
package com.inferyx.framework.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.operator.CloneDataOperator;
import com.inferyx.framework.operator.GenerateDataForAttrRef;
import com.inferyx.framework.operator.GenerateDataForValList;
import com.inferyx.framework.operator.GenerateDataOperator;
import com.inferyx.framework.operator.Operator;
import com.inferyx.framework.operator.TransposeOperator;

/**
 * @author joy
 *
 */
@Service
public class CustomOperatorFactory implements IOperatorFactory {
	
	@Autowired
	GenerateDataOperator generateDataOperator;
	@Autowired
	TransposeOperator transposeOperator;
	@Autowired
	GenerateDataForAttrRef generateDataForAttrRef;
	@Autowired
	GenerateDataForValList generateDataForValList;
	@Autowired
	CloneDataOperator cloneDataOperator;

	/**
	 * 
	 */
	public CustomOperatorFactory() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param operatorTypeName
	 * @return
	 */
	public Operator getOperator (MetaType operatorType) {
		switch(operatorType) {
			case GenerateData : return generateDataOperator;
			case Transpose : return transposeOperator;
			case GenDataAttr : return generateDataForAttrRef;
			case GenDataValList : return generateDataForValList;
			case CloneData : return cloneDataOperator;
			default : throw new IllegalArgumentException("Invalid Operator Type");
		}
	}

}
