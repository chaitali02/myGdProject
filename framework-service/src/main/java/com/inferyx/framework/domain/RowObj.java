/**
 * 
 */
package com.inferyx.framework.domain;

/**
 * @author joy
 *
 */
public class RowObj {
	
	private Object[] rowData;
	
	/**
	 * 
	 */
	public RowObj() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param rowData
	 */
	public RowObj(Object[] rowData) {
		super();
		this.rowData = rowData;
	}



	/**
	 * @return the rowData
	 */
	public Object[] getRowData() {
		return rowData;
	}

}
