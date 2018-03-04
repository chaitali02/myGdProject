package com.inferyx.framework.domain;

public class AttributeSource {
	
	private String attrSourceId;	
	private String attrSourceName;
	private AttributeRefHolder sourceAttr;
	
	public String getAttrSourceId() {
		return attrSourceId;
	}
	public void setAttrSourceId(String AttrSourceId) {
		this.attrSourceId = AttrSourceId;
	}
	public AttributeRefHolder getSourceAttr() {
		return sourceAttr;
	}
	public void setSourceAttr(AttributeRefHolder sourceAttr) {
		this.sourceAttr = sourceAttr;
	}	
	public String getAttrSourceName() {
		return attrSourceName;
	}
	public void setAttrSourceName(String attrSourceName) {
		this.attrSourceName = attrSourceName;
	}
	
	
	

}
