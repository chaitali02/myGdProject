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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IDataStoreDao;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.dao.IExportDao;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Export;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.register.GraphRegister;
import com.mongodb.util.JSON;

@Service
public class ExportServiceImpl implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	@Autowired
	GraphRegister registerGraph;
	@Autowired
	IDataStoreDao idatastoreDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	IDatasourceDao iDatasourceDao;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired 
	RegisterService registerService;
	@Autowired
	IExportDao iExportDao;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(ExportServiceImpl.class);
	private static final String GET = "get";
	
	public ExportServiceImpl() {
		super();
	}

	public Export save(Export export) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		export.setAppInfo(metaIdentifierHolderList);
		export.setBaseEntity();
		
		Export exprt = iExportDao.save(export);
		String filePath = writeObjectToFile(exprt);
		logger.info("File path: "+filePath);
		exprt.setLocation(filePath);
		Export exp = iExportDao.save(exprt);
		registerGraph.updateGraph((Object) exp, MetaType.export);
		
		return exp;
	}
	
	public String writeObjectToFile(Export exp) throws ZipException, JsonGenerationException, JsonMappingException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException{
		String directoryPath = commonServiceImpl.getConfigValue("framework.mg.export.path");
		directoryPath = directoryPath.endsWith("/") ? "" : directoryPath.concat("/");
		ZipOutputStream zos = null; 
		File file = null;
		ObjectMapper objectMapper  = new ObjectMapper();
		String filePath = null;
		Set<String> uuidInZipSet = new HashSet<>();		
		try {
			Export export = (Export) commonServiceImpl.resolveName(exp, MetaType.export);
			
			logger.info("Writting, Export uuid: "+export.getUuid());
			file = new File(directoryPath.concat("export.json"));
			objectMapper.writeValue(file, export);
			
			File exportZip = new File(directoryPath.concat(export.getUuid()).concat(".zip"));
			filePath = exportZip.getAbsolutePath();
			/*logger.info("Zip file absolute path: "+filePath);*/
			zos = new ZipOutputStream(new FileOutputStream(exportZip));
			zos.putNextEntry(new ZipEntry(file.getName()));
			Long fileSize = file.length();
			byte[] byteBuffer = new byte[fileSize.intValue()];
			int bytesRead = -1;
			FileInputStream fis = new FileInputStream(file);
			while ((bytesRead = fis.read(byteBuffer)) != -1) {
				zos.write(byteBuffer, 0, bytesRead);
		        }
			if(file.exists()) {
				//logger.info("Deleting file: "+file.getName());
				file.delete();
			}
			//List<MetaIdentifierHolder> metaInfo = export.getMetaInfo();
			for(MetaIdentifierHolder holder : export.getMetaInfo()) {
				String uuid = holder.getRef().getUuid();
				logger.info("writing to metaInfo: "+uuid);
				Object object = commonServiceImpl.getLatestByUuid(uuid, holder.getRef().getType().toString());
		
				file = new File(directoryPath.concat(uuid).concat(".json"));
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(JSON.parse(objectMapper.writeValueAsString(object)).toString());
				fileWriter.close();
				//objectMapper.writeValue(file, JSON.parse(objectMapper.writeValueAsString(object)));
				try {
					zos.putNextEntry(new ZipEntry(file.getName()));					
				}catch (Exception e) {
					logger.info("\""+uuid+".json\"" + " alReady present in file.");
					continue;
				}
				FileInputStream metaFis = new FileInputStream(file);
				while ((bytesRead = metaFis.read(byteBuffer)) != -1) {
					zos.write(byteBuffer, 0, bytesRead);
			    }
				if(export.getIncludeDep().equalsIgnoreCase("y")) {
					//System.out.println("\n");
					int level = Integer.parseInt(commonServiceImpl.getConfigValue("framework.dependency.level"));
					String metaUuid = ((BaseEntity)object).getUuid();
					if(uuidInZipSet.add(metaUuid)) {
						if(level>0)
							includeDependencies(object, level, uuidInZipSet, zos);
					}else
						logger.info("Duplicate metaInfo object \""+metaUuid+"\"");
					//System.out.println("\n");
				}
				if(file.exists()) {
					//logger.info("Deleting file: "+file.getName());
					file.delete();
				}	
				metaFis.close();
			}
			fis.close();
		}
		finally {
			if (zos != null) {
				try {
					zos.flush();
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return filePath;
	}
	public File writeObjectToFile(BaseEntity baseEntity) throws JsonProcessingException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String directoryPath = commonServiceImpl.getConfigValue("framework.mg.export.path");
		directoryPath = directoryPath.endsWith("/") ? "" : directoryPath.concat("/");
		ObjectMapper objectMapper  = new ObjectMapper();
		String uuid = baseEntity.getUuid();
		File file = new File(directoryPath.concat(uuid).concat(".json"));
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(JSON.parse(objectMapper.writeValueAsString(baseEntity)).toString());
		fileWriter.close();
		return file;
	}
	public ZipOutputStream writeFileToZip(ZipOutputStream zos, File file) throws IOException, ZipException {
		int bytesRead = -1;
		Long fileSize = file.length();		
		byte[] byteBuffer = new byte[fileSize.intValue()];
			zos.putNextEntry(new ZipEntry(file.getName()));
			FileInputStream metaFis = new FileInputStream(file);
			while ((bytesRead = metaFis.read(byteBuffer)) != -1) {
				zos.write(byteBuffer, 0, bytesRead);
		    }
			metaFis.close();
		if(file.exists()) {
			//logger.info("Deleting file: "+file.getName());
			file.delete();
		}		
		return zos;
	}
	
	public void includeDependencies(Object object, int level, Set<String> uuidInZipSet, ZipOutputStream zos/*, File exportZip*/) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, IOException, ParseException {
		if(object != null) {
			Method[] methodList = object.getClass().getMethods();
			ArrayList listObj = null;
			Class [] interfaces = null;
			logger.info("Level: "+level);
			for(Method method : methodList) {
				if (!method.getName().startsWith(GET) || method.getParameterCount() > 0) {
					continue;
				}
				//logger.info("Method: ");
				//logger.info(method.getReturnType()+"  "+method.getName());
				MetaType type = null;
				if (object instanceof MetaIdentifier) {
					type = (MetaType) object.getClass().getMethod(GET+"Type").invoke(object);
				}
				for(int i=1; i<level; i++)
				if((method.getReturnType() == MetaIdentifierHolder.class) && method.getName().startsWith(GET) /*&& !method.getName().toLowerCase().contains("createdby")*/) {
					MetaIdentifierHolder holder = (MetaIdentifierHolder) method.invoke(object);
					if(holder != null) {
						//logger.info("ref: "+holder.getRef().toString());
						String uuid = holder.getRef().getUuid();
						String version = holder.getRef().getVersion();
						MetaType metaType = holder.getRef().getType();
						
						Object metaObject = getMetaObject(metaType.toString().toLowerCase(), uuid, version);
						if(metaObject != null) {
							//logger.info(metaObject.toString());
							String metaUuid = ((BaseEntity)metaObject).getUuid();							
							if(uuidInZipSet.add(metaUuid)) {
								File objectJsonFile = writeObjectToFile((BaseEntity)metaObject);
								zos = writeFileToZip(zos, objectJsonFile);
								includeDependencies(metaObject, level-1, uuidInZipSet, zos);
							}else
								logger.info("Duplicate child object \""+metaUuid+"\"");
						}							
					}						
				}
				
				for(int i=1; i<level; i++)
					if((method.getReturnType() == SourceAttr.class) && method.getName().startsWith(GET) /*&& !method.getName().toLowerCase().contains("createdby")*/) {
						SourceAttr sourceAttr = (SourceAttr) method.invoke(object);
						if(sourceAttr != null) {
							//logger.info("ref: "+sourceAttr.getRef().toString());
							String uuid = sourceAttr.getRef().getUuid();
							String version = sourceAttr.getRef().getVersion();
							MetaType metaType = sourceAttr.getRef().getType();
							
							Object metaObject = getMetaObject(metaType.toString().toLowerCase(), uuid, version);
							if(metaObject != null) {
								//logger.info(metaObject.toString());
								String metaUuid = ((BaseEntity)metaObject).getUuid();							
								if(uuidInZipSet.add(metaUuid)) {
									File objectJsonFile = writeObjectToFile((BaseEntity)metaObject);
									zos = writeFileToZip(zos, objectJsonFile);
									includeDependencies(metaObject, level-1, uuidInZipSet, zos);
								}else
									logger.info("Duplicate child object \""+metaUuid+"\"");
							}							
						}						
					}
				
				for(int i=1; i<=level; i++)
				if(object.getClass().getName().toLowerCase().contains("MetaIdentifierHolder".toLowerCase())) {
					MetaIdentifierHolder holder = (MetaIdentifierHolder)object;					
					//logger.info("MetaIdentifierHolder instance: "+holder.toString());
					if(holder != null) {
						//logger.info("ref: "+holder.getRef().toString());
						String uuid = holder.getRef().getUuid();
						String version = holder.getRef().getVersion();
						MetaType metaType = holder.getRef().getType();
						
						Object metaObject = getMetaObject(metaType.toString().toLowerCase(), uuid, version);
						if(metaObject != null) {
							//logger.info(metaObject.toString());
							String metaUuid = ((BaseEntity)metaObject).getUuid();							
							if(uuidInZipSet.add(metaUuid)) {
								File objectJsonFile = writeObjectToFile((BaseEntity)metaObject);
								zos = writeFileToZip(zos, objectJsonFile);
								includeDependencies(metaObject, level-1, uuidInZipSet, zos);
							}else
								logger.info("Duplicate child object \""+metaUuid+"\"");
						}							
					}							
				}else {
					//logger.info("object class: "+object.getClass().getName()+"\tCompare:  "+object.getClass().getName().equalsIgnoreCase("com.inferyx.framework.domain.MetaIdentifierHolder"));																												    
				}
				
				Object invokedObj = method.invoke(object);
				if (invokedObj == null || invokedObj.getClass().isPrimitive()) {
					continue;
				}
				//logger.info("Class : " + invokedObj.getClass().getName());
				if (invokedObj.getClass().getName().startsWith("[") || invokedObj.getClass().getName().equals("java.util.ArrayList")) {
					interfaces = invokedObj.getClass().getInterfaces();
					if (interfaces == null || interfaces.length <= 0) {
						continue;
					}
					for (Class<?> intrfce : interfaces) {
						if (intrfce.getName().equals("java.util.List")) {
							listObj = (ArrayList)invokedObj;
							for (Object arrayObj : listObj) {								
								if (arrayObj.getClass().getPackage().getName().contains("inferyx")) {
									//logger.info("Class : " + invokedObj.getClass().getName());
									includeDependencies(arrayObj, level, uuidInZipSet, zos);
								}
							}
						} else {
							continue;
						}
					}
					continue;
				}
				
				if (!invokedObj.getClass().getPackage().getName().contains("inferyx")) {
					continue;
				}
				includeDependencies(invokedObj, level, uuidInZipSet,zos);
			}
		}else {
			logger.info("The object is null.");
			return;
		}
	}
	public Object getMetaObject(String type, String uuid, String version) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if(type.equalsIgnoreCase(MetaType.datasetview.toString()) 
				|| type.equalsIgnoreCase(MetaType.dqview.toString()) 
				|| type.equalsIgnoreCase(MetaType.ruleview.toString()) 
				|| type.equalsIgnoreCase(MetaType.dashboardview.toString())) {
			if(StringUtils.isBlank(version))
				return registerService.getLatestByUuid(uuid, type);
			else
				return registerService.getOneByUuidAndVersion(uuid, version, type);
		}else 
			return commonServiceImpl.getOneByUuidAndVersion(uuid, version, type);
	}
	public HttpServletResponse download(String uuid, HttpServletResponse response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		try {
			String dirPath = commonServiceImpl.getConfigValue("framework.mg.export.path");
			dirPath = dirPath.endsWith("/") ? "" : dirPath.concat("/");
	        	String filePath = dirPath.concat(uuid).concat(".zip");
	            File file = new File(filePath);
	 
	            if (file.exists()) {
	            	logger.info("File found.");
	                String mimeType = null;//context.getMimeType(file.getPath());
	 
	                if (mimeType == null) {
	                    mimeType = "application/octet-stream";
	                }
	 
	                response.setContentType(mimeType);
	                response.addHeader("Content-Disposition", "attachment; filename=" + uuid+".zip");
	                response.setContentLength((int) file.length());
	 
	                ServletOutputStream os = response.getOutputStream();
	                FileInputStream fis = new FileInputStream(file);
	                Long fileSize = file.length();
	                byte[] buffer = new byte[fileSize.intValue()];
	                int b = -1;
	 
	                while ((b = fis.read(buffer)) != -1) {
	                    os.write(buffer, 0, b);
	                }
	 
	                fis.close();
	                os.close();
	            } else {
	            	logger.info("Requested " + uuid+".zip" + " file not found!!");
	            }
	        } catch (IOException e) {
	        	logger.info("Exception:- " + e.getMessage());
	        }
		return response;
	}
}
