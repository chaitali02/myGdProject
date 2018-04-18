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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.inferyx.framework.dao.IRoleDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Privilege;
import com.inferyx.framework.domain.Role;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class RoleServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IRoleDao iRoleDao;
	@Autowired
	MongoTemplate mongoTemplate;
	/*@Autowired
	private UserServiceImpl userServiceImpl;*/
	/*@Autowired
	private PrivilegeServiceImpl privilegeServiceImpl;*/
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	RegisterService registerService;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(RoleServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Role findLatest() {
		return resolveName(iRoleDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/
	/********************** UNUSED **********************/
	/*public Role findAllByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRoleDao.findAllByUuid(appUuid, uuid);
		} else
			return iRoleDao.findAllByUuid(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public Role findLatestByUuid(String uuid) {
		// String appUuid = (securityServiceImpl.getAppInfo() != null &&
		// securityServiceImpl.getAppInfo().getRef() != null
		// )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		// if(appUuid != null)
		// {
		// return iRoleDao.findLatestByUuid(appUuid,uuid,new
		// Sort(Sort.Direction.DESC, "version"));
		// }
		// else
		return iRoleDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public Role findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRoleDao.findOneByUuidAndVersion(appUuid, uuid, version);
		}
		return iRoleDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Role findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRoleDao.findOneById(appUuid, id);
		}
		return iRoleDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public List<Role> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iRoleDao.findAll();
		}
		return iRoleDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Role role = null;
		if (appUuid != null) {
			role = iRoleDao.findOneById(appUuid, id);
		}
		role = iRoleDao.findOne(id);
		role.setActive("N");
		iRoleDao.save(role);
//		String ID = role.getId();
//		iRoleDao.delete(ID);
	}*/

	/********************** UNUSED **********************/
	/*public Role save(Role role) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		role.setAppInfo(metaIdentifierHolderList);
		role.setBaseEntity();
		Role roleDet = iRoleDao.save(role);
		registerGraph.updateGraph((Object) roleDet, MetaType.role);
		return roleDet;
	}*/

	/********************** UNUSED **********************/
	/*public List<Role> findAllVersion(String roleName) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRoleDao.findAllVersion(appUuid, roleName);
		}
		return iRoleDao.findAllVersion(roleName);
	}*/

	/********************** UNUSED **********************/
	/*public List<Role> findAllLatest() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;

		Aggregation roleAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<Role> roleResults = mongoTemplate.aggregate(roleAggr,"role", Role.class);
		List<Role> roleList = roleResults.getMappedResults();
		List<Role> result = new ArrayList<Role>();
		for (Role r : roleList) {
			Role roleLatest = null;
			if (appUuid != null) {
				roleLatest = iRoleDao.findOneByUuidAndVersion(appUuid, r.getId(), r.getVersion());
			} else {
				roleLatest = iRoleDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if (roleLatest != null) {
				result.add(roleLatest);
			}
		}
		return result;

	}*/

	/********************** UNUSED **********************/
	/*public List<Role> findAllLatestActive() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Aggregation roleAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Role> roleResults = mongoTemplate.aggregate(roleAggr, "role", Role.class);
		List<Role> roleList = roleResults.getMappedResults();

		// Fetch the role details for each id
		List<Role> result = new ArrayList<Role>();
		for (Role r : roleList) {
			Role roleLatest = null;
			if (appUuid != null) {
				roleLatest = iRoleDao.findOneByUuidAndVersion(appUuid, r.getId(), r.getVersion());
			} else {
				roleLatest = iRoleDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if (roleLatest != null) {
				result.add(roleLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
/*	public Role resolveName(Role role) {
		if (role.getCreatedBy() != null) {
			String createdByRefUuid = role.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			role.getCreatedBy().getRef().setName(user.getName());
		}
		if (role.getAppInfo() != null) {
			for (int i = 0; i < role.getAppInfo().size(); i++) {
				String appUuid = role.getAppInfo().get(i).getRef().getUuid();
				Application application = applicationServiceImpl.findLatestByUuid(appUuid);
				String appName = application.getName();
				role.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		List<MetaIdentifierHolder> privInfo = role.getPrivilegeInfo();
		for (int i = 0; i < privInfo.size(); i++) {
			MetaIdentifier privRef = privInfo.get(i).getRef();
			MetaType type = privRef.getType();
			if (type.toString().equalsIgnoreCase(MetaType.role.toString())) {
				Role roleDO = findLatestByUuid(privRef.getUuid());
				role.getPrivilegeInfo().get(i).getRef().setName(roleDO.getName());
			}
			if (type.toString().equalsIgnoreCase(MetaType.privilege.toString())) {
				Privilege privDO = privilegeServiceImpl.findLatestByUuid(privRef.getUuid());
				role.getPrivilegeInfo().get(i).getRef().setName(privDO.getName());
			}
		}
		return role;
	}*/

	/********************** UNUSED **********************/
	/*public List<Role> resolveName(List<Role> role) {
		List<Role> roleList = new ArrayList<>();
		for (Role r : role) {
			String createdByRefUuid = r.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			r.getCreatedBy().getRef().setName(user.getName());
			roleList.add(r);
		}
		return roleList;
	}*/

	/********************** UNUSED **********************/
	/*public List<Role> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRoleDao.findAllVersion(appUuid, uuid);
		}
		return iRoleDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public Role getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRoleDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		}
		return iRoleDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Role role) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();
		Role roleNew = new Role();
		roleNew.setName(role.getName() + "_copy");
		roleNew.setActive(role.getActive());
		roleNew.setDesc(role.getDesc());
		roleNew.setTags(role.getTags());
		roleNew.setPrivilgeInfo(role.getPrivilegeInfo());
		save(roleNew);
		ref.setType(MetaType.role);
		ref.setUuid(roleNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> roleList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity role : roleList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = role.getId();
			String uuid = role.getUuid();
			String version = role.getVersion();
			String name = role.getName();
			String desc = role.getDesc();
			MetaIdentifierHolder createdBy = role.getCreatedBy();
			String createdOn = role.getCreatedOn();
			String[] tags = role.getTags();
			String active = role.getActive();
			String published=role.getPublished();
			List<MetaIdentifierHolder> appInfo = role.getAppInfo();
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