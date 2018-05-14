package com.inferyx.framework.enums;

public enum ParamDataType {
	
	TWODARRAY("[][]"),
	ONEDARRAY("[]"),
	STRING("String"),
	DOUBLE("double"),
	INTEGER("int"),
	DATE("Date"),
	LONG("long"),
	ATTRIBUTE("[]"),
	ATTRIBUTES("[]"),
	DISTRIBUTION("distribution"),
	DATAPOD("datapod");
	
	String value = "";
	private ParamDataType(String value) {
		this.value = value;
	}
	
	public String getVal() {
		return value;
	}
}
