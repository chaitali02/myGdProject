package com.inferyx.framework.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="dataset")
public class DataSet extends BaseEntity{
	private MetaIdentifierHolder dependsOn;
	private List<AttributeRefHolder> filterInfo;	
	private List<AttributeSource> attributeInfo = new ArrayList<AttributeSource>();		
	private MetaIdentifierHolder groupBy;
	public List<AttributeRefHolder> getFilterInfo() {
		return filterInfo;
	}
	public void setFilterInfo(List<AttributeRefHolder> filterInfo) {
		this.filterInfo = filterInfo;
	}
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}	
	public MetaIdentifierHolder getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(MetaIdentifierHolder groupBy) {
		this.groupBy = groupBy;
	}
	public String sql(String attrName) {
		return String.format("%s.%s", this.getName(), attrName);
	}	
	public List<AttributeSource> getAttributeInfo() {
		return attributeInfo;
	}
	public void setAttributeInfo(List<AttributeSource> attributeInfo) {
		this.attributeInfo = attributeInfo;
	}

	public String getAttributeName(Integer attributeId) {
		List<AttributeSource> sourceAttrs = getAttributeInfo();
		for (AttributeSource sourceAttr : sourceAttrs) {
			if (sourceAttr.getSourceAttr() != null 
					&& sourceAttr.getAttrSourceId() != null 
					&& sourceAttr.getAttrSourceId().equals(attributeId.toString())) {
				return sourceAttr.getAttrSourceName();
			}
		}
		return null;
	}
}
