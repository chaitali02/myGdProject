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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.dao.IActivityDao;
import com.inferyx.framework.dao.ISessionDao;
import com.inferyx.framework.dao.IUserDao;
import com.inferyx.framework.domain.Activity;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Group;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class SessionServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	IUserDao iUserDao;
	@Autowired
	IActivityDao iActivityDao;
	@Autowired
	ISessionDao iSessionDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	DagExecServiceImpl dagExecImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	ActivityServiceImpl activityServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	/*@Autowired
	SessionContext sessionContext;*/
	@Autowired
	RoleServiceImpl roleServiceImpl;
	@Autowired
	PrivilegeServiceImpl privilegeServiceImpl;
	@Autowired
	RegisterService registerService;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(SessionServiceImpl.class);
	
	/*
	 * public HttpSession getSession(HttpServletRequest request) { HttpSession
	 * session = request.getSession(); return session; }
	 */

	/********************** UNUSED **********************/
	/*public Session findLatest() {
		return resolveName(iSessionDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/
	
	/********************** UNUSED **********************/
	/*public Session findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iSessionDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iSessionDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public List<Session> test(String param1) {
		return iSessionDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public Session findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iSessionDao.findOneByUuidAndVersion(appUuid, uuid, version);
		}
		return iSessionDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Session findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iSessionDao.findOneById(appUuid, id);
		}
		return iSessionDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Session session = null;
		if (appUuid != null) {
			session = iSessionDao.findOneById(appUuid, id);
		} else {
			session = iSessionDao.findOne(id);
		}
		session.setActive("N");
		iSessionDao.save(session);
		String ID = session.getId();
		iSessionDao.delete(ID);

	}*/

	/********************** UNUSED **********************/
	/*public Session save(Session session) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		if (session.getAppInfo() == null) {
			MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
			List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
			metaIdentifierHolderList.add(meta);
			session.setAppInfo(metaIdentifierHolderList);
		}
		session.setBaseEntity();
		Session sessionDet = iSessionDao.save(session);
		registerGraph.updateGraph((Object) sessionDet, MetaType.session);
		return sessionDet;
	}*/

	public Session getSessionByUser(String userUUID) {
		return iSessionDao.findSessionByUser(userUUID, new Sort(Sort.Direction.DESC, "version"));
	}

	/********************** UNUSED **********************/
	/*public List<Session> findAllVersion(String datapodName) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iSessionDao.findAllVersion(appUuid, datapodName);
		}
		return iSessionDao.findAllVersion(datapodName);
	}*/

	public Session findSessionByStatus(String status) {
		return iSessionDao.findSessionByStatus(status);
	}

	/*
	 * public Session loginUser(String userId) {
	 * 
	 * List<User> userList=iUserDao.findAll(); List<String> userIDS=new
	 * ArrayList<String>(); for(User user:userList) { String ids=user.getId();
	 * userIDS.add(ids); } User userDO = iUserDao.findOne(userId);
	 * if(userIDS.contains(userId)) { Session
	 * createdSession=createUserSession(userDO.getUuid()); Session
	 * session=iSessionDao.findLatestByUuid(createdSession.getUuid(),new
	 * Sort(Sort.Direction.DESC, "version")); List<Status> statusList = new
	 * ArrayList<Status>(); Status status = new Status(Status.Stage.login, new
	 * Date()); statusList.add(status); session.setStatus(statusList); return
	 * session; } else return null; }
	 */

	public Boolean validateUser(String userName, String password) throws IOException, JSONException, ParseException, ServletException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		// Check if its a valid user
		boolean status = false;
		User userDO = userServiceImpl.findLatestByUsername(userName);
		if (userDO == null) {
			// String msg = "Invalid user";
			// Message message = new Message("", status, msg, "");
			return status;
		}
		
		String username = userDO.getName();
		String userPassword = userDO.getPassword();
		String flag = userDO.getActive();
		
		/*MultiValueMap map = securityServiceImpl.getRolePrivInfo();
		session.setAttribute("mapRolePriv", map);*/

		if (username.equals(userName) && flag.equals("Y") && userPassword.equals(password)) {
			boolean value = createUserSession(username);
			if (value == true) {
				status = true;
				// String msg = "Valid user";
				// Message message = new Message(sessionDO.getId(), status, msg,
				// userDO.getUuid());
				return status;
			}
		} else if (username.equals(userName) && flag.equals("N") && userPassword.equals(password)) {
			// String msg = "Inactive user";
			// Message message = new Message("", status, msg, "");
			return status;
		}else if (username.equals(userName) && flag.equals("Y") && !(userPassword.equals(password))) {
			// String msg = "Invalid password";
			// Message message = new Message("", status, msg, "");
			return status;
		}
		return status;
	}

	
	public Activity logoutSession(String sessionId) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		if(sessionId != null && !StringUtils.isAnyBlank(sessionId)) {
			Session sessionObj = findSessionBySessionId(sessionId);
			if (sessionObj == null) {
				return null;
			}
			//User user = userServiceImpl.findLatestByUuid(sessionObj.getUserInfo().getRef().getUuid());
			User user = (User) commonServiceImpl.getLatestByUuidWithoutAppUuid(sessionObj.getUserInfo().getRef().getUuid(), MetaType.user.toString());
			Activity activityDO = activityServiceImpl.createActivity(user.getUuid());
			activityDO.setName("logout");
			activityDO.setDesc(user.getName() + " logged out successfully");
			MetaIdentifierHolder sessionInfo = getMetaIdentierInstance(MetaType.session, sessionObj.getUuid(), sessionObj.getVersion());
			activityDO.setSessionInfo(sessionInfo);
			//activityDO.setSessionId(sessionObj.getId());
			activityDO.setActive("N");
			/*if (appUuid != null) {
				Application appDO = applicationServiceImpl.findLatestByUuid(appUuid);
				MetaIdentifier appMeta = new MetaIdentifier(MetaType.application, appDO.getUuid(), appDO.getVersion());
				MetaIdentifierHolder appMetaHolder = new MetaIdentifierHolder();
				appMetaHolder.setRef(appMeta);
				List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
				metaIdentifierHolderList.add(appMetaHolder);
				activityDO.setAppInfo(metaIdentifierHolderList);
			}*/
				
			//Session session = iSessionDao.findOneByUuidAndVersion(activityDO.getSessionInfo().getRef().getUuid(),activityDO.getSessionInfo().getRef().getVersion());
			Session session = (Session) commonServiceImpl.getOneByUuidAndVersion(activityDO.getSessionInfo().getRef().getUuid(),activityDO.getSessionInfo().getRef().getVersion(), MetaType.session.toString());
			List<Status> statusList = session.getStatusList();
		
			Status logoutstatus = new Status(com.inferyx.framework.domain.Status.Stage.expired, new Date());
			statusList.add(logoutstatus);
			session.setStatusList(statusList);
			//save(session);
			commonServiceImpl.save(MetaType.session.toString(), session);
			//iActivityDao.save(activityDO);
			commonServiceImpl.save(MetaType.activity.toString(), activityDO);
			// SecurityContextHolder.clearContext();
			// shttpSession.invalidate();
			//httpSession.invalidate();
			return activityDO;
		}else {
			throw new NullPointerException("Session Id is null .....!");
		}
	}

	public MetaIdentifierHolder getMetaIdentierInstance(MetaType type, String uuid, String version) {
		MetaIdentifierHolder holder = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier(type, uuid, version);
		holder.setRef(ref);
		return holder;	
	}

	@SuppressWarnings("unused")
	public boolean createUserSession(String userName) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		User userDO = iUserDao.findLatestByUsername(userName, new Sort(Sort.Direction.DESC, "version"));
		if (userDO != null) {
			// Setting sessionContext object
			SessionContext sessionContext = new SessionContext();
			MetaIdentifier userMeta = new MetaIdentifier(MetaType.user, userDO.getUuid(), userDO.getVersion(),
					userDO.getName());
			MetaIdentifierHolder userInfo = new MetaIdentifierHolder();
			userInfo.setRef(userMeta);
			sessionContext.setUserInfo(userInfo);
	
			// Set default app and role
			sessionContext.setAppInfo(userDO.getAppInfo().get(0));
			List<MetaIdentifierHolder> roleInfoList = new ArrayList<>();
			Group group = (Group) commonServiceImpl.getOneByUuidAndVersion(userDO.getGroupInfo().get(0).getRef().getUuid(), userDO.getGroupInfo().get(0).getRef().getVersion(), MetaType.group.toString(), "N");
			sessionContext.setRoleInfo(group.getRoleId());
	
			//setting orgInfo
			Application application = (Application) commonServiceImpl.getOneByUuidAndVersion(userDO.getAppInfo().get(0).getRef().getUuid(), userDO.getAppInfo().get(0).getRef().getVersion(), MetaType.application.toString(), "N");
			sessionContext.setOrgInfo(application.getOrgInfo());
			
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpSession session = requestAttributes.getRequest().getSession(true);
				
			session.setAttribute("userInfo", userInfo);
			logger.info("Session created: " + session.toString());
			session.setAttribute("sessionContext", sessionContext);
			RequestContextHolder.setRequestAttributes(requestAttributes);

			//Add session
			Session sessionDO = new Session();
			MetaIdentifierHolder mHolder = new MetaIdentifierHolder();
			MetaIdentifier mIdentifier = new MetaIdentifier();
			mIdentifier.setType(MetaType.user);
			mIdentifier.setUuid(userDO.getUuid());
			mIdentifier.setVersion(userDO.getVersion());
			mIdentifier.setName(userDO.getName());
			mHolder.setRef(mIdentifier);
			sessionDO.setBaseEntity();
			if (sessionDO.getCreatedBy() == null) {
				MetaIdentifierHolder createdByMeta = new MetaIdentifierHolder();
				MetaIdentifier createdByInfo = new MetaIdentifier(MetaType.user, userDO.getUuid(), userDO.getVersion());
				createdByMeta.setRef(createdByInfo);
				sessionDO.setCreatedBy(createdByMeta);
			}
			sessionDO.setName("sys_" + sessionDO.getUuid());
			sessionDO.setUserInfo(mHolder);
			List<Status> statusList = new ArrayList<Status>();
			Status logoutstatus = new Status(Status.Stage.active, new Date());
			statusList.add(logoutstatus);
			sessionDO.setStatusList(statusList);
			sessionDO.setSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
			if(requestAttributes != null) {
				HttpServletRequest request = requestAttributes.getRequest();
				if(request != null) {
					String ipAddress = getIpAddress(request);
					sessionDO.setIpAddress(ipAddress);
				}else
					logger.info("HttpServletResponse response is \""+null+"\"");
			}else
				logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");	
			//Session savedSession = save(sessionDO);
			commonServiceImpl.save(MetaType.session.toString(), sessionDO);
			
			MetaIdentifier sessionMeta = new MetaIdentifier(MetaType.session, sessionDO.getUuid(), sessionDO.getVersion());
			MetaIdentifierHolder sessionInfo = new MetaIdentifierHolder();
			sessionInfo.setRef(sessionMeta);
			sessionContext.setSessionInfo(sessionInfo);
			logger.info(new ObjectMapper().writeValueAsString(sessionContext));

			//Add activity
			Activity activityDO = activityServiceImpl.createActivity(userDO.getUuid());
			MetaIdentifierHolder appInfo = securityServiceImpl.getAppInfo();
			if (appInfo != null) {
				List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
				metaIdentifierHolderList.add(appInfo);
				activityDO.setAppInfo(metaIdentifierHolderList);
			}
			
			activityDO.setSessionInfo(sessionInfo);
			//iActivityDao.save(activityDO);
			commonServiceImpl.save(MetaType.activity.toString(), activityDO);
			//logger.info("\n\n\n\n\n\tNOW WE ARE USING USER SESSION.\n\n");
			return true;
		} else {
			return false;

		}
	}
	public String getIpAddress(HttpServletRequest request) throws UnknownHostException {
		String ipAddress = request.getHeader("X-FORWARDED-FOR"); 
	    if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {  
	    	ipAddress = request.getHeader("Proxy-Client-IP");  
	    }  
	    if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {  
	        ipAddress = request.getHeader("WL-Proxy-Client-IP");  
	    }  
	    if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {  
	         ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");  
	    }  
	    if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {  
	        ipAddress = request.getHeader("HTTP_X_FORWARDED");  
	    }  
	    if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {  
	        ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");  
	    }  
	    if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {  
	        ipAddress = request.getHeader("HTTP_CLIENT_IP");  
	    }  
	    if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {  
	        ipAddress = request.getHeader("HTTP_FORWARDED_FOR");  
	    }  
	    if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {  
	        ipAddress = request.getHeader("HTTP_FORWARDED");  
	    }  
	    if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {  
	        ipAddress = request.getHeader("HTTP_VIA");  
	    }  
	    if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {  
	        ipAddress = request.getHeader("REMOTE_ADDR");  
	    }  
	    if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {  
	        ipAddress = request.getRemoteAddr();  
	    }
	    if(ipAddress.contains(",")) 
	    	ipAddress = ipAddress.split(",")[0] ;
	    if (ipAddress.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
	        InetAddress inetAddress = InetAddress.getLocalHost();
	        ipAddress = inetAddress.getHostAddress();
	        //System.out.println("inetAddress.getHostAddress(): "+ipAddress+"\n");
	    }
	    return ipAddress;
	}
	
	/********************** UNUSED **********************/
	/*public List<Session> findAllLatestActive() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Aggregation sessionAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Session> sessionResults = mongoTemplate.aggregate(sessionAggr, "session", Session.class);
		List<Session> sessionList = sessionResults.getMappedResults();

		// Fetch the session details for each id
		List<Session> result = new ArrayList<Session>();
		for (Session s : sessionList) {
			Session sessionLatest;
			if (appUuid != null) {
				sessionLatest = iSessionDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			} else {
				sessionLatest = iSessionDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
			}
			if (sessionLatest != null) {
				result.add(sessionLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public Session getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iSessionDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iSessionDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}*/
	
	public Session findSessionBySessionId(String sessionId) {
		String sId = sessionId.replace("\"", "");
		Session session = iSessionDao.findSessionBySessionId(sId);
		return session;
	}

	/*public MetaIdentifierHolder saveAs(Session session) throws IOException{
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Session sessionNew = new Session();
		sessionNew.setName(session.getName()+"_copy");
		sessionNew.setActive(session.getActive());		
		sessionNew.setDesc(session.getDesc());		
		sessionNew.setTags(session.getTags());	
		sessionNew.setRoleInfo(session.getRoleInfo());
		sessionNew.setStatus(session.getStatus());
		sessionNew.setUserInfo(session.getUserInfo());
		Save(sessionNew);
		ref.setType(MetaType.session);
		ref.setUuid(sessionNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> sessionList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity session : sessionList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = session.getId();
			String uuid = session.getUuid();
			String version = session.getVersion();
			String name = session.getName();
			String desc = session.getDesc();
			String published=session.getPublished();
			MetaIdentifierHolder createdBy = session.getCreatedBy();
			String createdOn = session.getCreatedOn();
			String[] tags = session.getTags();
			String active = session.getActive();
			List<MetaIdentifierHolder> appInfo = session.getAppInfo();
			baseEntity.setId(id);
			baseEntity.setUuid(uuid);
			baseEntity.setVersion(version);
			baseEntity.setName(name);
			baseEntity.setDesc(desc);
			baseEntity.setCreatedBy(createdBy);
			baseEntity.setCreatedOn(createdOn);
			baseEntity.setPublished(published);
			baseEntity.setTags(tags);
			baseEntity.setActive(active);
			baseEntity.setAppInfo(appInfo);
			baseEntityList.add(baseEntity);
		}
		return baseEntityList;
	}*/
}
