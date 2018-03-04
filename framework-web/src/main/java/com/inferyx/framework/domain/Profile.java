package com.inferyx.framework.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="profile")
public class Profile extends BaseRule {
	
	private MetaIdentifierHolder dependsOn;
	private List<AttributeRefHolder> attributeInfo;
	
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
	public List<AttributeRefHolder> getAttributeInfo() {
		return attributeInfo;
	}
	public void setAttributeInfo(List<AttributeRefHolder> attributeInfo) {
		this.attributeInfo = attributeInfo;
	}	

}
