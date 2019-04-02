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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IDataStoreDao;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.dao.IImportDao;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Export;
import com.inferyx.framework.domain.Import;
import com.inferyx.framework.domain.ImportIdentifierHolder;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ImportServiceImpl {
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
	IImportDao iImportDao ;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	
	static final Logger logger = Logger.getLogger(ImportServiceImpl.class);
	private static final String GET = "get";
	
	public ImportServiceImpl() {
		super();
	}

	public Import save(Import imprt, String fileName) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		imprt.setAppInfo(metaIdentifierHolderList);
		imprt.setBaseEntity();
		String filePath = saveMetaInfoDependencies(imprt, fileName);
		imprt.setLocation(filePath);
		Import imp = iImportDao.save(imprt);
		registerGraph.updateGraph((Object) imp, MetaType.Import);
		return imp;
	}
	
	public Export uploadFile(MultipartFile multiPartFile, String filename) throws IllegalStateException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		ObjectMapper objmapper=new  ObjectMapper();
		String dirPath = commonServiceImpl.getConfigValue("framework.mg.import.path");
		dirPath = dirPath.endsWith("/") ? "" : dirPath.concat("/");
		String filePath = dirPath.concat(filename);
		File dest = new File(filePath);
		multiPartFile.transferTo(dest);
		ZipFile zipFile = new ZipFile(filePath);
		InputStream is =  searchJsonFile("export.json", zipFile);
		if(is != null)
			return objmapper.readValue(is, Export.class);
		else
			return null;
	}
	
	public InputStream searchJsonFile(String name, ZipFile file) throws IOException {
		Enumeration<? extends ZipEntry> zipEntries = file.entries();
		while (zipEntries.hasMoreElements()) { 
			final ZipEntry entry = zipEntries.nextElement();
		    if(entry.getName().endsWith(name))
		      return file.getInputStream(entry);
		  }
		  return null;
	}
	
	@SuppressWarnings("unused")
	public String saveMetaInfoDependencies(Import imprt, String fileName) throws IOException, ParseException, JSONException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String dirPath = commonServiceImpl.getConfigValue("framework.mg.import.path");
		dirPath = dirPath.endsWith("/") ? "" : dirPath.concat("/");			
			String filePath = dirPath.concat(fileName).concat(".zip");
			ZipFile zipFile = new ZipFile(filePath);
			
			if(zipFile != null) {
				ObjectMapper objMapper=new  ObjectMapper();
				List<ImportIdentifierHolder> metaInfo = imprt.getMetaInfo();
				if(metaInfo != null){
					for(ImportIdentifierHolder holder : metaInfo) {
						String uuid = holder.getRef().getUuid();
						MetaType metaType = holder.getRef().getType();
						String version = holder.getRef().getVersion();
						InputStream is =  searchJsonFile(uuid+".json", zipFile);
						if(is != null) {
							BaseEntity baseEntity = (BaseEntity) objMapper.readValue(is, Helper.getDomainClass(metaType));
							if(uuid.equalsIgnoreCase(baseEntity.getUuid())) {
								if(imprt.getIncludeDep().equalsIgnoreCase("y")) {
									if(metaType.equals(MetaType.simple)) {
										logger.info("Found metaType \"simple\"");
									}
									else {
										int level = Integer.parseInt(commonServiceImpl.getConfigValue("framework.dependency.level"));
											if(level>0) {											
												boolean isPresent = checkDependency(uuid, version, metaType);
												if(!isPresent) {
													includeDependency(baseEntity, zipFile, level);
													baseEntity.setId(null);
													Object savedObject = commonServiceImpl.save(metaType.toString().toLowerCase(), (Object)baseEntity);
												} else{
													logger.info("Object \""+uuid+"\" not saved, alReady present in collection.");
													sendResponse("422", MessageStatus.FAIL.toString(), "AlREADY present.");
												}
										} else {
											boolean isPresent = checkDependency(uuid, version, metaType);
											if(!isPresent) {
												baseEntity.setId(null);
												Object savedObject = commonServiceImpl.save(metaType.toString().toLowerCase(), (Object)baseEntity);
												}else{
													logger.info("Object \""+uuid+"\" not saved, alReady present in collection.");
													sendResponse("422", MessageStatus.FAIL.toString(), "AlREADY present.");
												}
										}
									}
								}else {
									boolean isPresent = checkDependency(uuid, version, metaType);
									if(!isPresent) {
										baseEntity.setId(null);
										Object savedObject = commonServiceImpl.save(metaType.toString().toLowerCase(), (Object)baseEntity);
									}else
										logger.info("Object \""+uuid+"\" not saved, alReady present in collection.");
								}
							}else {
								logger.info("Corrupted file, aborting the operation.");
								sendResponse("422", MessageStatus.FAIL.toString(), "File corrupted, aborting the operation.");
							}							
						}else {
							logger.info("No json file available for the object \""+uuid+" \" inside "+ imprt.getName()+".zip file.");
							sendResponse("422", MessageStatus.FAIL.toString(), "File corrupted, can not find the included dependencies.");
						}
					}
				}else {
					logger.info("Invalid zip file. metaInfo not found.");
					if(zipFile != null)
						zipFile.close();
					sendResponse("422", MessageStatus.FAIL.toString(), "Invalid zip file.");
					return null;
				}				
			}else {
				logger.info("Zip file not present.");
				if(zipFile != null)
					zipFile.close();
				sendResponse("404", MessageStatus.FAIL.toString(), "Requested file not fount.");
				return null;
			}
			return filePath;
	}	
	public void includeDependency(Object object, ZipFile zipFile, int level) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, JSONException, ParseException {
		if(object != null && zipFile != null) {
			ObjectMapper objMapper=new  ObjectMapper();
			Method[] methodList = object.getClass().getMethods();
			ArrayList listObj = null;
			Class [] interfaces = null;
			for(Method method : methodList) {
				if (!method.getName().startsWith(GET) || method.getParameterCount() > 0) {
					continue;
				}
				MetaType type = null;
				if (object instanceof MetaIdentifier) {
					type = (MetaType) object.getClass().getMethod(GET+"Type").invoke(object);
				}			
				for(int i=1; i<=level; i++)
					if((method.getReturnType() == MetaIdentifierHolder.class) && method.getName().startsWith(GET) /*&& !method.getName().toLowerCase().contains("createdby")*/) {
						MetaIdentifierHolder holder = (MetaIdentifierHolder) method.invoke(object);
						if(holder != null) {
							String uuid = holder.getRef().getUuid();
							MetaType metaType = holder.getRef().getType();
							String version = holder.getRef().getVersion();							
							boolean isPresent = checkDependency(uuid, version, metaType);
							InputStream is =  searchJsonFile(uuid+".json", zipFile);
							if(is != null) {
								if(!isPresent) {
									if(metaType.equals(MetaType.simple)) {
										logger.info("Found metaType \"simple\"");
									}else {
											Object obj = objMapper.readValue(is, Helper.getDomainClass(metaType));
											BaseEntity baseEntity = (BaseEntity)obj;
											includeDependency(baseEntity, zipFile, level-1);
											baseEntity.setId(null);
											obj = baseEntity;
											Object savedObject = commonServiceImpl.save(metaType.toString().toLowerCase(), obj);
											logger.info("Type: "+metaType.toString()+"\tSaved object: "+savedObject.toString());
									}
								}else
									logger.info("Object \""+uuid+"\" not saved, alReady present in collection.");
							}else {
								logger.info("No json file available of the object \""+uuid+" \" inside "/*+ imprt.getName()*/+".zip file.");
								//int defaultLevel = Integer.parseInt(commonServiceImpl.getConfigValue("framework.dependency.level"));
							}
						}						
					}
				for(int i=1; i<=level; i++)
					if((method.getReturnType() == SourceAttr.class) && method.getName().startsWith(GET) /*&& !method.getName().toLowerCase().contains("createdby")*/) {
						SourceAttr sourceAttr = (SourceAttr) method.invoke(object);
						if(sourceAttr != null) {
							String uuid = sourceAttr.getRef().getUuid();
							MetaType metaType = sourceAttr.getRef().getType();
							String version = sourceAttr.getRef().getVersion();
							boolean isPresent = checkDependency(uuid, version, metaType);
							if(!isPresent) {
								InputStream is =  searchJsonFile(uuid+".json", zipFile);
								if(is != null)
								if(metaType.equals(MetaType.simple)) {
									logger.info("Found metaType \"simple\"");
								}
								else {
										Object obj = objMapper.readValue(is, Helper.getDomainClass(metaType));
										BaseEntity baseEntity = (BaseEntity)obj;
										includeDependency(baseEntity, zipFile, level-1);
												baseEntity.setId(null);
												obj = baseEntity;
												Object savedObject = commonServiceImpl.save(metaType.toString().toLowerCase(), obj);
												logger.info("Type: "+metaType.toString()+"\tSaved object: "+savedObject.toString());
								}else
									logger.info("Object unavailable in file.");
							}else
								logger.info("Object \""+uuid+"\" not saved, alReady present in collection.");	
						}						
					}
			for(int i=1; i<=level; i++)
				if(object.getClass().getName().toLowerCase().contains("MetaIdentifierHolder".toLowerCase())) {
					MetaIdentifierHolder holder = (MetaIdentifierHolder)object;					
					//logger.info("MetaIdentifierHolder instance: "+holder.toString());
					if(holder != null) {
						String uuid = holder.getRef().getUuid();
						MetaType metaType = holder.getRef().getType();
						String version = holder.getRef().getVersion();
						boolean isPresent = checkDependency(uuid, version, metaType);
						if(!isPresent) {
							InputStream is =  searchJsonFile(uuid+".json", zipFile);
							if(is != null)
							if(metaType.equals(MetaType.simple)) {
								logger.info("Found metaType \"simple\"");
							}else {
									Object obj = objMapper.readValue(is, Helper.getDomainClass(metaType));
									BaseEntity baseEntity = (BaseEntity)obj;
									includeDependency(baseEntity, zipFile, level-1);
									baseEntity.setId(null);
									obj = baseEntity;
									Object savedObject = commonServiceImpl.save(metaType.toString().toLowerCase(), obj);
									logger.info("Type: "+metaType.toString()+"\tSaved object: "+savedObject.toString());
							}else
								logger.info("Object unavailable in file.");
						}else
							logger.info("Object \""+uuid+"\" not saved, alReady present in collection.");
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
									//logger.info("Class : " + invokedObj.getClass().getName()+"  invokedObj instanceof MetaIdentifierHolder: "+(invokedObj instanceof MetaIdentifierHolder));
									includeDependency(arrayObj, zipFile, level);
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
				includeDependency(invokedObj, zipFile, level);
			}
		}else {
			logger.info("The meta object or/and zip file is/are null.");
			return;
		}
	}
	public boolean checkDependency(String uuid, String version, MetaType metaType) throws JsonProcessingException {
		boolean isPresent = true;
		/*String uuid = baseEntity.getUuid();
		String version  = baseEntity.getVersion();*/
		Object metaObject = commonServiceImpl.getOneByUuidAndVersion(uuid, version, metaType.toString().toLowerCase());
		if(metaObject == null)
			isPresent = false;
		return isPresent;
	}
	public HttpServletResponse sendResponse(String code, String status, String msg) throws JSONException, ParseException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(requestAttributes != null) {
			HttpServletResponse response = requestAttributes.getResponse();
			if(response != null) {
					Message message = new Message(code, status, msg);
					Message savedMessage = messageServiceImpl.save(message);
					
					//PrintWriter out = response.getWriter();
					ObjectMapper mapper = new ObjectMapper();
					String messageJson = mapper.writeValueAsString(savedMessage);
					response.setContentType("application/json");
					//response.setCharacterEncoding("UTF-8");
					response.setStatus(Integer.parseInt(code));
					//out.print(messageJson);
					///out.flush();
					response.getOutputStream().write(messageJson.getBytes());
					//response.getOutputStream().flush();
					response.getOutputStream().close();
					//System.out.println("\n\n");
					return response;
			}else
				logger.info("HttpServletResponse response is \""+null+"\"");
		}else
			logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");
		return null;
	}
	
	public boolean validateDependency(Object object, int level, ZipFile zipFile) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, JSONException, ParseException {
		boolean result = true;
		Method[] methodList = object.getClass().getMethods();
		ArrayList listObj = null;
		Class [] interfaces = null;
		for(Method method : methodList) {
			if (!method.getName().startsWith(GET) || method.getParameterCount() > 0) {
				continue;
			}
			MetaType type = null;
			if (object instanceof MetaIdentifier) {
				type = (MetaType) object.getClass().getMethod(GET+"Type").invoke(object);
			}		
			for(int i=1; i<=level; i++)
				if((method.getReturnType() == MetaIdentifierHolder.class) && method.getName().startsWith(GET)/* && !method.getName().toLowerCase().contains("createdby")*/) {
					MetaIdentifierHolder holder = (MetaIdentifierHolder) method.invoke(object);
					if(holder != null) {
						String uuid = holder.getRef().getUuid();
						MetaType metaType = holder.getRef().getType();
						String version = holder.getRef().getVersion();	
						InputStream is =  searchJsonFile(uuid+".json", zipFile);
						if(is != null) {
							if(metaType.equals(MetaType.simple)) {
								logger.info("Found metaType \"simple\"");
							}
							else {									
									boolean isPresent = checkDependency(uuid, version, metaType);
									if(!isPresent) {
										logger.info("Object "+"\""+uuid+"\""+" is not present in collection "+"\""+metaType+"\"");
										return false;
									}
							}
						}else
							return false;
					}
				}
			for(int i=1; i<=level; i++)
				if((method.getReturnType() == SourceAttr.class) && method.getName().startsWith(GET)/* && !method.getName().toLowerCase().contains("createdby")*/) {
					SourceAttr sourceAttr = (SourceAttr) method.invoke(object);
					if(sourceAttr != null) {
						String uuid = sourceAttr.getRef().getUuid();
						MetaType metaType = sourceAttr.getRef().getType();
						String version = sourceAttr.getRef().getVersion();
						
							if(metaType.equals(MetaType.simple)) {
								logger.info("Found metaType \"simple\"");
							}
							else {									
									boolean isPresent = checkDependency(uuid, version, metaType);
									if(!isPresent) {
										logger.info("Object "+"\""+uuid+"\""+" is not present in collection "+"\""+metaType+"\"");
										return false;
									}
							}
					}
				}
		for(int i=1; i<=level; i++)
			if(object.getClass().getName().toLowerCase().contains("MetaIdentifierHolder".toLowerCase())) {
				MetaIdentifierHolder holder = (MetaIdentifierHolder)object;					
				//logger.info("MetaIdentifierHolder instance: "+holder.toString());
				if(holder != null) {
					String uuid = holder.getRef().getUuid();
					MetaType metaType = holder.getRef().getType();
					String version = holder.getRef().getVersion();						
						if(metaType.equals(MetaType.simple)) {
							logger.info("MetaType: simple, not written.");
						}
						else {									
								boolean isPresent = checkDependency(uuid, version, metaType);
								if(!isPresent) {
									logger.info("Object "+"\""+uuid+"\""+" is not present in collection "+"\""+metaType+"\"");
									return false;
								}
						}
				}							
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
								//logger.info("Class : " + invokedObj.getClass().getName()+"  invokedObj instanceof MetaIdentifierHolder: "+(invokedObj instanceof MetaIdentifierHolder));
								result = validateDependency(invokedObj, level, zipFile);
								if(result == false) {
									break;	
								}
							}
						}
					} else {
						continue;
					}
					if(result == false) {
						break;	
					}
				}
				continue;
			}
			if (!invokedObj.getClass().getPackage().getName().contains("inferyx")) {
				continue;
			}
			result = validateDependency(invokedObj, level, zipFile);
			if(result == false) {
				break;	
			}
		}				
		return result;
	}
	@SuppressWarnings("unused")
	public String validateMetaInfoDependencies(Import imprt, String fileName) throws IOException, ParseException, JSONException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String dirPath = commonServiceImpl.getConfigValue("framework.mg.import.path");
		dirPath = dirPath.endsWith("/") ? "" : dirPath.concat("/");	
		String filePath = dirPath.concat(fileName).concat(".zip");
			ZipFile zipFile = new ZipFile(filePath);
			if(zipFile != null) {
				ObjectMapper objMapper=new  ObjectMapper();
				List<ImportIdentifierHolder> validateInfo = new ArrayList<>();
				List<ImportIdentifierHolder> metaInfo = imprt.getMetaInfo();
				if(metaInfo != null){
					for(ImportIdentifierHolder holder : metaInfo) {
						String uuid = holder.getRef().getUuid();
						MetaType metaType = holder.getRef().getType();
						String version = holder.getRef().getVersion();						
						InputStream is =  searchJsonFile(uuid+".json", zipFile);
						if(is != null) {
							BaseEntity baseEntity = (BaseEntity) objMapper.readValue(is, Helper.getDomainClass(metaType));
							boolean present = checkDependency(uuid, version, metaType);
							logger.info("isPresent: "+present);
							if(imprt.getIncludeDep().equalsIgnoreCase("y")) {
								if(metaType.equals(MetaType.simple)) {
									logger.info("Found metaType \"simple\"");
								}
								else {										
									int level = Integer.parseInt(commonServiceImpl.getConfigValue("framework.dependency.level"));
									if(level>0) {
										boolean isPresent = checkDependency(uuid, version, metaType);
										if(!isPresent) {
											boolean result = validateDependency(baseEntity, level, zipFile);
											logger.info("Result: "+result);
												ImportIdentifierHolder importHolder = getImportIdentifierHolder(uuid, version, metaType, ""+result);
												validateInfo.add(importHolder);
										}else {
											logger.info("Object \""+uuid+"\" alReady present in collection.");
											ImportIdentifierHolder importHolder = getImportIdentifierHolder(uuid, version, metaType, "false");
											validateInfo.add(importHolder);
											//sendResponse("422", MessageStatus.FAIL.toString(), "AlREADY present.");
										}
										
									}else {
										logger.info("Base level, level<=0 .");
										boolean isPresent = checkDependency(uuid, version, metaType);
										if(!isPresent) {
											ImportIdentifierHolder importHolder = getImportIdentifierHolder(uuid, version, metaType, "true");
											validateInfo.add(importHolder);
										}else {
											logger.info("Object \""+uuid+"\" alReady present in collection.");
											ImportIdentifierHolder importHolder = getImportIdentifierHolder(uuid, version, metaType, "false");
											validateInfo.add(importHolder);	
											//sendResponse("422", MessageStatus.FAIL.toString(), "AlREADY present.");
										}																
									}					
								}
							}else {
								logger.info("Include dependency = \"Y\" is not checked.");
								boolean isPresent = checkDependency(uuid, version, metaType);
								if(!isPresent) {
									ImportIdentifierHolder importHolder = getImportIdentifierHolder(uuid, version, metaType, "true");
									validateInfo.add(importHolder);
								}else {
									logger.info("Object \""+uuid+"\" alReady present in collection.");
									ImportIdentifierHolder importHolder = getImportIdentifierHolder(uuid, version, metaType, "false");
									validateInfo.add(importHolder);	
									//sendResponse("422", MessageStatus.FAIL.toString(), "AlREADY present.");
								}							
							}							
						}else {
							logger.info("No json file available of the object \""+uuid+" \" inside "+ imprt.getName()+".zip file.");
							ImportIdentifierHolder importHolder = getImportIdentifierHolder(uuid, version, metaType, "false");
							validateInfo.add(importHolder);
							sendResponse("422", MessageStatus.FAIL.toString(), "File corrupted, can not find the included dependencies.");
						}
					}
					imprt.setMetaInfo(validateInfo);
					String result = objMapper.writeValueAsString(imprt);
					return result;
				}else {
					logger.info("metaInfo is null.");
					if(zipFile != null)
						zipFile.close();
					sendResponse("422", MessageStatus.FAIL.toString(), "Invalid zip file.");
					return null;
				}				
			}else {
				logger.info("No such zip file.");
				if(zipFile != null)
					zipFile.close();
				sendResponse("404", MessageStatus.FAIL.toString(), "sorry we could not find the file you requested.");
				return null;
			}			
	}
	public ImportIdentifierHolder getImportIdentifierHolder(String uuid, String version, MetaType metaType, String status) {
		ImportIdentifierHolder importHolder = new ImportIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier(metaType, uuid, version);
		importHolder.setRef(ref);
		importHolder.setStatus(status);
		return importHolder;
	}
}