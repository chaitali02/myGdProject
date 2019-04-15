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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.service.SecurityServiceImpl;

public class BaseEntity {
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	
	@Id
	private String id;
	private String uuid;
	private String version;
	private String name;
	private String displayName;
	private String desc;
	private MetaIdentifierHolder createdBy;
	private Date createdOn;
	private String[] tags;
	private String active = "Y";
	private String locked = "N";
	private String published = "N";
	private List<MetaIdentifierHolder> appInfo;
	private String publicFlag = "N";


	static final Logger logger = Logger.getLogger(BaseEntity.class);

	
	public String getPublicFlag() {
		return publicFlag;
	}

	public void setPublicFlag(String publicFlag) {
		this.publicFlag = publicFlag;
	}

	public String getLocked() {
		return this.locked;
	}

	public void setLocked(String locked) {
		this.locked = locked;
	}
	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	public List<MetaIdentifierHolder> getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(List<MetaIdentifierHolder> appInfo) {
		this.appInfo = appInfo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCreatedOn() {
		String tmp = "";
		if (createdOn != null)
			tmp = createdOn.toString();
		return tmp;
	}

	public void setCreatedOn(String createdOn) {
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		
		//Date tmp = new Date(createdOn);
		try {
			this.createdOn = formatter.parse(createdOn);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//tmp;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public MetaIdentifierHolder getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(MetaIdentifierHolder createdBy) {
		this.createdBy = createdBy;
	}

	public void setBaseEntity() {
		logger.info(" id is uuid " + this.getUuid());

		if (this.getUuid() == null || this.getUuid().isEmpty()) {
			this.setUuid(Helper.getNextUUID());
		}
		
		if (this.getVersion() == null || this.getVersion().isEmpty()) {
			this.setVersion(Helper.getVersion());
		}
		
		if(StringUtils.isBlank(this.getName()))
		{
			this.setName("sys_"+this.getUuid());			
		}
		if (this.getCreatedBy() == null || this.getCreatedBy().getRef() == null || this.getCreatedBy().getRef().getUuid().isEmpty()) {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if(requestAttributes != null) {
				if(requestAttributes.getRequest() !=null && requestAttributes.getRequest().getSession(false) != null){
					MetaIdentifierHolder userInfo = (MetaIdentifierHolder) requestAttributes.getRequest().getSession(false).getAttribute("userInfo");
					//sessionContext = (SessionContext) requestAttributes.getRequest().getSession(false).getAttribute("sessionContext");
					if (userInfo != null) {
						this.setCreatedBy(userInfo);
					}else {			
						logger.info("userInfo object is null, default values are used !!");
						this.setCreatedBy(new MetaIdentifierHolder(new MetaIdentifier(MetaType.user, "d04716df-e96a-419f-9118-c81342b47f86", "1464977196")));
					}
			 }else{
					logger.info("HttpRequest or HttpSession instance is null, default values are used !!");
					this.setCreatedBy(new MetaIdentifierHolder(new MetaIdentifier(MetaType.user, "d04716df-e96a-419f-9118-c81342b47f86", "1464977196")));
				}
			}else {
				SessionContext sessionContext = FrameworkThreadLocal.getSessionContext().get();
				if(sessionContext != null) {			
					//logger.info("Setting sessionContext using class  FrameworkThreadLocal !");
					this.setCreatedBy(sessionContext.getUserInfo());
				}
				else {			
					logger.info("ServletRequestAttributes instance is null, default values are used !!");
					this.setCreatedBy(new MetaIdentifierHolder(new MetaIdentifier(MetaType.user, "d04716df-e96a-419f-9118-c81342b47f86", "1464977196")));
				}
			}
		}
		if (this.getCreatedOn() == null || this.getCreatedOn().isEmpty()) {
			this.setCreatedOn(Helper.getCurrentDate().toString());
		}
	}

	/**
	 * Convenience method to create MetaIdentifier
	 * @param type
	 * @return
	 */
	public MetaIdentifier getRef(MetaType type) {
		return new MetaIdentifier(type, uuid, version);
	}
	
	/**
	 * Convenience method to create MetaIdentifierHolder
	 * @param type
	 * @return
	 */
	
	public MetaIdentifierHolder getMetaIdentifierHolder(MetaType type) {
		return new MetaIdentifierHolder(getRef(type));
	}	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
