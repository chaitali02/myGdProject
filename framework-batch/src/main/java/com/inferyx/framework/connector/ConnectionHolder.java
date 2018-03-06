package com.inferyx.framework.connector;

import java.sql.DatabaseMetaData;

public class ConnectionHolder {
	
	private String type;
	private Object conObject;
	private Object stmtObject;
	
	public Object getStmtObject() {
		return stmtObject;
	}
	public void setStmtObject(Object stmtObject) {
		this.stmtObject = stmtObject;
	}
	public Object getConObject() {
		return conObject;
	}
	public void setConObject(Object conObject) {
		this.conObject = conObject;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
