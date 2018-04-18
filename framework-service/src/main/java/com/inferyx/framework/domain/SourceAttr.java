/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

public class SourceAttr {
	private MetaIdentifier ref;
	
	private Integer attributeId;
	
/*	private MetaIdentifierHolder condition;
*/	
	private String value;
	private String attributeName;

	
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	/*public MetaIdentifierHolder getCondition() {
		return condition;
	}
	public void setCondition(MetaIdentifierHolder condition) {
		this.condition = condition;
	}*/
		
	public Integer getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(Integer attributeId) {
		this.attributeId = attributeId;
	}
	public MetaIdentifier getRef() {
		return ref;
	}
	public void setMeta(MetaIdentifier ref) {
		this.ref = ref;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setRef(MetaIdentifier ref) {
		this.ref = ref;
	}
	
	/*public String sql(MetaIdentifierUtil commonActivity) {
		StringBuilder builder = new StringBuilder();
		if (getRef().getType() == MetaType.datapod) {
			// Bhanu - JIRA FW-3 - Remove cache
			// Datapod datapod = (Datapod) getRef().getCollectionObject(loader);
			Datapod datapod = (Datapod) commonActivity.getRefObject(getRef());
			// End JIRA FW-3 Changes
			return builder.append(datapod.sql(getAttributeId())).append(" ").toString();
		}
		
		// Bhanu - JIRA FW-3 - Remove cache
		// Object object = getRef().getCollectionObject(loader);
		Object object = commonActivity.getRefObject(getRef());
		// End JIRA FW-3 Changes
		
		if(object instanceof Expression) {
			if (getCondition().getRef() == null) {
				
				return builder.append(((Expression)object).sql(commonActivity)).append(" ").toString();
			}
			// Bhanu - JIRA FW-3 - Remove cache
			// Condition condition = (Condition) getCondition().getRef().getCollectionObject(loader);
			Condition condition = (Condition) commonActivity.getRefObject(getCondition().getRef());
			// End JIRA FW-3 Changes
					
			builder.append(" when ").append(condition.sql(commonActivity)).append(" then ").append(((Expression)object).sql(commonActivity)).append(" ");
		}

		if(object instanceof Formula) {
			if (getCondition().getRef() == null) {
				return ((Formula)object).sql(commonActivity);
			}
			// Bhanu - JIRA FW-3 - Remove cache
			// Condition condition = (Condition) getCondition().getRef().getCollectionObject(loader);
			Condition condition = (Condition) commonActivity.getRefObject(getCondition().getRef());
			// End JIRA FW-3 Changes
			builder.append(" when ").append(condition.sql(commonActivity)).append(" then ").append(((Formula)object).sql(commonActivity)).append(" ");
		}
		
		return builder.toString();
	}*/

}
