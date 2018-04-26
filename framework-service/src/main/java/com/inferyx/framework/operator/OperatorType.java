/**
 * 
 */
package com.inferyx.framework.operator;

import org.springframework.data.mongodb.core.mapping.Document;

import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.ParamList;

/**
 * @author Ganesh
 *
 */
@Document(collection="operatortype")
public class OperatorType extends BaseEntity {
	private MetaIdentifierHolder paramList;

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
