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
public class Notification {
	List<String> emailTo;
	List<String> emailBCC;
	List<String> emailCC;
	String emailSubect;
	List<String> emailAttachment;
	Map<String, Object> emailFromDetails;
	String message;
	
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
	 * @return the emailSubect
	 */
	public String getEmailSubect() {
		return emailSubect;
	}
	/**
	 * @Ganesh
	 *
	 * @param emailSubect the emailSubect to set
	 */
	public void setEmailSubect(String emailSubect) {
		this.emailSubect = emailSubect;
	}
	/**
	 * @Ganesh
	 *
	 * @return the emailAttachment
	 */
	public List<String> getEmailAttachment() {
		return emailAttachment;
	}
	/**
	 * @Ganesh
	 *
	 * @param emailAttachment the emailAttachment to set
	 */
	public void setEmailAttachment(List<String> emailAttachment) {
		this.emailAttachment = emailAttachment;
	}
	/**
	 * @Ganesh
	 *
	 * @return the emailFromDetails
	 */
	public Map<String, Object> getEmailFromDetails() {
		return emailFromDetails;
	}
	/**
	 * @Ganesh
	 *
	 * @param emailFromDetails the emailFromDetails to set
	 */
	public void setEmailFromDetails(Map<String, Object> emailFromDetails) {
		this.emailFromDetails = emailFromDetails;
	}
	/**
	 * @Ganesh
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @Ganesh
	 *
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Notification [emailTo=" + emailTo + ", emailBCC=" + emailBCC + ", emailCC=" + emailCC + ", emailSubect="
				+ emailSubect + ", emailAttachment=" + emailAttachment + ", emailFromDetails=" + emailFromDetails
				+ ", message=" + message + "]";
	}	
	
	
}
