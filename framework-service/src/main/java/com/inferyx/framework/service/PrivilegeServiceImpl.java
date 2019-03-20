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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Meta;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Privilege;
import com.inferyx.framework.domain.Role;
import com.inferyx.framework.domain.RolePriv;
import com.inferyx.framework.register.GraphRegister;

@Service
public class PrivilegeServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	RoleServiceImpl roleServiceImpl;
	@Autowired
	MetadataServiceImpl metadataServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(PrivilegeServiceImpl.class);
    
	/********************** UNUSED **********************/
	/*public Privilege findLatest() {
		return resolveName(iPrivilegeDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/


	/********************** UNUSED **********************/
	/*public Privilege findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iPrivilegeDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iPrivilegeDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public Privilege findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iPrivilegeDao.findOneByUuidAndVersion(appUuid, uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Privilege findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iPrivilegeDao.findOneById(appUuid, id);
		}
		return iPrivilegeDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Privilege privilege = iPrivilegeDao.findOneById(appUuid, id);
		privilege.setActive("N");
		iPrivilegeDao.save(privilege);
//		String ID = privilege.getId();
//		iPrivilegeDao.delete(ID);
	}*/

	/********************** UNUSED **********************/
	/*public Privilege save(Privilege privilege) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		privilege.setAppInfo(metaIdentifierHolderList);
		privilege.setBaseEntity();
		Privilege privilegeDet = iPrivilegeDao.save(privilege);
		registerGraph.updateGraph((Object) privilegeDet, MetaType.privilege);
		return privilegeDet;
	}*/

	/********************** UNUSED **********************/
	/*public List<Privilege> findAllVersion(String privilegeName) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iPrivilegeDao.findAllVersion(appUuid, privilegeName);
	}*/

	/********************** UNUSED **********************/
	/*public List<Privilege> findAllLatestActive() {
		Aggregation privilegeAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Privilege> privilegeResults = mongoTemplate.aggregate(privilegeAggr, "privilege",
				Privilege.class);
		List<Privilege> privilegeList = privilegeResults.getMappedResults();

		// Fetch the privilege details for each id
		List<Privilege> result = new ArrayList<Privilege>();
		for (Privilege p : privilegeList) {
			Privilege privilegeLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				privilegeLatest = iPrivilegeDao.findOneByUuidAndVersion(appUuid, p.getId(), p.getVersion());
			} else {
				privilegeLatest = iPrivilegeDao.findOneByUuidAndVersion(p.getId(), p.getVersion());
			}
			if (privilegeLatest != null) {
				result.add(privilegeLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public Privilege getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iPrivilegeDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iPrivilegeDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Privilege privilege) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();
		Privilege privNew = new Privilege();
		privNew.setName(privilege.getName() + "_copy");
		privNew.setActive(privilege.getActive());
		privNew.setDesc(privilege.getDesc());
		privNew.setTags(privilege.getTags());
		privNew.setPrivType(privilege.getPrivType());
		privNew.setMetaId(privilege.getMetaId());
		save(privNew);
		ref.setType(MetaType.privilege);
		ref.setUuid(privNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> privilegeList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for (BaseEntity privilege : privilegeList) {
			BaseEntity baseEntity = new BaseEntity();
			String id = privilege.getId();
			String uuid = privilege.getUuid();
			String version = privilege.getVersion();
			String name = privilege.getName();
			String desc = privilege.getDesc();
			String published=privilege.getPublished();
			MetaIdentifierHolder createdBy = privilege.getCreatedBy();
			String createdOn = privilege.getCreatedOn();
			String[] tags = privilege.getTags();
			String active = privilege.getActive();
			List<MetaIdentifierHolder> appInfo = privilege.getAppInfo();
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

	public List<RolePriv> getRolePriv(String roleUuid) throws JsonProcessingException {
//		List<RolePriv> userPrivList = new ArrayList<RolePriv>();
		//Role role = roleServiceImpl.findLatestByUuid(roleUuid);
		Role role = (Role) commonServiceImpl.getLatestByUuid(roleUuid, MetaType.role.toString(), "N");
		List<MetaIdentifierHolder> privilegeInfo = role.getPrivilegeInfo();	
		
		return getRolePrivByMIHolder(privilegeInfo);
		
//		MultiValueMap rulePrivMap = new MultiValueMap();
//		for (int i = 0; i < privilegeInfo.size(); i++) {
//			//Privilege privilege = findLatestByUuid(privilegeInfo.get(i).getRef().getUuid());
//			Privilege privilege = (Privilege) commonServiceImpl.getLatestByUuid(privilegeInfo.get(i).getRef().getUuid(), MetaType.privilege.toString(), "N");
//			String uuid = privilege.getMetaId().getRef().getUuid();
//			//Meta meta = metadataServiceImpl.findLatestByUuid(uuid);
//			Meta meta = (Meta) commonServiceImpl.getLatestByUuid(uuid, MetaType.meta.toString(),"N");
//			
//			if(meta == null) {
//				continue;
//			}
//
//			try{
//				if(rulePrivMap.isEmpty()) {
//					rulePrivMap.put(meta.getName(), privilege.getPrivType());
//				} else {
//					if(!rulePrivMap.containsKey(meta.getName())) {
//						rulePrivMap.put(meta.getName(), privilege.getPrivType());
//					} else {
//						Collection<?> values = rulePrivMap.getCollection(meta.getName());
//						if(!values.isEmpty()) {
//							if(!values.contains(privilege.getPrivType())) {
//								rulePrivMap.put(meta.getName(), privilege.getPrivType());
//							}
//						} else {
//							rulePrivMap.put(meta.getName(), privilege.getPrivType());
//						}
//					}
//						
//				}
//			}catch (NullPointerException e) {
//				e.printStackTrace();
//			}
//		}//end of for loop
//		
//		 Set<String> keys = rulePrivMap.keySet();
//		 for (String key : keys) {
//			 RolePriv rolePriv = new RolePriv();
//			 rolePriv.setType(key);
//			 rolePriv.setPrivInfo(rulePrivMap.get(key));
//			 userPrivList.add(rolePriv);	           
//	        }		
//		return userPrivList;
	}
	
	public List<RolePriv> getRolePrivByMIHolder(List<MetaIdentifierHolder> privInfoList) {
		List<RolePriv> userPrivList = new ArrayList<>();
		Map<String, RolePriv> tempRolePrivHolder = new HashMap<>();
		List<String> privUuidList = new ArrayList<>();
		for(MetaIdentifierHolder privHolder : privInfoList) {
			privUuidList.add(privHolder.getRef().getUuid());
		}
		
		MatchOperation filterByPrivUuid = match(new Criteria("uuid").in(privUuidList.toArray(new String[privUuidList.size()])));
		GroupOperation groupOperation = group("uuid", "metaId", "privType").max("version").as("version");
		
		Aggregation privAggr = newAggregation(filterByPrivUuid, groupOperation);
		AggregationResults<Privilege> aggrResults = mongoTemplate.aggregate(privAggr, MetaType.privilege.toString().toLowerCase(), Privilege.class);
		List<Privilege> privList = aggrResults.getMappedResults();
		
		if(privList != null) {
			for(Privilege privilege : privList) {
				if(privilege.getMetaId() != null) {
					Meta meta = getMetaByUuid(privilege.getMetaId().getRef().getUuid());
					if(meta != null) {
						if(tempRolePrivHolder.get(meta.getName()) != null) {
							RolePriv rolePriv = tempRolePrivHolder.get(meta.getName());
							@SuppressWarnings("unchecked")
							ArrayList<String> privInfo = (ArrayList<String>) rolePriv.getPrivInfo();
							 privInfo.add(privilege.getPrivType());
							 rolePriv.setPrivInfo(privInfo);
							 userPrivList.add(rolePriv);
							 tempRolePrivHolder.put(meta.getName(), rolePriv);
						} else {
							 RolePriv rolePriv = new RolePriv();
							 rolePriv.setType(meta.getName());
							 ArrayList<String> privInfo = new ArrayList<>();
							 privInfo.add(privilege.getPrivType());
							 rolePriv.setPrivInfo(privInfo);
							 userPrivList.add(rolePriv);
							 tempRolePrivHolder.put(meta.getName(), rolePriv);
						}
					} 
				}
			}
		}
		return new ArrayList<>(tempRolePrivHolder.values());
	}

	public Meta getMetaByUuid(String metaUuid) {
		MatchOperation filterByPrivUuid = match(new Criteria("uuid").is(metaUuid));
		SortOperation sortByVersion = sort(new Sort(Direction.DESC, "version"));
		LimitOperation limitToOnlyFirstDoc = limit(1);
		GroupOperation groupOperation = group("uuid", "name").max("version").as("version");
		Aggregation privAggr = newAggregation(filterByPrivUuid, sortByVersion, limitToOnlyFirstDoc, groupOperation);
		AggregationResults<Meta> aggrResults = mongoTemplate.aggregate(privAggr, MetaType.meta.toString().toLowerCase(), Meta.class);
		Meta meta = aggrResults.getUniqueMappedResult();
		return meta;
	}
	/*public List<RolePriv> getRolePriv(String roleUuid) {
		List<RolePriv> userPrivList = new ArrayList<RolePriv>();
		Role role = roleServiceImpl.findLatestByUuid(roleUuid);
		List<MetaIdentifierHolder> privilegeInfo = role.getPrivilegeInfo();
		List<Privilege> pList = new ArrayList<Privilege>();
		boolean isPresent = false;
		for (int i = 0; i < privilegeInfo.size(); i++) {
			RolePriv userPriv = new RolePriv();
			List<String> privList = new ArrayList<>();
			Privilege privilege = findLatestByUuid(privilegeInfo.get(i).getRef().getUuid());
			Metadata meta = metadataServiceImpl.findLatestByUuid(privilege.getMetaId().getRef().getUuid());
			for(int j=0; j<pList.size();j++)
			{
				if(privilege.getUuid().equals(pList.get(j).getUuid()))
				{					
					isPresent = true;
					break;
				}
				else
				{
					isPresent = false;
				}
			}
			if (!isPresent) {				
				String metaName = meta.getName();
				System.out.println("Metaname: "+metaName);
				Aggregation privilegeAggr = newAggregation(match(Criteria.where("metaId.ref.uuid").is(meta.getUuid())),
						group("uuid").max("version").as("version"));
				AggregationResults<Privilege> privResults = mongoTemplate.aggregate(privilegeAggr, "privilege",Privilege.class);
				List<Privilege> privilegeList = privResults.getMappedResults();
				String privType = null;
				String metaType = null;				
				for (Privilege p : privilegeList) {
					Privilege privilegeLatest;
					String appUuid = (securityServiceImpl.getAppInfo() != null
							&& securityServiceImpl.getAppInfo().getRef() != null)
									? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
					if (appUuid != null) {
						privilegeLatest = iPrivilegeDao.findOneByUuidAndVersion(appUuid, p.getId(), p.getVersion());
					} else {
						privilegeLatest = iPrivilegeDao.findOneByUuidAndVersion(p.getId(), p.getVersion());
					}					
					pList.add(privilegeLatest);
					privType = privilegeLatest.getPrivType();
					metaType = metaName;
					privList.add(privType);									
				}				
				userPriv.setType(metaType);	
				userPriv.setPrivInfo(privList);
				userPrivList.add(userPriv);
			}
		}
		return userPrivList;
	}*/
}