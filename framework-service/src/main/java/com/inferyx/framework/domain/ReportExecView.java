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


public class ReportExecView extends BaseExec {

	private long numRows;
	private double sizeMB;
	private String saveOnRefresh;
	private SenderInfo senderInfo;
	private String format;


	public long getNumRows() {
		return numRows;
	}

	public void setNumRows(long numRows) {
		this.numRows = numRows;
	}

	public double getSizeMB() {
		return sizeMB;
	}

	public void setSizeMB(double sizeMB) {
		this.sizeMB = sizeMB;
	}

	public String getSaveOnRefresh() {
		return saveOnRefresh;
	}

	public void setSaveOnRefresh(String saveOnRefresh) {
		this.saveOnRefresh = saveOnRefresh;
	}

	public SenderInfo getSenderInfo() {
		return senderInfo;
	}

	public void setSenderInfo(SenderInfo senderInfo) {
		this.senderInfo = senderInfo;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
