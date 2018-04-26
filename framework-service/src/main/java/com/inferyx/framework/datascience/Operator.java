/**
 * 
 */
package com.inferyx.framework.datascience;

import org.springframework.data.mongodb.core.mapping.Document;

import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.MetaIdentifierHolder;

/**
 * @author Ganesh
 *
 */
@Document(collection = "operator")
public class Operator extends BaseEntity {
	MetaIdentifierHolder operatorType;

	/**
	 * @Ganesh
	 *
	 * @return the operatorType
	 */
	public MetaIdentifierHolder getOperatorType() {
		return operatorType;
	}

	/**
	 * @Ganesh
	 *
	 * @param operatorType the operatorType to set
	 */
	public void setOperatorType(MetaIdentifierHolder operatorType) {
		this.operatorType = operatorType;
	}
	
}
