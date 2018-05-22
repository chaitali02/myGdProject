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
@Document(collection="operator")
public class Operator extends BaseEntity {
	private MetaIdentifierHolder paramList;
	private String operatorType;

	/**
	 * @Ganesh
	 *
	 * @return the operatorType
	 */
	public String getOperatorType() {
		return operatorType;
	}

	/**
	 * @Ganesh
	 *
	 * @param operatorType the operatorType to set
	 */
	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}

	/**
	 * @Ganesh
	 *
	 * @return the paramList
	 */
	public MetaIdentifierHolder getParamList() {
		return paramList;
	}

	/**
	 * @Ganesh
	 *
	 * @param paramList the paramList to set
	 */
	public void setParamList(MetaIdentifierHolder paramList) {
		this.paramList = paramList;
	}
	
}
