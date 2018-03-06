package com.inferyx.framework.domain;

public class SectionView {
	
	private String sectionId;
	private String name;
	private Vizpod vizpodInfo;
	private Integer rowNo;
    private Integer colNo;
	 
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Vizpod getVizpodInfo() {
		return vizpodInfo;
	}
	public void setVizpodInfo(Vizpod vizpodInfo) {
		this.vizpodInfo = vizpodInfo;
	}
	public Integer getRowNo() {
		return rowNo;
	}
	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
	}
	public Integer getColNo() {
		return colNo;
	}
	public void setColNo(Integer colNo) {
		this.colNo = colNo;
	}
	 
}
