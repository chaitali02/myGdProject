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
package com.inferyx.framework.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IApplicationDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.ApplicationView;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.register.GraphRegister;

/**
 * @author Ganesh
 *
 */
@Service
public class ApplicationViewServiceImpl {
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private SecurityServiceImpl securityServiceImpl;
    @Autowired
    private GraphRegister<?> registerGraph;
    @Autowired
    private IApplicationDao iApplicationDao;
    
    static final Logger logger = Logger.getLogger(ApplicationViewServiceImpl.class);
    
    public ApplicationView findOneByUuidAndVersion(String applicationUuid, String applicationVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		logger.info("Inside findOneByUuidAndVersion.");
		ApplicationView applicationView = new ApplicationView();
		Application application = (Application) commonServiceImpl.getOneByUuidAndVersion(applicationUuid, applicationVersion, MetaType.application.toString());
		//setting applicationView properties specific to baseEntity
		applicationView.setId(application.getId());
		applicationView.setUuid(application.getUuid());
		applicationView.setVersion(application.getVersion());
		applicationView.setName(application.getName());
		applicationView.setDisplayName(application.getDisplayName());
		applicationView.setActive(application.getActive());
		applicationView.setLocked(application.getLocked());
		applicationView.setAppInfo(application.getAppInfo());
		applicationView.setCreatedBy(application.getCreatedBy());
		applicationView.setCreatedOn(application.getCreatedOn());
		applicationView.setDesc(application.getDesc());
		applicationView.setPublished(application.getPublished());
		applicationView.setTags(application.getTags());
		applicationView.setDeployPort(application.getDeployPort());
		applicationView.setOrgInfo(application.getOrgInfo());
		applicationView.setPublicFlag(application.getPublicFlag());
		applicationView.setApplicationType(application.getApplicationType());
		//setting applicationView properties specific to application		
		MetaIdentifierHolder paramListHolder = application.getParamList();
		ParamList resolvedParamList = null;
		if(paramListHolder != null) {
			ParamList paramList = (ParamList) commonServiceImpl.getAsOf(paramListHolder.getRef().getUuid(), application.getVersion(), paramListHolder.getRef().getType().toString());
			resolvedParamList = (ParamList) commonServiceImpl.resolveName(paramList, MetaType.paramlist);
		}
		
		applicationView.setParamList(resolvedParamList);
		applicationView.setDataSource(application.getDataSource());
		return applicationView;
	}
    
    public BaseEntity save(ApplicationView applicationView) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
    	Application application = null;
    	MetaIdentifierHolder paramListMIH = null;
		if(applicationView.getApplicationChg().equalsIgnoreCase("Y") && applicationView.getUuid() == null) {
			application = new Application();
			//setting application baseEntity
			application.setName(applicationView.getName());
			application.setDisplayName(applicationView.getDisplayName());
			application.setTags(applicationView.getTags());
			application.setLocked(applicationView.getLocked());
			application.setDeployPort(applicationView.getDeployPort());
			application.setOrgInfo(applicationView.getOrgInfo());
			application.setApplicationType(applicationView.getApplicationType());
			application.setPublicFlag(applicationView.getPublicFlag());
			application.setBaseEntity();
			if(applicationView.getParamList() != null) {
				paramListMIH = processParamList(applicationView, application.getUuid());
			}
		} else if(applicationView.getApplicationChg().equalsIgnoreCase("Y") & applicationView.getParamlistChg().equalsIgnoreCase("N") ) {
			//setting application baseEntity
			application = setApplicationBaseEntity(applicationView);
			paramListMIH = new MetaIdentifierHolder(new MetaIdentifier(MetaType.paramlist, applicationView.getParamList().getUuid(), null));
		} else if(applicationView.getParamlistChg().equalsIgnoreCase("Y")) {
			//setting application baseEntity
			application = setApplicationBaseEntity(applicationView);
			if(applicationView.getParamList() != null) {
				paramListMIH = processParamList(applicationView, application.getUuid());
			}
		}
		
		//setting application specific properties
		application.setDataSource(applicationView.getDataSource());
		if(applicationView.getParamList() != null) {
			application.setParamList(paramListMIH);
		}
		return save(application);
	}
    
    private Application setApplicationBaseEntity(ApplicationView applicationView) {
    	Application application = new Application();
		//setting application baseEntitiy
		application.setActive(applicationView.getActive());
		application.setAppInfo(applicationView.getAppInfo());
		application.setCreatedBy(applicationView.getCreatedBy());
		application.setDesc(applicationView.getDesc());
		application.setName(applicationView.getName());
		application.setDisplayName(applicationView.getDisplayName());
		application.setPublished(applicationView.getPublished());
		application.setTags(applicationView.getTags());
		application.setUuid(applicationView.getUuid());
		application.setVersion(Helper.getVersion());
		application.setDeployPort(applicationView.getDeployPort());
		application.setOrgInfo(applicationView.getOrgInfo());
		application.setPublicFlag(applicationView.getPublicFlag());
		application.setApplicationType(applicationView.getApplicationType());
		return application;
	}
    
    public Application save(Application application) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		List<MetaIdentifierHolder> appInfos = new ArrayList<>();
		appInfos.add(securityServiceImpl.getAppInfo());
		application.setAppInfo(appInfos);
		application.setCreatedBy(null);
		application.setBaseEntity();
		Application savedApp = iApplicationDao.save(application);
		registerGraph.updateGraph((Object) savedApp, MetaType.application);
		return savedApp;
	}
    
    public MetaIdentifierHolder processParamList(ApplicationView applicationView, String applicationUuid) throws JsonProcessingException, JSONException, ParseException {
    	ParamList appParamList = applicationView.getParamList();
    	appParamList.setName("paramlist_"+applicationView.getName());
    	appParamList.setLocked(applicationView.getLocked());
    	appParamList.setBaseEntity();
    	BaseEntity savedParamList = (BaseEntity) commonServiceImpl.save(MetaType.paramlist.toString(), appParamList);
		return new MetaIdentifierHolder(new MetaIdentifier(MetaType.paramlist, savedParamList.getUuid(), null));
	}
}
