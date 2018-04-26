/**
 * 
 */
package com.inferyx.framework.operator;

import org.springframework.data.mongodb.core.mapping.Document;

import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.ParamList;

/**
 * @author Ganesh
 *
 */
@Document(collection="operatortype")
public class OperatorType extends BaseEntity {
	private ParamList paramList;

	/**
	 * @Ganesh
	 *
	 * @return the paramList
	 */
	public ParamList getParamList() {
		return paramList;
	}

	/**
	 * @Ganesh
	 *
	 * @param paramList the paramList to set
	 */
	public void setParamList(ParamList paramList) {
		this.paramList = paramList;
	}
	
}
