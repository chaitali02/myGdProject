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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Export;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Property;
import com.inferyx.framework.domain.UploadExec;

@Service
public class AdminServiceImpl {	
	@Resource(name="taskThreadMap")
	ConcurrentHashMap taskThreadMap;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;	
	@Autowired
	private Helper helper;
	
	static final Logger logger = Logger.getLogger(AdminServiceImpl.class);

	public ConcurrentHashMap getTaskThreadMap() {
		logger.info(" taskThreadMap in AdminService : " + taskThreadMap);
		return taskThreadMap;
	}

	public void setTaskThreadMap(ConcurrentHashMap taskThreadMap) {
		this.taskThreadMap = taskThreadMap;
	} 
	
	public String getSettings() throws FileNotFoundException, IOException{
		com.inferyx.framework.domain.Settings setting =new com.inferyx.framework.domain.Settings();
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		List<Property> metaEngine=new ArrayList<>();
		List<Property> generalSetting=new ArrayList<>();
		List<Property> ruleEngine=new ArrayList<>();
		for (Entry<Object, Object> e : Helper.getPropertiesList()) {
		    if (e.getKey().toString().startsWith("framework.mongo")){
		    	Property property=new Property();
		    	property.setPropertyName(e.getKey().toString());
		    	property.setPropertyValue(e.getValue().toString());
		    	metaEngine.add(property);
		    }
		    else if(e.getKey().toString().matches("framework.*")){
		    	Property property=new Property();
		    	property.setPropertyName(e.getKey().toString());
		    	property.setPropertyValue(e.getValue().toString());
		    	generalSetting.add(property);
		    }
		    else if(e.getKey().toString().matches("spark.*") || e.getKey().toString().matches("hive.*")){
		    	Property property=new Property();
		    	property.setPropertyName(e.getKey().toString());
		    	property.setPropertyValue(e.getValue().toString());
		    	ruleEngine.add(property);
		    }
		    
		}
		setting.setMetaEngine(metaEngine);
		setting.setGeneralSetting(generalSetting);
		setting.setRuleEngine(ruleEngine);
		return objectWriter.writeValueAsString(setting);
	}
	public void setSettings(Map<String, Object>object) throws IOException, URISyntaxException{
		ObjectMapper mapper = new ObjectMapper();
		Properties prop = new Properties();
		OutputStream output = null;
		String filePath="framework.properties_"+Helper.getVersion();
		String path=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		File oldfile =new File(path+"framework.properties");
		File newfile =new File(path+filePath);
		oldfile.renameTo(newfile);
		
		com.inferyx.framework.domain.Settings setting = mapper.convertValue(object,com.inferyx.framework.domain.Settings.class);
		for(Property property : setting.getGeneralSetting()){
			prop.setProperty(property.getPropertyName(),property.getPropertyValue());
		}
		for(Property property : setting.getMetaEngine()){
			prop.setProperty(property.getPropertyName(),property.getPropertyValue());
		}
		for(Property property : setting.getRuleEngine()){
			prop.setProperty(property.getPropertyName(),property.getPropertyValue());
		}
		output = new FileOutputStream(createPropertiesFile("framework.properties"));
		prop.store(output, null);		
	}
	private File createPropertiesFile(String relativeFilePath) throws URISyntaxException {
	    return new File(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()), relativeFilePath);
	}

	public String upload(MultipartFile multiPartFile, String filename, String fileType) throws FileNotFoundException, IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {

		FileType fType = Helper.getFileType(fileType);
		String directoryPath = helper.getFileDirectoryByFileType(fType);
		String location = directoryPath + "/" + filename;
		File dest = new File(location);
		multiPartFile.transferTo(dest);
		
		UploadExec uploadExec=new UploadExec();
		uploadExec.setName(Helper.getFileName(filename));
		uploadExec.setFileName(filename);
		uploadExec.setBaseEntity();
		uploadExec.setLocation(location);
		//uploadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(Helper.getMetaType(metaType), metaUuid, metaVersion)));
		commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
		
		return dest.getAbsolutePath();
	}
}
