package com.inferyx.framework.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="dashboard")
public class Dashboard extends BaseEntity {

	private List<Section> sectionInfo;
	
	private MetaIdentifierHolder dependsOn;
	private List<AttributeRefHolder> filterInfo;

	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	public List<AttributeRefHolder> getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(List<AttributeRefHolder> filterInfo) {
		this.filterInfo = filterInfo;
	}

	public List<Section> getSectionInfo() {
		return sectionInfo;
	}

	public void setSectionInfo(List<Section> sectionInfo) {
		this.sectionInfo = sectionInfo;
	}

	@Override
	public String toString() {
		return "Dashboard [filterInfo=" + filterInfo + "]";
	}
	
}