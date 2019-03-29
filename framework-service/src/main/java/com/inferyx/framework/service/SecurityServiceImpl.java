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
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.ISessionDao;
import com.inferyx.framework.dao.IUserDao;
import com.inferyx.framework.domain.AppRole;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.Group;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Role;
import com.inferyx.framework.domain.RolePriv;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.enums.ApplicationType;
import com.inferyx.framework.register.GraphRegister;


@Service
public class SecurityServiceImpl  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9188483510205558602L;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RoleServiceImpl roleServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SessionContext sessionContext;
	@Autowired
	SessionServiceImpl sessionServiceImpl;
	@Autowired
	ActivityServiceImpl activityServiceImpl;
//	@Autowired
//	private SessionRegistry sessionRegistry;
	@Autowired
	IUserDao iUserDao;
	@Autowired
	ISessionDao iSessionDao;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	@Autowired
	PrivilegeServiceImpl privilegeServiceImpl;
	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	public CommonServiceImpl<?> commonServiceImpl;
	
	
	static final Logger logger = Logger.getLogger(SecurityServiceImpl.class);
	
	public SessionContext setAppRole(String appUUID, String roleUUID, String sessionId) throws JSONException,
			ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException {

		Application appDO = (Application) commonServiceImpl.getLatestByUuid(appUUID, MetaType.application.toString(),
				"N");
		Role roleDO = (Role) commonServiceImpl.getLatestByUuid(roleUUID, MetaType.role.toString(), "N");
		MetaIdentifierHolder app = new MetaIdentifierHolder(new MetaIdentifier(MetaType.application, appDO.getUuid(), appDO.getVersion()));
		MetaIdentifierHolder role = new MetaIdentifierHolder(new MetaIdentifier(MetaType.role, roleDO.getUuid(), roleDO.getVersion()));
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session = requestAttributes.getRequest().getSession(false);
		// logger.info("setAppRole: session: " + session.toString());
		SessionContext sessionContext = (SessionContext) session.getAttribute("sessionContext");
		session.setAttribute("sessionId", sessionId);
		if (sessionContext != null) {
			sessionContext.setAppInfo(app);
			sessionContext.setRoleInfo(role);
			setPrivInfo();
			FrameworkThreadLocal.getSessionContext().set(sessionContext);
			// session.setAttribute("mapRolePriv", map);
			// logger.info("setAppRole: sessionContext: " + sessionContext.toString());
		} else {
			logger.info("Null Session context.");
		}
		session.setAttribute("sessionContext", sessionContext);
		// String roleType = roleDO.getName();
		// if (sessionContext != null) {
		// if (roleType.equals("admin")) {
		// sessionContext.setAppInfo(null);
		// } else {
		// sessionContext.setAppInfo(app);
		// }
		// sessionContext.setRoleInfo(role);
		// }

		// Added by YP. Updating session object with app and role.
		Session sessionDO = sessionServiceImpl.findSessionBySessionId(sessionId);
		List<MetaIdentifierHolder> appList = new ArrayList<MetaIdentifierHolder>();
		appList.add(app);
		sessionDO.setAppInfo(appList);
		sessionDO.setRoleInfo(role);
		try {
			// sessionServiceImpl.save(sessionDO);
			commonServiceImpl.save(MetaType.session.toString(), sessionDO);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Added by YP

		/*
		 * String sessionUuid = sessionContext.getSessionInfo().getRef().getUuid();
		 * Session userSession = sessionServiceImpl.findLatestByUuid(sessionUuid);
		 */

		/*
		 * List<MetaIdentifierHolder> appList = new ArrayList<MetaIdentifierHolder>();
		 * appList.add(sessionContext.getAppInfo()); userSession.setAppInfo(appList);
		 * sessionServiceImpl.Save(userSession);
		 */
		// session.setAttribute("sessionContext", sessionContext);

		/*
		 * ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
		 * RequestContextHolder.getRequestAttributes();
		 * 
		 * httpSession = requestAttributes.getRequest().getSession(true); SessionContext
		 * sessionContext = (SessionContext)
		 * httpSession.getAttribute("scopedTarget.sessionContext");
		 * sessionContext.setAppInfo(app); sessionContext.setRoleInfo(role);
		 * httpSession.setAttribute("scopedTarget.sessionContext", sessionContext);
		 */
		// RequestContextHolder.getRequestAttributes().setAttribute("scopedTarget.sessionContext",
		// sessionContext, RequestAttributes.SCOPE_SESSION);
		// System.out.println("*******rSessionAttr***********"+RequestContextHolder.getRequestAttributes().getAttribute("scopedTarget.sessionContext",RequestAttributes.SCOPE_SESSION));
		return sessionContext;
	}

	

	public MetaIdentifierHolder getAppInfo() {
		MetaIdentifierHolder appInfo = new MetaIdentifierHolder();	
		SessionContext sessionContext = null;
		try {
			//if(sessionContext != null) {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
				HttpSession session = requestAttributes.getRequest().getSession(false);
				if (session == null) 
					sessionContext = FrameworkThreadLocal.getSessionContext().get();
				else 
					sessionContext = (SessionContext) session.getAttribute("sessionContext");
			//}else				
				appInfo = sessionContext.getAppInfo();
		} catch (Exception e) {
			sessionContext = FrameworkThreadLocal.getSessionContext().get();
			if(sessionContext != null) {			
				//logger.info("Setting appInfo using class  FrameworkThreadLocal !");
				appInfo = sessionContext.getAppInfo();
			} else {
				logger.info("Null Session context. Unable to get appInfo.");
//				Exception excp = new Exception("Null Session context. Unable to get appInfo.", e);
				throw e;
				/*MetaIdentifier appMeta = new MetaIdentifier(MetaType.application,"d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd","1547482049");
				appInfo.setRef(appMeta);*/
			}
			//e.printStackTrace();
		}
		return appInfo;
	}
	
	public MetaIdentifierHolder getOrgInfo() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException, IOException {
		SessionContext sessionContext = null;
		try {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpSession session = requestAttributes.getRequest().getSession(false);
			if(session == null)
				sessionContext = FrameworkThreadLocal.getSessionContext().get();
			else
				sessionContext = (SessionContext) session.getAttribute("sessionContext");
			
			return sessionContext.getOrgInfo();	
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.organization, null, null));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "No organization information available.\n"+e.toString(), dependsOn);
			throw new RuntimeException("No organization information available.");	
		}	
	}	
		
	
	
	public MetaIdentifierHolder getRoleInfo() {
		MetaIdentifierHolder roleInfo = new MetaIdentifierHolder();		
		SessionContext sessionContext = null;
		try {
			//if(sessionContext != null) {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
				HttpSession session = requestAttributes.getRequest().getSession(false);
				if (session == null) 
					sessionContext = FrameworkThreadLocal.getSessionContext().get();
				else 
					sessionContext = (SessionContext) session.getAttribute("sessionContext");
			//}else
				roleInfo = sessionContext.getRoleInfo();				
		} catch (Exception e) {
			sessionContext = FrameworkThreadLocal.getSessionContext().get();
			if(sessionContext != null) {			
				//logger.info("Setting roleInfo using class  FrameworkThreadLocal !");
				roleInfo =  sessionContext.getRoleInfo();
			} else {
				throw new RuntimeException("No role information available.");
			}
		}		
		return roleInfo;		
	}
	
	public MetaIdentifierHolder getuserInfo() {
		MetaIdentifierHolder userInfo = new MetaIdentifierHolder();	
		SessionContext sessionContext = null;
		try {
			//if(sessionContext != null) {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
				HttpSession session = requestAttributes.getRequest().getSession(false);
				if (session == null) 
					sessionContext = FrameworkThreadLocal.getSessionContext().get();
				else 
					sessionContext = (SessionContext) session.getAttribute("sessionContext");
			//}else
				userInfo = sessionContext.getUserInfo();							
		} catch (Exception e) {
			sessionContext = FrameworkThreadLocal.getSessionContext().get();
			if(sessionContext != null) {			
				//logger.info("Setting userInfo using class  FrameworkThreadLocal !");
				userInfo = sessionContext.getUserInfo();	
			} else {
				throw new RuntimeException("No user information available.");
			}
		}
		return userInfo;		
	}
	
	public MetaIdentifierHolder getSessionInfo() {
		MetaIdentifierHolder sessionInfo = new MetaIdentifierHolder();	
		SessionContext sessionContext = null;	
		try {
			//if(sessionContext != null) {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
				HttpSession session = requestAttributes.getRequest().getSession(false);
				if (session == null) 
					sessionContext = FrameworkThreadLocal.getSessionContext().get();
				else 
					sessionContext = (SessionContext) session.getAttribute("sessionContext");
			//}else
				sessionInfo = sessionContext.getSessionInfo();										
		} catch (Exception e) {
			sessionContext = FrameworkThreadLocal.getSessionContext().get();
			if(sessionContext != null) {			
				//logger.info("Setting sessionInfo using class  FrameworkThreadLocal !");
				sessionInfo = sessionContext.getSessionInfo();	
			} else {
				logger.info("Null Session context.");	
				sessionInfo=null;
				throw new RuntimeException("No session information available.");
			}
		}
		return sessionInfo;		
	}
	
	

	@SuppressWarnings("unused")
	public Boolean UnlockUser(String userName, String password) throws JsonProcessingException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		User user = iUserDao.findLatestByUsername(userName, new Sort(Sort.Direction.DESC, "version"));
		
		MetaIdentifier ref = new MetaIdentifier(null, user.getUuid(), user.getVersion(), user.getName());
		MetaIdentifierHolder activityInfo = new MetaIdentifierHolder(ref);
		//boolean status = true;
		if (user == null) {
			/*String msg = "Invalid user";
			status = false;*/
			Message message = new Message(activityInfo, "404", MessageStatus.FAIL.toString(), "Invalid user.");
			messageServiceImpl.save(message);
			logger.info("User not found.");
			return false;
		}
		String username = user.getName();
		String userPassword = user.getPassword();
		String flag = user.getActive();
	
		/*ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes();
		HttpSession session = requestAttributes.getRequest().getSession();
		sessionContext = (SessionContext) session.getAttribute("sessionContext");
		String sessionUuid = sessionContext.getSessionInfo().getRef().getUuid();
		Session sessionDO = null;
		if(sessionUuid != null)
		{		
		 sessionDO = iSessionDao.findLatestByUuid(sessionUuid, new Sort(Sort.Direction.DESC, "version"));
		}*/
		if (username.equals(userName) && flag.equals("Y") && userPassword.equals(password) ) {
			//Session sessionDO = iSessionDao.findSessionByUser(user.getUuid(), new Sort(Sort.Direction.DESC, "version"));			
			//String msg = "Valid user";
			Message message = new Message(activityInfo, "200", MessageStatus.SUCCESS.toString(), "Valid user."+user.getName());
			messageServiceImpl.save(message);
			return true;
						
		}		
		if (username.equals(userName) && flag.equals("N") && userPassword.equals(password)) {
			//String msg = "Inactive user";
			//status = false;
			Message message = new Message(activityInfo, "404", MessageStatus.FAIL.toString(), "Inactive user.");
			
			messageServiceImpl.save(message);
			return false;
		}
		if (username.equals(userName) && flag.equals("Y") && !(userPassword.equals(password))) {
			//String msg = "Invalid password";
			//status = false;
			Message message = new Message(activityInfo, "404", MessageStatus.FAIL.toString(), "Invalid password.");
			messageServiceImpl.save(message);
			return false;
		} else
			return false;
	}
	
	public String setActivity(String uuid, String version, String type, String action) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		  com.inferyx.framework.domain.Activity activity = new  com.inferyx.framework.domain.Activity();	
		  MetaIdentifierHolder appInfo=getAppInfo();
		  MetaIdentifierHolder sessionInfo=getSessionInfo();
		  if(sessionInfo !=null){
			  String sessionuuid=sessionInfo.getRef().getUuid();
			  //Session session=sessionServiceImpl.findLatestByUuid(sessionuuid);
			  Session session = (Session) commonServiceImpl.getLatestByUuid(sessionuuid, MetaType.session.toString());
			  MetaIdentifierHolder activitySessionInfo = getMetaIdentierInstance(MetaType.session, session.getUuid(), session.getVersion());
			  activity.setSessionInfo(activitySessionInfo);
		  }
		  List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		  metaIdentifierHolderList.add(appInfo);
		  activity.setAppInfo(metaIdentifierHolderList);
          activity.setBaseEntity();
          activity.setUserInfo(activity.getCreatedBy());
          activity.setPublished(activity.getPublished());
          activity.setName(type+"-"+action);
          activity.setStatus("Success");
          MetaIdentifier metaInfoRef= new MetaIdentifier();
          MetaIdentifierHolder metaIdentifireHoler=new MetaIdentifierHolder();
          MetaType metatype=Helper.getMetaType(type);
          metaInfoRef.setType(metatype);
          metaInfoRef.setUuid(uuid);
          metaInfoRef.setVersion(version);
          metaIdentifireHoler.setRef(metaInfoRef);
          activity.setMetaInfo(metaIdentifireHoler);
          BaseEntity activityBaseEntity = (BaseEntity) commonServiceImpl.save(MetaType.activity.toString(), activity);
        //  registerGraph.updateGraph(activityBaseEntity, metatype);
          return activityBaseEntity.getId();
    }
	public MetaIdentifierHolder getMetaIdentierInstance(MetaType type, String uuid, String version) {
		MetaIdentifierHolder holder = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier(type, uuid, version);
		holder.setRef(ref);
		return holder;	
	}
	public MultiValueMap getPrivInfo(){
		MultiValueMap privInfo = new MultiValueMap();
		SessionContext sessionContext = null;
		/*MetaIdentifierHolder roleInfo = getRoleInfo();		
		String uuid = roleInfo.getRef().getUuid();
		List<RolePriv> rolePrivList = privilegeServiceImpl.getRolePriv(uuid);
		
		
		for(RolePriv rolePriv : rolePrivList) {
			String type = rolePriv.getType();
			List<String> roles = (List<String>) rolePriv.getPrivInfo();
			if(roles != null)
				map.put(type.toLowerCase(), roles);
		}*/
		try {
			//if(sessionContext != null) {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
				HttpSession session = requestAttributes.getRequest().getSession(false);
				if (session == null) 
					sessionContext = FrameworkThreadLocal.getSessionContext().get();
				else 
					sessionContext = (SessionContext) session.getAttribute("sessionContext");
			//}else
				privInfo = sessionContext.getPrivInfo();										
		} catch (Exception e) {
			sessionContext = FrameworkThreadLocal.getSessionContext().get();
			if(sessionContext != null) {			
				//logger.info("Setting privInfo using class  FrameworkThreadLocal !");
				privInfo = sessionContext.getPrivInfo();
			} else {
				logger.info("Null Session context.");	
				privInfo=null;
				throw new RuntimeException("No priviledge information available.");
			}
		}
		//logger.info(map.entrySet());
		return privInfo;
	}
	@SuppressWarnings("unused")
	public String getAppRole(String userName) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException{
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		User user = userServiceImpl.findUserByName(userName);
		user = (User) commonServiceImpl.getLatestByUuid(user.getUuid(),MetaType.user.toString(), "N");
		Group defaultGroup = (Group) commonServiceImpl.getLatestByUuidWithoutAppUuid(user.getDefaultGroup().getRef().getUuid(), user.getDefaultGroup().getRef().getType().toString());
		if(user != null) {
			//List<AppRole> appRoleList = new ArrayList<>();
			List<MetaIdentifierHolder> holderList = new ArrayList<>();
			List<MetaIdentifierHolder> groupInfoList = user.getGroupInfo();
			if(groupInfoList != null && !groupInfoList.isEmpty()) {
				ConcurrentHashMap<String, List<MetaIdentifierHolder>> rawList = new ConcurrentHashMap<>();
				for(MetaIdentifierHolder groupInfo : groupInfoList) {
					String groupUuid = groupInfo.getRef().getUuid();
					//String groupVersion = groupInfo.getRef().getVersion();
					MetaType type = groupInfo.getRef().getType();
					Group group = (Group) commonServiceImpl.getLatestByUuidWithoutAppUuid(groupUuid, type.toString());
					List<MetaIdentifierHolder> roleList = new ArrayList<>();
					roleList.add(group.getRoleId());
					List<MetaIdentifierHolder> list = rawList.putIfAbsent(group.getAppId().getRef().getUuid(), roleList);
					if(list != null) {
						List<MetaIdentifierHolder> oldList = rawList.get(group.getAppId().getRef().getUuid());
						if(oldList == null) {
							logger.info("empty old list: ");
						}else {
							oldList = resolveList(oldList, roleList);
						}
						rawList.put(group.getAppId().getRef().getUuid(), oldList);
					}
					holderList.add(group.getAppId());
				}
				return ow.writeValueAsString(resolveAppVsRole(rawList, holderList,defaultGroup.getAppId()));
			} else {
				logger.info("No group informaion available, groupInfo is empty/null.");
				throw new RuntimeException("No app role information available.");
			}
		} else {
			logger.info("User object null."); 
			throw new RuntimeException("No app role information available.");
		}
	}
	
	public void setPrivInfo() throws JsonProcessingException {
		MultiValueMap privInfo = new MultiValueMap();
		MetaIdentifierHolder roleInfo = getRoleInfo();		
		String uuid = roleInfo.getRef().getUuid();
		List<RolePriv> rolePrivList = privilegeServiceImpl.getRolePriv(uuid);		
		
		for(RolePriv rolePriv : rolePrivList) {
			String type = rolePriv.getType();
			@SuppressWarnings("unchecked")
			List<String> roles = (List<String>) rolePriv.getPrivInfo();
			if(roles != null)
				privInfo.put(type.toLowerCase(), roles);
		}
		sessionContext.setPrivInfo(privInfo);
	}
	
	public List<AppRole> resolveAppVsRole(ConcurrentHashMap<String, List<MetaIdentifierHolder>> rawList, List<MetaIdentifierHolder> aholderList, MetaIdentifierHolder defaultAppId) throws JsonProcessingException{
		List<AppRole> resolvedAppRoleList = new ArrayList<>();
		MetaIdentifierHolder appInfo = new MetaIdentifierHolder();
		ApplicationType applicationType = null;
		for(Entry<String, List<MetaIdentifierHolder>> entry : rawList.entrySet()) {
			boolean isDefault = false;
			for(MetaIdentifierHolder ref : aholderList) {
				if(ref.getRef().getUuid().equalsIgnoreCase(entry.getKey())) {
					appInfo = ref;
					Application application=(Application)commonServiceImpl.getLatestByUuid(appInfo.getRef().getUuid(),appInfo.getRef().getType().toString(),"N");
					applicationType=application.getApplicationType();
					if(ref.getRef().getUuid().equalsIgnoreCase(defaultAppId.getRef().getUuid())) {
						isDefault = true;
					}
				}
			}
			AppRole appRole = null;
			if(isDefault) {
				appRole = new AppRole(appInfo, entry.getValue(), defaultAppId,applicationType);
			} else {
				appRole = new AppRole(appInfo, entry.getValue(), null,applicationType);
			}			
			resolvedAppRoleList.add(appRole);
		}
		return resolvedAppRoleList;
	}
	
	public List<MetaIdentifierHolder> resolveList(List<MetaIdentifierHolder> oldList, List<MetaIdentifierHolder> newList){
		List<MetaIdentifierHolder> roleInfoList = new ArrayList<>();
		if(oldList != null && (oldList.size() > 0))
			for(MetaIdentifierHolder olderHolder : oldList) {
				if(newList != null && (newList.size() > 0)) {
					for(MetaIdentifierHolder newHolder : newList) {
						if(!olderHolder.getRef().getUuid().equalsIgnoreCase(newHolder.getRef().getUuid())) {
							roleInfoList.add(newHolder);
						}
					}
					roleInfoList.add(olderHolder);
				}
				else
					roleInfoList = oldList;
			}else
				roleInfoList = newList;
		return roleInfoList;		
	}

	
}