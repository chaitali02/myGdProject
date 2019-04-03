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
package com.inferyx.framework.common;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.service.CommonServiceImpl;


public class MetadataUtil {
		
//	Logger logger = Logger.getLogger(MetadataUtil.class);	
//	
//	@Autowired
//	IActivityDao activityDao;
//	@Autowired
//	IDagDao dagDao;
//	@Autowired
//	IDatapodDao datapodDao;
//	@Autowired
//	private
//	IFilterDao filterDao;
//	@Autowired
//	IExpressionDao expressionDao;
//	@Autowired
//	IFormulaDao formulaDao;
//	@Autowired
//	IMapDao mapDao;
//	@Autowired
//	private
//	IRelationDao relationDao;
//	@Autowired
//	IDatasetDao datasetDao;
//	@Autowired
//	IDagExecDao dagExecDao;
//	@Autowired 
//	IConditionDao conditionDao;
//	@Autowired
//	IDataStoreDao dataStoreDao; //datastore implementation
//	@Autowired
//	ILoadDao loadDao; //datastore implementation
//	@Autowired
//	IGroupDao iUserGroupDao;
//	@Autowired
//	DataStoreServiceImpl dataStoreServiceImpl;
//	@Autowired
//	IDimensionDao dimensionDao;
//	@Autowired
//	IMeasureDao measureDao;
//	@Autowired
//	IVizpodDao vizpodDao;
//	@Autowired
//	IApplicationDao applicationDao;
//	@Autowired
//	IGroupDao igroupDao;
//	@Autowired
//	IRuleExecDao iRuleExecDao;
//	@Autowired
//	IRuleGroupExecDao iRuleGroupExecDao;
//	@Autowired
//	IRoleDao iroleDao;
//	@Autowired
//	IRuleDao iRuleDao;
//	@Autowired
//	IUserDao iUserDao;
//	@Autowired
//	IVizpodExecDao iVizpodExecDao;
//	@Autowired
//	IPrivilegeDao iPrivilegeDao;
//	@Autowired
//	IDatasourceDao iDatasourceDao;
//	@Autowired 
//	IMetaDao iMetaDataDao;
//	@Autowired
//	ISessionDao isessionDao;
//	@Autowired
//	IActivityDao iactivityDao;
//	@Autowired
//	ILoadDao iLoadDao;
//	@Autowired
//	IDashboardDao iDashboardDao;
//	@Autowired
//	IDataQualDao iDataQualDao;
//	@Autowired
//	IDataQualExecDao iDataQualExecDao;
//	@Autowired
//	IDataQualGroupExecDao iDataQualGroupExecDao;
//	@Autowired
//	IRuleGroupDao iRuleGroupDao;
//	@Autowired
//	IFunctionDao iFunctionDao;
//	@Autowired
//	IMapExecDao iMapExecDao;
//	@Autowired
//	ILoadExecDao iLoadExecDao;
//	@Autowired
//	IProfileDao iProfileDao;
//	@Autowired
//	IProfileGroupDao iProfileGroupDao;
//	@Autowired 
//	IProfileExecDao iprofileExecDao;
//	@Autowired
//	IProfileGroupExecDao iProfileGroupExecDao;
//	@Autowired 
//	IAlgorithmDao iAlgorithmDao;
//	@Autowired 
//	IModelDao iModelDao;
//	@Autowired 
//	IParamListDao iParamListDao;
//	@Autowired 
//	IParamSetDao iParamSetDao;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;
//    @Autowired 
//    IDistributionDao iDistributionDao;    
    
//	/**
//	 * @Ganesh
//	 *
//	 * @return the iDistributionDao
//	 */
//	public IDistributionDao getiDistributionDao() {
//		return iDistributionDao;
//	}
//
//	/**
//	 * @Ganesh
//	 *
//	 * @param iDistributionDao the iDistributionDao to set
//	 */
//	public void setiDistributionDao(IDistributionDao iDistributionDao) {
//		this.iDistributionDao = iDistributionDao;
//	}
//
//	public IAlgorithmDao getiAlgorithmDao() {
//		return iAlgorithmDao;
//	}
//
//	public void setiAlgorithmDao(IAlgorithmDao iAlgorithmDao) {
//		this.iAlgorithmDao = iAlgorithmDao;
//	}
//
//	public IModelDao getiModelDao() {
//		return iModelDao;
//	}
//
//	public void setiModelDao(IModelDao iModelDao) {
//		this.iModelDao = iModelDao;
//	}
//
//	public IParamListDao getiParamListDao() {
//		return iParamListDao;
//	}
//
//	public void setiParamListDao(IParamListDao iParamListDao) {
//		this.iParamListDao = iParamListDao;
//	}
//
//	public IParamSetDao getiParamSetDao() {
//		return iParamSetDao;
//	}
//
//	public void setiParamSetDao(IParamSetDao iParamSetDao) {
//		this.iParamSetDao = iParamSetDao;
//	}
//
//	public IProfileExecDao getIprofileExecDao() {
//		return iprofileExecDao;
//	}
//
//	public void setIprofileExecDao(IProfileExecDao iprofileExecDao) {
//		this.iprofileExecDao = iprofileExecDao;
//	}
//	
//	public IProfileGroupExecDao getiProfileGroupExecDao() {
//		return iProfileGroupExecDao;
//	}
//
//	public void setiProfileGroupExecDao(IProfileGroupExecDao iProfileGroupExecDao) {
//		this.iProfileGroupExecDao = iProfileGroupExecDao;
//	}
//
//	public IProfileDao getiProfileDao() {
//		return iProfileDao;
//	}
//
//	public void setiProfileDao(IProfileDao iProfileDao) {
//		this.iProfileDao = iProfileDao;
//	}
//
//	public IProfileGroupDao getiProfileGroupDao() {
//		return iProfileGroupDao;
//	}
//
//	public void setiProfileGroupDao(IProfileGroupDao iProfileGroupDao) {
//		this.iProfileGroupDao = iProfileGroupDao;
//	}
//
//	public IRuleExecDao getiRuleExecDao() {
//		return iRuleExecDao;
//	}
//
//	public void setiRuleExecDao(IRuleExecDao iRuleExecDao) {
//		this.iRuleExecDao = iRuleExecDao;
//	}
//	public IMapExecDao getiMapExecDao() {
//		return iMapExecDao;
//	}
//
//	public void setiMapExecDao(IMapExecDao iMapExecDao) {
//		this.iMapExecDao = iMapExecDao;
//	}
//
//	public ILoadExecDao getiLoadExecDao() {
//		return iLoadExecDao;
//	}
//
//	public void setiLoadExecDao(ILoadExecDao iLoadExecDao) {
//		this.iLoadExecDao = iLoadExecDao;
//	}
//
//	public IFunctionDao getiFunctionDao() {
//		return iFunctionDao;
//	}
//
//	public void setiFunctionDao(IFunctionDao iFunctionDao) {
//		this.iFunctionDao = iFunctionDao;
//	}
//
//	public IRuleGroupDao getiRuleGroupDao() {
//		return iRuleGroupDao;
//	}
//
//	public void setiRuleGroupDao(IRuleGroupDao iRuleGroupDao) {
//		this.iRuleGroupDao = iRuleGroupDao;
//	}
//
//	public IActivityDao getActivityDao() {
//		return activityDao;
//	}
//
//	public void setActivityDao(IActivityDao activityDao) {
//		this.activityDao = activityDao;
//	}
//	public IDataQualGroupExecDao getiDataQualGroupExecDao() {
//		return iDataQualGroupExecDao;
//	}
//
//	public void setiDataQualGroupExecDao(IDataQualGroupExecDao iDataQualGroupExecDao) {
//		this.iDataQualGroupExecDao = iDataQualGroupExecDao;
//	}
//
//	@Autowired
//	IDataQualGroupDao iDataQualGroupDao;
//	public IDataQualExecDao getiDataQualExecDao() {
//		return iDataQualExecDao;
//	}
//
//	public void setiDataQualExecDao(IDataQualExecDao iDataQualExecDao) {
//		this.iDataQualExecDao = iDataQualExecDao;
//	}
//	public IDashboardDao getiDashboardDao() {
//		return iDashboardDao;
//	}
//
//	public void setiDashboardDao(IDashboardDao iDashboardDao) {
//		this.iDashboardDao = iDashboardDao;
//	}
//	public IRuleDao getiRuleDao() {
//		return iRuleDao;
//	}
//
//	public void setiRuleDao(IRuleDao iRuleDao) {
//		this.iRuleDao = iRuleDao;
//	}
//
//	public ILoadDao getiLoadDao() {
//		return iLoadDao;
//	}
//
//	public void setiLoadDao(ILoadDao iLoadDao) {
//		this.iLoadDao = iLoadDao;
//	}
//
//	@Autowired
//	IDatasetDao iDatasetDao;
//	
//	
//	public IDatasetDao getiDatasetDao() {
//		return iDatasetDao;
//	}
//
//	public void setiDatasetDao(IDatasetDao iDatasetDao) {
//		this.iDatasetDao = iDatasetDao;
//	}
//
//	public IActivityDao getIactivityDao() {
//		return iactivityDao;
//	}
//
//	public void setIactivityDao(IActivityDao iactivityDao) {
//		this.iactivityDao = iactivityDao;
//	}
//
//	public ISessionDao getIsessionDao() {
//		return isessionDao;
//	}
//
//	public void setIsessionDao(ISessionDao isessionDao) {
//		this.isessionDao = isessionDao;
//	}
//
//	public IMetaDao getiMetaDataDao() {
//		return iMetaDataDao;
//	}
//
//	public void setiMetaDataDao(IMetaDao iMetaDataDao) {
//		this.iMetaDataDao = iMetaDataDao;
//	}
//
//	public IPrivilegeDao getiPrivilegeDao() {
//		return iPrivilegeDao;
//	}
//
//	public void setiPrivilegeDao(IPrivilegeDao iPrivilegeDao) {
//		this.iPrivilegeDao = iPrivilegeDao;
//	}
//
//	public IUserDao getiUserDao() {
//		return iUserDao;
//	}
//
//	public void setiUserDao(IUserDao iUserDao) {
//		this.iUserDao = iUserDao;
//	}
//
//	public IRoleDao getIroleDao() {
//		return iroleDao;
//	}
//
//	public void setIroleDao(IRoleDao iroleDao) {
//		this.iroleDao = iroleDao;
//	}
//
//	
//
//	public IGroupDao getIgroupDao() {
//		return igroupDao;
//	}
//
//	public void setIgroupDao(IGroupDao igroupDao) {
//		this.igroupDao = igroupDao;
//	}
//
//	@Autowired
//	IUserDao userDao;
//	
//
//	public IUserDao getUserDao() {
//		return userDao;
//	}
//
//	public void setUserDao(IUserDao userDao) {
//		this.userDao = userDao;
//	}
//
//	public IApplicationDao getApplicationDao() {
//		return applicationDao;
//	}
//
//	public void setApplicationDao(IApplicationDao applicationDao) {
//		this.applicationDao = applicationDao;
//	}
//
//	public IDataStoreDao getDatastoreDao() {
//		return dataStoreDao;
//	}
//
//	public void setDatastoreDao(IDataStoreDao datastoreDao) {
//		this.dataStoreDao = datastoreDao;
//	}
//
//	public IDagDao getDagDao() {
//		return dagDao;
//	}
//
//	public void setDagDao(IDagDao dagDao) {
//		this.dagDao = dagDao;
//	}
//
//	public IExpressionDao getExpressionDao() {
//		return expressionDao;
//	}
//
//	public void setExpressionDao(IExpressionDao expressionDao) {
//		this.expressionDao = expressionDao;
//	}
//
//	public IFormulaDao getFormulaDao() {
//		return formulaDao;
//	}
//
//	public void setFormulaDao(IFormulaDao formulaDao) {
//		this.formulaDao = formulaDao;
//	}
//
//	public IMapDao getMapDao() {
//		return mapDao;
//	}
//
//	public void setMapDao(IMapDao mapDao) {
//		this.mapDao = mapDao;
//	}
//
//	public IConditionDao getConditionDao() {
//		return conditionDao;
//	}
//
//	public void setConditionDao(IConditionDao conditionDao) {
//		this.conditionDao = conditionDao;
//	}
//
//	public IDatapodDao getDatapodDao() {
//		return datapodDao;
//	}
//
//	public void setDatapodDao(IDatapodDao datapodDao) {
//		this.datapodDao = datapodDao;
//	}
//
//	public IDagExecDao getDagExecDao() {
//		return dagExecDao;
//	}
//
//	public void setDagExecDao(IDagExecDao dagExecDao) {
//		this.dagExecDao = dagExecDao;
//	}
//	
//	
//	
///*
//	public IGroupDao getGroupDao() {
//		return groupDao;
//	}
//
//	public void setGroupDao(IGroupDao groupDao) {
//		this.groupDao = groupDao;
//	}
//*/
//
//	
//	public IDataQualDao getiDataQualDao() {
//		return iDataQualDao;
//	}
//
//	public void setiDataQualDao(IDataQualDao iDataQualDao) {
//		this.iDataQualDao = iDataQualDao;
//	}
//
//	public IDataQualGroupDao getiDataQualGroupDao() {
//		return iDataQualGroupDao;
//	}
//
//	public void setiDataQualGroupDao(IDataQualGroupDao iDataQualGroupDao) {
//		this.iDataQualGroupDao = iDataQualGroupDao;
//	}
//
//	public IMeasureDao getMeasureDao() {
//		return measureDao;
//	}
//
//	public void setMeasureDao(IMeasureDao measureDao) {
//		this.measureDao = measureDao;
//	}
//	
//	public IDimensionDao getDimensionDao() {
//		return dimensionDao;
//	}
//
//	public void setDimensionDao(IDimensionDao dimensionDao) {
//		this.dimensionDao = dimensionDao;
//	}
//	
//
//	
//	public IVizpodDao getVizpodDao() {
//		return vizpodDao;
//	}
//
//	public void setVizpodDao(IVizpodDao vizpodDao) {
//		this.vizpodDao = vizpodDao;
//	}
//	
//	public IDatasourceDao getiDatasourceDao() {
//		return iDatasourceDao;
//	}
//
//	public void setiDatasourceDao(IDatasourceDao iDatasourceDao) {
//		this.iDatasourceDao = iDatasourceDao;
//	}
//	
//	public MetadataUtil(){
//		// private constructor to defeat instantiation
//	}
	
/*	public Object getRefObject(MetaIdentifier ref) throws JsonProcessingException {
		return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
//		OrderKey key = new OrderKey(ref.getUuid(), ref.getVersion());
//		//Set<OrderKey> keySets = null;
//		if(ref.getType() == MetaType.datapod) {
//			if (key.getVersion() != null ) {
//				//return datapodDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.datapod.toString());
//			} else {
//				//Datapod dPod = datapodDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version")); 
//				Datapod dPod = (Datapod) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.datapod.toString());
//				//System.out.println("\n\nUUID: "+ref.getUuid());
//				ref.setVersion(dPod.getVersion());
//				return dPod;
//			}
//		}
//		
//		if(ref.getType() == MetaType.profile) {
//			if (key.getVersion() != null ) {
//				//return iProfileDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.profile.toString());
//			} else {
//				//Profile profile = iProfileDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Profile profile = (Profile) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.profile.toString());
//				ref.setVersion(profile.getVersion());
//				return profile;
//			}
//		}
//		
//		if(ref.getType() == MetaType.profilegroup) {
//			if (key.getVersion() != null ) {
//				//return iProfileGroupDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.profilegroup.toString());
//			} else {
//				//ProfileGroup profilegroup = iProfileGroupDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				ProfileGroup profilegroup = (ProfileGroup) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.profilegroup.toString());
//				ref.setVersion(profilegroup.getVersion());
//				return profilegroup;
//			}
//		}
//		
//		if(ref.getType() == MetaType.profileExec) {
//			if (key.getVersion() != null ) {
//				//return iprofileExecDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.profileExec.toString());
//			} else {
//				//ProfileExec profileexec = iprofileExecDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				ProfileExec profileexec = (ProfileExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.profileExec.toString());
//				ref.setVersion(profileexec.getVersion());
//				return profileexec;
//			}
//		}
//		
//		if(ref.getType() == MetaType.load) {
//			if (key.getVersion() != null ) {
//				//return iLoadDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.load.toString());
//			} else {
//				//Load lPod = iLoadDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Load lPod = (Load) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.load.toString());
//				ref.setVersion(lPod.getVersion());
//				return lPod;
//			}
//		}
//		
//		if(ref.getType() == MetaType.rulegroup) {
//			if (key.getVersion() != null ) {
//				//return iRuleGroupDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.rulegroup.toString());
//			} else {
//				//RuleGroup ruleGroup = iRuleGroupDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version")); 
//				RuleGroup ruleGroup = (RuleGroup) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.rulegroup.toString()); 
//				ref.setVersion(ruleGroup.getVersion());
//				return ruleGroup;
//			}
//		}
//	
//		if(ref.getType() == MetaType.rule) {
//			if (key.getVersion() != null ) {
//				//return iRuleDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.rule.toString());
//			} else {
//				//Rule rule = iRuleDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Rule rule = (Rule) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.rule.toString());
//				ref.setVersion(rule.getVersion());
//				return rule;
//			} 
//		}
//		
//		if(ref.getType() == MetaType.dq) {
//			if (key.getVersion() != null ) {
//				//return iDataQualDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.dq.toString());
//			} else {
//				//DataQual dataqual = iDataQualDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				DataQual dataqual = (DataQual) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.dq.toString());
//				ref.setVersion(dataqual.getVersion());
//				return dataqual;
//			}
//		}
//		
//		if(ref.getType() == MetaType.dqgroup) {
//			if (key.getVersion() != null ) {
//				//return iDataQualGroupDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.dqgroup.toString());
//			} else {
//				//DataQualGroup dataqualgroup = iDataQualGroupDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				DataQualGroup dataqualgroup = (DataQualGroup) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.dqgroup.toString());
//				ref.setVersion(dataqualgroup.getVersion());
//				return dataqualgroup;
//			}
//		}
//		
//		if(ref.getType() == MetaType.dataset) {
//			if (key.getVersion() != null ) {
//				//return iDatasetDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.dataset.toString());
//			} else {
//				//Dataset dataset = iDatasetDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				DataSet dataset = (DataSet) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.dataset.toString());
//				ref.setVersion(dataset.getVersion());
//				return dataset;
//			}
//		}
//		
//		if(ref.getType() == MetaType.session) {
//			if (key.getVersion() != null ) {
//				//return isessionDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.session.toString());
//			} else {
//				//Session sessiondao = isessionDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Session sessiondao = (Session) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.session.toString());
//				ref.setVersion(sessiondao.getVersion());
//				return sessiondao;
//			}
//		}
//		
//		if(ref.getType() == MetaType.meta) {
//			if (key.getVersion() != null ) {
//				//return iMetaDataDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.meta.toString());
//			} else {
//				//Meta dPod = iMetaDataDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Meta dPod = (Meta) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.meta.toString());
//				ref.setVersion(dPod.getVersion());
//				return dPod;
//			}
//		}		
//		
//		if(ref.getType() == MetaType.privilege) {
//			if (key.getVersion() != null ) {
//				//return iPrivilegeDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.privilege.toString());
//			} else {
//				//Privilege dPod = iPrivilegeDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Privilege dPod = (Privilege) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.privilege.toString());
//				ref.setVersion(dPod.getVersion());
//				return dPod;
//			}
//		}
//		
//		
//		if(ref.getType() == MetaType.user) {
//			if (key.getVersion() != null ) {
//				//return iUserDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.user.toString());
//			} else {
//				//User dPod = iUserDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				User dPod = (User) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.user.toString());
//				ref.setVersion(dPod.getVersion());
//				return dPod;
//			}
//		}
//		if(ref.getType() == MetaType.usergroup) {
//			if (key.getVersion() != null ) {
//				//return iUserGroupDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.usergroup.toString());
//			} else {
//				//Group dPod = iUserGroupDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Group dPod = (Group) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.usergroup.toString());
//				ref.setVersion(dPod.getVersion());
//				return dPod;
//			}
//		}
//		if(ref.getType() == MetaType.role) {
//			if (key.getVersion() != null ) {
//				//return iroleDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.role.toString());
//			} else {
//				//Role dPod = iroleDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Role dPod = (Role) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.role.toString());
//				ref.setVersion(dPod.getVersion());
//				return dPod;
//			}
//		}
//		
//		if(ref.getType() == MetaType.application) {
//			if (key.getVersion() != null ) {
//				//return applicationDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.application.toString());
//			} else {
//				//Application aPod = applicationDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Application aPod = (Application) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.application.toString());
//				ref.setVersion(aPod.getVersion());
//				return aPod;
//			}
//		}
//		
//		if(ref.getType() == MetaType.ruleExec) {
//			if (key.getVersion() != null ) {
//				//return iRuleExecDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.ruleExec.toString());
//			} else {
//				//RuleExec ruleExec = iRuleExecDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				RuleExec ruleExec = (RuleExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.ruleExec.toString());
//				ref.setVersion(ruleExec.getVersion());
//				return ruleExec;
//			}
//		}
//		
//		if(ref.getType() == MetaType.rulegroupExec) {
//			if (key.getVersion() != null ) {
//				//return iRuleGroupExecDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.rulegroupExec.toString());
//			} else {
//				//RuleGroupExec ruleGroupExec = iRuleGroupExecDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				RuleGroupExec ruleGroupExec = (RuleGroupExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.rulegroupExec.toString());
//				ref.setVersion(ruleGroupExec.getVersion());
//				return ruleGroupExec;
//			}
//		}
//		if(ref.getType() == MetaType.dqgroupExec) {
//			if (key.getVersion() != null ) {
//				//return iDataQualGroupExecDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.dqgroupExec.toString());
//			} else {
//				//DataQualGroupExec dataQualGroupExec = iDataQualGroupExecDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version")); 
//				DataQualGroupExec dataQualGroupExec = (DataQualGroupExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.dqgroupExec.toString()); 
//				ref.setVersion(dataQualGroupExec.getVersion());
//				return dataQualGroupExec;
//			}
//		}
//		if(ref.getType() == MetaType.profilegroupExec) {
//			if (key.getVersion() != null ) {
//				//return iProfileGroupExecDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.profilegroupExec.toString());
//			} else {
//				//ProfileGroupExec profileGroupExec = iProfileGroupExecDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version")); 
//				ProfileGroupExec profileGroupExec = (ProfileGroupExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.profilegroupExec.toString()); 
//				ref.setVersion(profileGroupExec.getVersion());
//				return profileGroupExec;
//			}
//		}
//		
//		if(ref.getType() == MetaType.filter) {
//			if (key.getVersion() != null ) {
//				//return getFilterDao().findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.filter.toString());
//			} else {
//				//Filter filter = getFilterDao().findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Filter filter = (Filter) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.filter.toString());
//				ref.setVersion(filter.getVersion());
//				return filter;
//			}
//		}
//		
//		if(ref.getType() == MetaType.relation) {
//			if (key.getVersion() != null ) {
//				//return getRelationDao().findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.relation.toString());
//			} else {
//				//Relation relation = getRelationDao().findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Relation relation = (Relation) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.relation.toString());
//				ref.setVersion(relation.getVersion());
//				return relation;
//			}
//		}
//		
//		if(ref.getType() == MetaType.formula) {
//			if (key.getVersion() != null ) {
//				//return formulaDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.formula.toString());
//			} else {
//				//Formula formula = formulaDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version")); 
//				Formula formula = (Formula) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.formula.toString()); 
//				ref.setVersion(formula.getVersion());
//				return formula;
//			}
//		}
//		
//		if(ref.getType() == MetaType.condition) {
//			if (key.getVersion() != null ) {
//				//return conditionDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.condition.toString());
//			} else {
//				//Condition condition = conditionDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Condition condition = (Condition) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.condition.toString());
//				ref.setVersion(condition.getVersion());
//				return condition;
//			}
//		}
//		
//		if(ref.getType() == MetaType.expression) {
//			if (key.getVersion() != null ) {
//				//return expressionDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.expression.toString());
//			} else {
//				//Expression expression = expressionDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Expression expression = (Expression) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.expression.toString());
//				ref.setVersion(expression.getVersion());
//				return expression;
//			}
//		}
//		
//		if(ref.getType() == MetaType.dagExec) {
//			if (key.getVersion() != null ) {
//				//return dagExecDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.dagExec.toString());
//			} else {
//				//DagExec dagExec = dagExecDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				DagExec dagExec = (DagExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.dagExec.toString());
//				ref.setVersion(dagExec.getVersion());
//				return dagExec;
//			}
//		}
//		
//		if(ref.getType() == MetaType.rule) {
//			if (key.getVersion() != null ) {
//				//return iRuleDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.rule.toString());
//			} else {
//				//Rule rule = iRuleDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Rule rule = (Rule) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.rule.toString());
//				ref.setVersion(rule.getVersion());
//				return rule;
//			}
//		}
//		
//		
//		if(ref.getType() == MetaType.map) {
//			if (key.getVersion() != null ) {
//				//return mapDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.map.toString());
//			} else {
//				//Map map = mapDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Map map = (Map) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.map.toString());
//				ref.setVersion(map.getVersion());
//				return map;
//			}
//		}
//		if(ref.getType() == MetaType.mapExec) {
//			if (key.getVersion() != null ) {
//				//return iMapExecDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.mapExec.toString());
//			} else {
//				//MapExec mapExec = iMapExecDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				MapExec mapExec = (MapExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.mapExec.toString());
//				ref.setVersion(mapExec.getVersion());
//				return mapExec;
//			}
//		}
//		if(ref.getType() == MetaType.mapiter) {
//			if (key.getVersion() != null ) {
//				//return mapDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.mapiter.toString());
//			} else {
//				//Map map = mapDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Map map = (Map) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.mapiter.toString());
//				ref.setVersion(map.getVersion());
//				return map;
//			}
//		}
//		if(ref.getType() == MetaType.dag) {
//			if (key.getVersion() != null ) {
//				//return dagDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.dag.toString());
//			} else {
//				//Dag dag = dagDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Dag dag = (Dag) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.dag.toString());
//				ref.setVersion(dag.getVersion());
//				return dag;
//			}
//		}
//		if(ref.getType() == MetaType.datastore){ //datastore implementation
//			if (key.getVersion() != null ) {
//				//return dataStoreDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.datastore.toString());
//			}else {
//				//DataStore dataStore = dataStoreDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				DataStore dataStore = (DataStore) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.datastore.toString());
//				ref.setVersion(dataStore.getVersion());
//				return dataStore;
//			}
//		}
//		if(ref.getType() == MetaType.load){ //load implementation
//			if (key.getVersion() != null ) {
//				//return loadDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.load.toString());
//			}else {
//				//Load load = loadDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Load load = (Load) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.load.toString());
//				ref.setVersion(load.getVersion());
//				return load;
//			}
//		}
//		if(ref.getType() == MetaType.loadExec) {
//			if (key.getVersion() != null ) {
//				//return iLoadExecDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.loadExec.toString());
//			} else {
//				//LoadExec loadExec = iLoadExecDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				LoadExec loadExec = (LoadExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.loadExec.toString());
//				ref.setVersion(loadExec.getVersion());
//				return loadExec;
//			}
//		}
//		/*if(ref.getType() == MetaType.group){ //group implementation
//			if (key.getVersion() != null ) {
//				return groupDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//			}else {
//				Group group = groupDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version")); 
//				ref.setVersion(group.getVersion());
//				return group;
//			}
//		}*/
//		if(ref.getType() == MetaType.dimension) {
//			if (key.getVersion() != null ) {
//				//return dimensionDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.dimension.toString());
//			} else {
//				//Dimension dimension = dimensionDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Dimension dimension = (Dimension) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.dimension.toString());
//				ref.setVersion(dimension.getVersion());
//				return dimension;
//			}
//		}
//		
//		if(ref.getType() == MetaType.measure) {
//			if (key.getVersion() != null ) {
//				//return measureDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.measure.toString());
//			} else {
//				//Measure measure = measureDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Measure measure = (Measure) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.measure.toString());
//				ref.setVersion(measure.getVersion());
//				return measure;
//			}
//		}
//		if(ref.getType() == MetaType.vizpod) {
//			if (key.getVersion() != null ) {
//				//return vizpodDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.vizpod.toString());
//			} else {
//				//Vizpod vizpod = vizpodDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Vizpod vizpod = (Vizpod) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.vizpod.toString());
//				ref.setVersion(vizpod.getVersion());
//				return vizpod;
//			}
//		}
//		if(ref.getType() == MetaType.datasource) {
//			if (key.getVersion() != null ) {
//				//return iDatasourceDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.datasource.toString());
//			} else {
//				//Datasource datasource = iDatasourceDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				Datasource datasource = (Datasource) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.datasource.toString());
//				ref.setVersion(datasource.getVersion());
//				return datasource;
//			}
//		}
//		
//		if(ref.getType() == MetaType.vizExec) {
//			if (key.getVersion() != null ) {
//				//return iVizpodExecDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.vizExec.toString());
//			} else {
//				//VizExec vizexec = iVizpodExecDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				VizExec vizexec = (VizExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.vizExec.toString());
//				ref.setVersion(vizexec.getVersion());
//				return vizexec;
//			}
//		}
//
//		if(ref.getType() == MetaType.dataset) {
//			if (key.getVersion() != null ) {
//				//return datasetDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.dataset.toString());
//			} else {
//				//Dataset dataset = datasetDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				DataSet dataset = (DataSet) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.dataset.toString());
//				ref.setVersion(dataset.getVersion());
//				return dataset;
//			}
//		}
//		
//		if(ref.getType() == MetaType.function) {
//			if (key.getVersion() != null ) {
//				//return iFunctionDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.function.toString());
//			} else {
//				//Function function = iFunctionDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version")); 
//				Function function = (Function) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.function.toString()); 
//				ref.setVersion(function.getVersion());
//				return function;
//			}
//		}
//		
//		if(ref.getType() == MetaType.loadExec) {
//			if (key.getVersion() != null ) {
//				//return iLoadExecDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.loadExec.toString());
//			} else {
//				//LoadExec loadExec = iLoadExecDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				LoadExec loadExec = (LoadExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.loadExec.toString());
//				ref.setVersion(loadExec.getVersion());
//				return loadExec;
//			}
//		}
//		
//		if(ref.getType() == MetaType.paramset) {
//			if (key.getVersion() != null ) {
//				//return iParamSetDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.paramset.toString());
//			} else {
//				//ParamSet paramSet = iParamSetDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				ParamSet paramSet = (ParamSet) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.paramset.toString());
//				ref.setVersion(paramSet.getVersion());
//				return paramSet;
//			}
//		}
//		
//		if(ref.getType() == MetaType.paramlist) {
//			if (key.getVersion() != null ) {
//				//return iParamListDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.paramlist.toString());
//			} else {
//				//ParamList paramList = iParamListDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version"));  
//				ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.paramlist.toString());
//				ref.setVersion(paramList.getVersion());
//				return paramList;
//			}
//		}
//		
//		if(ref.getType() == MetaType.dqExec) {
//			if (key.getVersion() != null ) {
//				//return iDataQualExecDao.findOneByUuidAndVersion(ref.getUuid(), ref.getVersion());
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.dqExec.toString());
//			} else {
//				//DataQualExec dataQualExec = iDataQualExecDao.findLatestByUuid(ref.getUuid(), new Sort(Sort.Direction.DESC, "version")); 
//				DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.dqExec.toString());
//				ref.setVersion(dataQualExec.getVersion());
//				return dataQualExec;
//			}
//		}
//		
//		if(ref.getType() == MetaType.recon) {
//			if (key.getVersion() != null ) {
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.recon.toString());
//			} else {
//				Recon recon = (Recon) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.recon.toString());
//				ref.setVersion(recon.getVersion());
//				return recon;
//			}
//		}
//		
//		if(ref.getType() == MetaType.reconExec) {
//			if (key.getVersion() != null ) {
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.reconExec.toString());
//			} else {
//				ReconExec reconExec = (ReconExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.reconExec.toString());
//				ref.setVersion(reconExec.getVersion());
//				return reconExec;
//			}
//		}
//		if(ref.getType() == MetaType.ingest) {
//			if (key.getVersion() != null ) {
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.ingest.toString());
//			} else {
//				Ingest ingest = (Ingest) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.ingest.toString());
//				ref.setVersion(ingest.getVersion());
//				return ingest;
//			}
//		}
//		
//		if(ref.getType() == MetaType.ingestExec) {
//			if (key.getVersion() != null ) {
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.ingestExec.toString());
//			} else {
//				IngestExec ingestExec = (IngestExec) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.ingestExec.toString());
//				ref.setVersion(ingestExec.getVersion());
//				return ingestExec;
//			}
//		}
//		if(ref.getType() == MetaType.distribution)
//			if (key.getVersion() != null ) {
//				return commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.distribution.toString());
//			} else {
//				Distribution distribution = (Distribution) commonServiceImpl.getLatestByUuid(ref.getUuid(), MetaType.distribution.toString());
//				ref.setVersion(distribution.getVersion());
//				return distribution;
//			}
//	//	logger.error("Meta not found");
//		return null;
	//}*/

//	public IVizpodExecDao getiVizpodExecDao() {
//		return iVizpodExecDao;
//	}
//
//	public void setiVizpodExecDao(IVizpodExecDao iVizpodExecDao) {
//		this.iVizpodExecDao = iVizpodExecDao;
//	}
//
//	public IRelationDao getRelationDao() {
//		return relationDao;
//	}
//
//	public void setRelationDao(IRelationDao relationDao) {
//		this.relationDao = relationDao;
//	}
//	
//	public IDatasetDao getDatasetDao() {
//		return datasetDao;
//	}
//
//	public void setDatasetDao(IDatasetDao datasetDao) {
//		this.datasetDao = datasetDao;
//	}
//
//	public IFilterDao getFilterDao() {
//		return filterDao;
//	}
//
//	public void setFilterDao(IFilterDao filterDao) {
//		this.filterDao = filterDao;
//	}
//
//	public BaseEntity resolveName(BaseEntity baseEntity) throws JsonProcessingException {
//		User user = (User)getRefObject(baseEntity.getCreatedBy().getRef());
//		baseEntity.getCreatedBy().getRef().setName(user.getName());
//		return baseEntity;
//	}
	
}
