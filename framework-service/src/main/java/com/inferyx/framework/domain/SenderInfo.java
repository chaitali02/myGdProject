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

import java.util.List;
import java.util.Map;

/**
 * @author Ganesh
 *
 */
public class SenderInfo {
	private List<String> emailTo;
	private List<String> emailBCC;
	private List<String> emailCC;
	private Map<String, String> emailAttachment;
	private String notifyOnSuccess = "Y";
	private String notifyOnFailure = "Y";
	private String sendAttachment = "Y";
	
	/**
	 * @Ganesh
	 *
	 * @return the notifyOnSuccess
	 */
	public String getNotifyOnSuccess() {
		return notifyOnSuccess;
	}
	/**
	 * @Ganesh
	 *
	 * @param notifyOnSuccess the notifyOnSuccess to set
	 */
	public void setNotifyOnSuccess(String notifyOnSuccess) {
		this.notifyOnSuccess = notifyOnSuccess;
	}
	/**
	 * @Ganesh
	 *
	 * @return the notifyOnFailure
	 */
	public String getNotifyOnFailure() {
		return notifyOnFailure;
	}
	/**
	 * @Ganesh
	 *
	 * @param notifyOnFailure the notifyOnFailure to set
	 */
	public void setNotifyOnFailure(String notifyOnFailure) {
		this.notifyOnFailure = notifyOnFailure;
	}
	/**
	 * @Ganesh
	 *
	 * @return the sendAttachment
	 */
	public String getSendAttachment() {
		return sendAttachment;
	}
	/**
	 * @Ganesh
	 *
	 * @param sendAttachment the sendAttachment to set
	 */
	public void setSendAttachment(String sendAttachment) {
		this.sendAttachment = sendAttachment;
	}
	/**
	 * @Ganesh
	 *
	 * @return the emailTo
	 */
	public List<String> getEmailTo() {
		return emailTo;
	}
	/**
	 * @Ganesh
	 *
	 * @param emailTo the emailTo to set
	 */
	public void setEmailTo(List<String> emailTo) {
		this.emailTo = emailTo;
	}
	/**
	 * @Ganesh
	 *
	 * @return the emailBCC
	 */
	public List<String> getEmailBCC() {
		return emailBCC;
	}
	/**
	 * @Ganesh
	 *
	 * @param emailBCC the emailBCC to set
	 */
	public void setEmailBCC(List<String> emailBCC) {
		this.emailBCC = emailBCC;
	}
	/**
	 * @Ganesh
	 *
	 * @return the emailCC
	 */
	public List<String> getEmailCC() {
		return emailCC;
	}
	/**
	 * @Ganesh
	 *
	 * @param emailCC the emailCC to set
	 */
	public void setEmailCC(List<String> emailCC) {
		this.emailCC = emailCC;
	}
	/**
	 * @Ganesh
	 *
	 * @return the emailAttachment
	 */
	public Map<String, String> getEmailAttachment() {
		return emailAttachment;
	}
	/**
	 * @Ganesh
	 *
	 * @param emailAttachment the emailAttachment to set
	 */
	public void setEmailAttachment(Map<String, String> emailAttachment) {
		this.emailAttachment = emailAttachment;
	}
}
