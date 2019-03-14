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
package com.inferyx.framework.register;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
//import org.apache.spark.SparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.codehaus.jettison.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.GraphInfo;
import com.inferyx.framework.dao.IActivityDao;
import com.inferyx.framework.dao.IAlgorithmDao;
import com.inferyx.framework.dao.IAppConfigDao;
import com.inferyx.framework.dao.IApplicationDao;
import com.inferyx.framework.dao.ICommentDao;
import com.inferyx.framework.dao.IConditionDao;
import com.inferyx.framework.dao.IDagDao;
import com.inferyx.framework.dao.IDagExecDao;
import com.inferyx.framework.dao.IDashboardDao;
import com.inferyx.framework.dao.IDataQualDao;
import com.inferyx.framework.dao.IDataQualExecDao;
import com.inferyx.framework.dao.IDataQualGroupDao;
import com.inferyx.framework.dao.IDataQualGroupExecDao;
import com.inferyx.framework.dao.IDataStoreDao;
import com.inferyx.framework.dao.IDatapodDao;
import com.inferyx.framework.dao.IDatasetDao;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.dao.IDimensionDao;
import com.inferyx.framework.dao.IDistributionDao;
import com.inferyx.framework.dao.IDownloadDao;
import com.inferyx.framework.dao.IEdgeDao;
import com.inferyx.framework.dao.IExportDao;
import com.inferyx.framework.dao.IExpressionDao;
import com.inferyx.framework.dao.IFilterDao;
import com.inferyx.framework.dao.IFormulaDao;
import com.inferyx.framework.dao.IFunctionDao;
import com.inferyx.framework.dao.IGraphpodDao;
import com.inferyx.framework.dao.IGraphpodExecDao;
import com.inferyx.framework.dao.IGroupDao;
import com.inferyx.framework.dao.IImportDao;
import com.inferyx.framework.dao.ILoadDao;
import com.inferyx.framework.dao.ILoadExecDao;
import com.inferyx.framework.dao.ILogDao;
import com.inferyx.framework.dao.ILovDao;
import com.inferyx.framework.dao.IMapDao;
import com.inferyx.framework.dao.IMapExecDao;
import com.inferyx.framework.dao.IMeasureDao;
import com.inferyx.framework.dao.IMessageDao;
import com.inferyx.framework.dao.IMetaDao;
import com.inferyx.framework.dao.IModelDao;
import com.inferyx.framework.dao.IModelExecDao;
import com.inferyx.framework.dao.IOperatorDao;
import com.inferyx.framework.dao.IOperatorExecDao;
import com.inferyx.framework.dao.IOperatorTypeDao;
import com.inferyx.framework.dao.IParamListDao;
import com.inferyx.framework.dao.IParamSetDao;
import com.inferyx.framework.dao.IPredictDao;
import com.inferyx.framework.dao.IPredictExecDao;
import com.inferyx.framework.dao.IPrivilegeDao;
import com.inferyx.framework.dao.IProfileDao;
import com.inferyx.framework.dao.IProfileExecDao;
import com.inferyx.framework.dao.IProfileGroupDao;
import com.inferyx.framework.dao.IProfileGroupExecDao;
import com.inferyx.framework.dao.IReconDao;
import com.inferyx.framework.dao.IReconExecDao;
import com.inferyx.framework.dao.IRelationDao;
import com.inferyx.framework.dao.IRoleDao;
import com.inferyx.framework.dao.IRuleDao;
import com.inferyx.framework.dao.IRuleExecDao;
import com.inferyx.framework.dao.IRuleGroupDao;
import com.inferyx.framework.dao.IRuleGroupExecDao;
import com.inferyx.framework.dao.IScheduleDao;
import com.inferyx.framework.dao.ISessionDao;
import com.inferyx.framework.dao.ISimulateDao;
import com.inferyx.framework.dao.ISimulateExecDao;
import com.inferyx.framework.dao.ITagDao;
import com.inferyx.framework.dao.ITrainDao;
import com.inferyx.framework.dao.ITrainExecDao;
import com.inferyx.framework.dao.IUploadDao;
import com.inferyx.framework.dao.IUserDao;
import com.inferyx.framework.dao.IVertexDao;
import com.inferyx.framework.dao.IVizpodDao;
import com.inferyx.framework.dao.IVizpodExecDao;
import com.inferyx.framework.domain.Edge;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ProcessExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Vertex;
import com.inferyx.framework.enums.ProcessType;
import com.inferyx.framework.service.AlgorithmServiceImpl;
import com.inferyx.framework.service.ApplicationServiceImpl;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DagExecServiceImpl;
import com.inferyx.framework.service.DagServiceImpl;
import com.inferyx.framework.service.DataQualExecServiceImpl;
import com.inferyx.framework.service.DataQualGroupExecServiceImpl;
import com.inferyx.framework.service.DataQualGroupServiceImpl;
import com.inferyx.framework.service.DataQualServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.DatasourceServiceImpl;
import com.inferyx.framework.service.DimensionServiceImpl;
import com.inferyx.framework.service.ExpressionServiceImpl;
import com.inferyx.framework.service.FilterServiceImpl;
import com.inferyx.framework.service.FormulaServiceImpl;
import com.inferyx.framework.service.GenericGraph;
import com.inferyx.framework.service.GraphServiceImpl;
import com.inferyx.framework.service.GroupServiceImpl;
import com.inferyx.framework.service.LoadExecServiceImpl;
import com.inferyx.framework.service.LoadServiceImpl;
import com.inferyx.framework.service.MapExecServiceImpl;
import com.inferyx.framework.service.MapServiceImpl;
import com.inferyx.framework.service.MetadataServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;
import com.inferyx.framework.service.ParamListServiceImpl;
import com.inferyx.framework.service.PrivilegeServiceImpl;
import com.inferyx.framework.service.RegisterService;
import com.inferyx.framework.service.RelationServiceImpl;
import com.inferyx.framework.service.RoleServiceImpl;
import com.inferyx.framework.service.RuleExecServiceImpl;
import com.inferyx.framework.service.RuleServiceImpl;
import com.inferyx.framework.service.SecurityServiceImpl;
import com.inferyx.framework.service.SessionServiceImpl;
import com.inferyx.framework.service.UserServiceImpl;
import com.inferyx.framework.service.VizExecServiceImpl;
import com.inferyx.framework.service.VizpodServiceImpl;

@Component
public class GraphRegister<T> {

	@Autowired
	DatapodServiceImpl datapodServiceImpl; 
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	FormulaServiceImpl formulaServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	MapServiceImpl mapServiceImpl;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	@Autowired
	DimensionServiceImpl dimensionServiceImpl;
	@Autowired
	DagServiceImpl dagServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	GroupServiceImpl groupServiceImpl;
	@Autowired
	RoleServiceImpl roleServiceImpl;
	@Autowired
	PrivilegeServiceImpl privilegeServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	SessionServiceImpl sessionServiceImpl;
	@Autowired
	DatasourceServiceImpl datasourceServiceImpl;
	@Autowired
	MetadataServiceImpl metaDataServiceImpl;
	@Autowired
	DagExecServiceImpl dagExecServiceImpl;
	@Autowired
	VizExecServiceImpl vizExecServiceImpl;
	@Autowired
	VizpodServiceImpl vizpodServiceImpl;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	LoadServiceImpl loadServiceImpl;
	@Autowired
	DataQualServiceImpl dataQualServiceImpl;
	@Autowired
	DataQualExecServiceImpl dataQualExecServiceImpl;
	@Autowired
	DataQualGroupServiceImpl dataQualGroupServiceImpl;
	@Autowired
	DataQualGroupExecServiceImpl dataQualGroupExecServiceImpl;
	@Autowired
	RuleServiceImpl ruleServiceImpl;
	@Autowired
	MapExecServiceImpl mapExecServiceImpl;
	@Autowired
	LoadExecServiceImpl loadExecServiceImpl;
	@Autowired
	RuleExecServiceImpl ruleExecServiceImpl;
	@Autowired
	AlgorithmServiceImpl algorithmServiceImpl;
	@Autowired
	ModelServiceImpl modelServiceImpl;
	@Autowired
	ParamListServiceImpl paramListServiceImpl;
	@Autowired
	GenericGraph genericGraph;
	GraphInfo graphFlag;
//	@Autowired
//	MetadataUtil miUtil;
	@Autowired
	GraphServiceImpl graphServiceImpl;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	/*@Autowired
    private DatapodServiceImpl datapodServiceImpl;
	@Autowired
    private RelationServiceImpl relationServiceImpl;
	*/
	@Autowired
	RegisterService registerService;
	@Autowired
	IModelDao iModelDao;
	/*@Autowired
	UserServiceImpl userServiceImpl;*/	
	@Autowired
	IDatapodDao iDatapodDao;
	@Autowired
	IRuleDao iRuleDao;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	ILoadDao iLoadDao;
	@Autowired
	IFunctionDao iFunctionDao;
	@Autowired 
	MetadataServiceImpl metadataServiceImpl;
	@Autowired 
	IMapDao iMapDao;	
	@Autowired
	IActivityDao iActivityDao;
	@Autowired
	IAlgorithmDao iAlgorithmDao;
	@Autowired
	IApplicationDao iApplicationDao;
	@Autowired
	IConditionDao iConditionDao;
	@Autowired
	IDagDao iDagDao;
	@Autowired
	IDagExecDao iDagExecDao;
	@Autowired
	IDashboardDao iDashboardDao;
	@Autowired
	IDatasetDao iDatasetDao;
	@Autowired
	IDataStoreDao iDataStoreDao;
	@Autowired
	IDimensionDao iDimensionDao;
	@Autowired
	IExpressionDao iExpressionDao;
	@Autowired
	IFilterDao iFilterDao;
	@Autowired
	IFormulaDao iFormulaDao;
	@Autowired
	IGroupDao iGroupDao;
	@Autowired
	ILoadExecDao iLoadExecDao;
	@Autowired
	IMeasureDao iMeasureDao;
	@Autowired
	IModelExecDao iModelExecDao;
	@Autowired
	IParamListDao iParamListDao;
	@Autowired
	IParamSetDao iParamSetDao;
	@Autowired
	IPrivilegeDao iPrivilegeDao;
	@Autowired
	IProfileDao iProfileDao;
	@Autowired
	IProfileExecDao iProfileExecDao;
	@Autowired
	IProfileGroupDao iProfileGroupDao;
	@Autowired
	IProfileGroupExecDao iProfileGroupExecDao;
	@Autowired
	IRelationDao iRelationDao;
	@Autowired
	IRoleDao iRoleDao;
	@Autowired
	IRuleExecDao iRuleExecDao;
	@Autowired
	IRuleGroupExecDao iRuleGroupExecDao;
	@Autowired
	IRuleGroupDao iRuleGroupDao;
	@Autowired
	ISessionDao iSessionDao;
	@Autowired
	IUserDao iUserDao;
	@Autowired
	IVizpodDao iVizpodDao;
	@Autowired
	IVizpodExecDao iVizpodExecDao;
	@Autowired
	IDataQualDao iDataQualDao;
	@Autowired
	IDataQualGroupDao iDataQualGroupDao;
	@Autowired
	IDataQualGroupExecDao iDataQualGroupExecDao;
	@Autowired
	IMetaDao iMetadataDao;
	@Autowired
	IDatasourceDao iDatasourceDao;
	@Autowired
	IDataQualExecDao iDataQualExecDao;/*
	@Autowired
	MetadataUtil miUtil;
	@Autowired
	GraphInfo graphFlag;*/
	@Autowired
	IVertexDao iVertexDao;
	@Autowired
	IEdgeDao iEdgeDao;
	@Autowired
	IMapExecDao iMapExecDao;/*
	@Autowired
	GraphServiceImpl graphServiceImpl;*/
	@Autowired
	IImportDao iImportDao;
	@Autowired
	IExportDao iExportDao;
	@Autowired
	IMessageDao iMessageDao;
	@Autowired
	ILogDao iLogDao;
	@Autowired
	IDownloadDao iDownloadDao;
	@Autowired
	IUploadDao iUploadDao;
	@Autowired
	IPredictDao iPredictDao;
	@Autowired
	IPredictExecDao iPredictExecDao;
	@Autowired
	ISimulateDao iSimulateDao;
	@Autowired
	ISimulateExecDao iSimulateExecDao;
	@Autowired
	ITrainDao iTrainDao;
	@Autowired
	ITrainExecDao iTrainExecDao;
	@Autowired
	IReconDao iReconDao;
	@Autowired
	IReconExecDao iReconExecDao;
	@Autowired
	IDistributionDao iDistributionDao;
	@Autowired
	IAppConfigDao iAppConfigDao;
	@Autowired
	IOperatorTypeDao iOperatorTypeDao;
	@Autowired
	IOperatorExecDao iOperatorExecDao;
	@Autowired
	IOperatorDao iOperatorDao;
	@Autowired
	ICommentDao iCommentDao;
	@Autowired
	ITagDao iTagDao;
	@Autowired
	ILovDao iLovDao;
	@Autowired
	IGraphpodDao iGraphpodDao;
	@Autowired
	IGraphpodExecDao iGraphpodExecDao;
	@Autowired
	IScheduleDao iScheduleDao;
	
	
	public IScheduleDao getiScheduleDao() {
		return iScheduleDao;
	}

	public void setiScheduleDao(IScheduleDao iScheduleDao) {
		this.iScheduleDao = iScheduleDao;
	}

	public IGraphpodDao getiGraphpodDao() {
		return this.iGraphpodDao;
	}

	public void setiGraphpodDao(IGraphpodDao iGraphpodDao) {
		this.iGraphpodDao = iGraphpodDao;
	}

	public IGraphpodExecDao getiGraphpodExecDao() {
		return this.iGraphpodExecDao;
	}

	public void setiGraphpodExecDao(IGraphpodExecDao iGraphpodExecDao) {
		this.iGraphpodExecDao = iGraphpodExecDao;
	}
	
	public ILovDao getiLovDao() {
		return iLovDao;
	}

	public void setiLovDao(ILovDao iLovDao) {
		this.iLovDao = iLovDao;
	}

	
	public ITagDao getiTagDao() {
		return iTagDao;
	}

	public void setiTagDao(ITagDao iTagDao) {
		this.iTagDao = iTagDao;
	}
	public ICommentDao getiCommentDao() {
		return iCommentDao;
	}

	public void setiCommentDao(ICommentDao iCommentDao) {
		this.iCommentDao = iCommentDao;
	}
	/**
	 * @Ganesh
	 *
	 * @return the iOperatorDao
	 */
	public IOperatorDao getiOperatorDao() {
		return iOperatorDao;
	}

	/**
	 * @Ganesh
	 *
	 * @param iOperatorDao the iOperatorDao to set
	 */
	public void setiOperatorDao(IOperatorDao iOperatorDao) {
		this.iOperatorDao = iOperatorDao;
	}
	
	/**
	 * @Ganesh
	 *
	 * @return the iOperatorTypeDao
	 */
	public IOperatorTypeDao getiOperatorTypeDao() {
		return iOperatorTypeDao;
	}

	/**
	 * @Ganesh
	 *
	 * @param iOperatorTypeDao the iOperatorTypeDao to set
	 */
	public void setiOperatorTypeDao(IOperatorTypeDao iOperatorTypeDao) {
		this.iOperatorTypeDao = iOperatorTypeDao;
	}

	/**
	 * @Ganesh
	 *
	 * @return the iOperatorExecDao
	 */
	public IOperatorExecDao getiOperatorExecDao() {
		return iOperatorExecDao;
	}

	/**
	 * @Ganesh
	 *
	 * @param iOperatorExecDao the iOperatorExecDao to set
	 */
	public void setiOperatorExecDao(IOperatorExecDao iOperatorExecDao) {
		this.iOperatorExecDao = iOperatorExecDao;
	}
	

	public IAppConfigDao getiAppConfigDao() {
		return iAppConfigDao;
	}

	public void setiAppConfigDao(IAppConfigDao iAppConfigDao) {
		this.iAppConfigDao = iAppConfigDao;
	}


	/**
	 * @Ganesh
	 *
	 * @return the iDistributionDao
	 */
	public IDistributionDao getiDistributionDao() {
		return iDistributionDao;
	}

	/**
	 * @Ganesh
	 *
	 * @param iDistributionDao the iDistributionDao to set
	 */
	public void setiDistributionDao(IDistributionDao iDistributionDao) {
		this.iDistributionDao = iDistributionDao;
	}	
	
	public IReconDao getiReconDao() {
		return iReconDao;
	}

	public void setiReconDao(IReconDao iReconDao) {
		this.iReconDao = iReconDao;
	}

	public IReconExecDao getiReconExecDao() {
		return iReconExecDao;
	}

	public void setiReconExecDao(IReconExecDao iReconExecDao) {
		this.iReconExecDao = iReconExecDao;
	}

	public ITrainExecDao getiTrainExecDao() {
		return iTrainExecDao;
	}

	public void setiTrainExecDao(ITrainExecDao iTrainExecDao) {
		this.iTrainExecDao = iTrainExecDao;
	}

	public ITrainDao getiTrainDao() {
		return iTrainDao;
	}

	public void setiTrainDao(ITrainDao iTrainDao) {
		this.iTrainDao = iTrainDao;
	}

	public ISimulateDao getiSimulateDao() {
		return iSimulateDao;
	}

	public void setiSimulateDao(ISimulateDao iSimulateDao) {
		this.iSimulateDao = iSimulateDao;
	}
	public ISimulateExecDao getiSimulateExecDao() {
		return iSimulateExecDao;
	}

	public void setiSimulateExecDao(ISimulateExecDao iSimulateExecDao) {
		this.iSimulateExecDao = iSimulateExecDao;
	}
	/**
	 * @Ganesh
	 *
	 * @return the iPredictDao
	 */

	public IPredictExecDao getiPredictExecDao() {
		return iPredictExecDao;
	}
	/**
	 * @Ganesh
	 *
	 * @return the iPredictExecDao
	 */
	public IPredictDao getiPredictDao() {
		return iPredictDao;
	}
	/**
	 * @Ganesh
	 *
	 * @return the iPredictExecDao
	 */
	public void setiPredictDao(IPredictDao iPredictDao) {
		this.iPredictDao = iPredictDao;
	}

	/**
	 * @Ganesh
	 *
	 * @param iPredictExecDao the iPredictExecDao to set
	 */
	public void setiPredictExecDao(IPredictExecDao iPredictExecDao) {
		this.iPredictExecDao = iPredictExecDao;
	}

	/**
	 * @Ganesh
	 *
	 * @return the iDownloadDao
	 */
	public IDownloadDao getiDownloadDao() {
		return iDownloadDao;
	}

	/**
	 * @Ganesh
	 *
	 * @param iDownloadDao the iDownloadDao to set
	 */
	public void setiDownloadDao(IDownloadDao iDownloadDao) {
		this.iDownloadDao = iDownloadDao;
	}

	/**
	 * @Ganesh
	 *
	 * @return the iUploadDao
	 */
	public IUploadDao getiUploadDao() {
		return iUploadDao;
	}

	/**
	 * @Ganesh
	 *
	 * @param iUploadDao the iUploadDao to set
	 */
	public void setiUploadDao(IUploadDao iUploadDao) {
		this.iUploadDao = iUploadDao;
	}

	/**
	 * @return the hiveContext
	 */
	/*public HiveContext getHiveContext() {
		return hiveContext;
	}*/

	/**
	 * @param hiveContext the hiveContext to set
	 */
	/*public void setHiveContext(HiveContext hiveContext) {
		this.hiveContext = hiveContext;
	}*/

	public ILogDao getiLogDao() {
		return iLogDao;
	}

	public void setiLogDao(ILogDao iLogDao) {
		this.iLogDao = iLogDao;
	}

	public IMessageDao getiMessageDao() {
		return iMessageDao;
	}

	public void setiMessageDao(IMessageDao iMessageDao) {
		this.iMessageDao = iMessageDao;
	}

	public IExportDao getiExportDao() {
		return iExportDao;
	}

	public void setiExportDao(IExportDao iExportDao) {
		this.iExportDao = iExportDao;
	}

	public IVertexDao getiVertexDao() {
		return iVertexDao;
	}

	public void setiVertexDao(IVertexDao iVertexDao) {
		this.iVertexDao = iVertexDao;
	}

	public IEdgeDao getiEdgeDao() {
		return iEdgeDao;
	}

	public void setiEdgeDao(IEdgeDao iEdgeDao) {
		this.iEdgeDao = iEdgeDao;
	}

	public IMapExecDao getiMapExecDao() {
		return iMapExecDao;
	}

	public void setiMapExecDao(IMapExecDao iMapExecDao) {
		this.iMapExecDao = iMapExecDao;
	}

	/*public GraphInfo getGraphFlag() {
		return graphFlag;
	}

	public void setGraphFlag(GraphInfo graphFlag) {
		this.graphFlag = graphFlag;
	}*/

//	public MetadataUtil getMiUtil() {
//		return miUtil;
//	}
//
//	public void setMiUtil(MetadataUtil miUtil) {
//		this.miUtil = miUtil;
//	}

	public IDataQualExecDao getiDataQualExecDao() {
		return iDataQualExecDao;
	}

	public void setiDataQualExecDao(IDataQualExecDao iDataQualExecDao) {
		this.iDataQualExecDao = iDataQualExecDao;
	}

	public IDatasourceDao getiDatasourceDao() {
		return iDatasourceDao;
	}

	public void setiDatasourceDao(IDatasourceDao iDatasourceDao) {
		this.iDatasourceDao = iDatasourceDao;
	}

	public IMetaDao getiMetadataDao() {
		return iMetadataDao;
	}

	public void setiMetadataDao(IMetaDao iMetadataDao) {
		this.iMetadataDao = iMetadataDao;
	}

	public IDataQualDao getiDataQualDao() {
		return iDataQualDao;
	}

	public void setiDataQualDao(IDataQualDao iDataQualDao) {
		this.iDataQualDao = iDataQualDao;
	}

	public IDataQualGroupDao getiDataQualGroupDao() {
		return iDataQualGroupDao;
	}

	public void setiDataQualGroupDao(IDataQualGroupDao iDataQualGroupDao) {
		this.iDataQualGroupDao = iDataQualGroupDao;
	}

	public IDataQualGroupExecDao getiDataQualGroupExecDao() {
		return iDataQualGroupExecDao;
	}

	public void setiDataQualGroupExecDao(IDataQualGroupExecDao iDataQualGroupExecDao) {
		this.iDataQualGroupExecDao = iDataQualGroupExecDao;
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public ILoadDao getiLoadDao() {
		return iLoadDao;
	}

	public void setiLoadDao(ILoadDao iLoadDao) {
		this.iLoadDao = iLoadDao;
	}

	public IFunctionDao getiFunctionDao() {
		return iFunctionDao;
	}

	public void setiFunctionDao(IFunctionDao iFunctionDao) {
		this.iFunctionDao = iFunctionDao;
	}

	public IModelDao getiModelDao() {
		return iModelDao;
	}

	public void setiModelDao(IModelDao iModelDao) {
		this.iModelDao = iModelDao;
	}
	public IDatapodDao getiDatapodDao() {
		return iDatapodDao;
	}

	public void setiDatapodDao(IDatapodDao iDatapodDao) {
		this.iDatapodDao = iDatapodDao;
	}

	public IRuleDao getiRuleDao() {
		return iRuleDao;
	}

	public void setiRuleDao(IRuleDao iRuleDao) {
		this.iRuleDao = iRuleDao;
	}
	
	public IMapDao getiMapDao() {
		return iMapDao;
	}

	public void setiMapDao(IMapDao iMapDao) {
		this.iMapDao = iMapDao;
	}
	
	public IActivityDao getiActivityDao() {
		return iActivityDao;
	}

	public void setiActivityDao(IActivityDao iActivityDao) {
		this.iActivityDao = iActivityDao;
	}

	public IAlgorithmDao getiAlgorithmDao() {
		return iAlgorithmDao;
	}

	public void setiAlgorithmDao(IAlgorithmDao iAlgorithmDao) {
		this.iAlgorithmDao = iAlgorithmDao;
	}

	public IApplicationDao getiApplicationDao() {
		return iApplicationDao;
	}

	public void setiApplicationDao(IApplicationDao iApplicationDao) {
		this.iApplicationDao = iApplicationDao;
	}

	public IConditionDao getiConditionDao() {
		return iConditionDao;
	}

	public void setiConditionDao(IConditionDao iConditionDao) {
		this.iConditionDao = iConditionDao;
	}

	public IDagDao getiDagDao() {
		return iDagDao;
	}

	public void setiDagDao(IDagDao iDagDao) {
		this.iDagDao = iDagDao;
	}

	public IDagExecDao getiDagExecDao() {
		return iDagExecDao;
	}

	public void setiDagExecDao(IDagExecDao iDagExecDao) {
		this.iDagExecDao = iDagExecDao;
	}

	public IDashboardDao getiDashboardDao() {
		return iDashboardDao;
	}

	public void setiDashboardDao(IDashboardDao iDashboardDao) {
		this.iDashboardDao = iDashboardDao;
	}

	public IDatasetDao getiDatasetDao() {
		return iDatasetDao;
	}

	public void setiDatasetDao(IDatasetDao iDatasetDao) {
		this.iDatasetDao = iDatasetDao;
	}

	public IDataStoreDao getiDataStoreDao() {
		return iDataStoreDao;
	}

	public void setiDataStoreDao(IDataStoreDao iDataStoreDao) {
		this.iDataStoreDao = iDataStoreDao;
	}

	public IDimensionDao getiDimensionDao() {
		return iDimensionDao;
	}

	public void setiDimensionDao(IDimensionDao iDimensionDao) {
		this.iDimensionDao = iDimensionDao;
	}

	public IExpressionDao getiExpressionDao() {
		return iExpressionDao;
	}

	public void setiExpressionDao(IExpressionDao iExpressionDao) {
		this.iExpressionDao = iExpressionDao;
	}

	public IFilterDao getiFilterDao() {
		return iFilterDao;
	}

	public void setiFilterDao(IFilterDao iFilterDao) {
		this.iFilterDao = iFilterDao;
	}

	public IFormulaDao getiFormulaDao() {
		return iFormulaDao;
	}

	public void setiFormulaDao(IFormulaDao iFormulaDao) {
		this.iFormulaDao = iFormulaDao;
	}

	public IGroupDao getiGroupDao() {
		return iGroupDao;
	}

	public void setiGroupDao(IGroupDao iGroupDao) {
		this.iGroupDao = iGroupDao;
	}

	public ILoadExecDao getiLoadExecDao() {
		return iLoadExecDao;
	}

	public void setiLoadExecDao(ILoadExecDao iLoadExecDao) {
		this.iLoadExecDao = iLoadExecDao;
	}

	public IMeasureDao getiMeasureDao() {
		return iMeasureDao;
	}

	public void setiMeasureDao(IMeasureDao iMeasureDao) {
		this.iMeasureDao = iMeasureDao;
	}

	public IModelExecDao getiModelExecDao() {
		return iModelExecDao;
	}

	public void setiModelExecDao(IModelExecDao iModelExecDao) {
		this.iModelExecDao = iModelExecDao;
	}

	public IParamListDao getiParamListDao() {
		return iParamListDao;
	}

	public void setiParamListDao(IParamListDao iParamListDao) {
		this.iParamListDao = iParamListDao;
	}

	public IParamSetDao getiParamSetDao() {
		return iParamSetDao;
	}

	public void setiParamSetDao(IParamSetDao iParamSetDao) {
		this.iParamSetDao = iParamSetDao;
	}

	public IPrivilegeDao getiPrivilegeDao() {
		return iPrivilegeDao;
	}

	public void setiPrivilegeDao(IPrivilegeDao iPrivilegeDao) {
		this.iPrivilegeDao = iPrivilegeDao;
	}

	public IProfileDao getiProfileDao() {
		return iProfileDao;
	}

	public void setiProfileDao(IProfileDao iProfileDao) {
		this.iProfileDao = iProfileDao;
	}

	public IProfileExecDao getiProfileExecDao() {
		return iProfileExecDao;
	}

	public void setiProfileExecDao(IProfileExecDao iProfileExecDao) {
		this.iProfileExecDao = iProfileExecDao;
	}

	public IProfileGroupDao getiProfileGroupDao() {
		return iProfileGroupDao;
	}

	public void setiProfileGroupDao(IProfileGroupDao iProfileGroupDao) {
		this.iProfileGroupDao = iProfileGroupDao;
	}

	public IProfileGroupExecDao getiProfileGroupExecDao() {
		return iProfileGroupExecDao;
	}

	public void setiProfileGroupExecDao(IProfileGroupExecDao iProfileGroupExecDao) {
		this.iProfileGroupExecDao = iProfileGroupExecDao;
	}

	public IRelationDao getiRelationDao() {
		return iRelationDao;
	}

	public void setiRelationDao(IRelationDao iRelationDao) {
		this.iRelationDao = iRelationDao;
	}

	public IRoleDao getiRoleDao() {
		return iRoleDao;
	}

	public void setiRoleDao(IRoleDao iRoleDao) {
		this.iRoleDao = iRoleDao;
	}

	public IRuleExecDao getiRuleExecDao() {
		return iRuleExecDao;
	}

	public void setiRuleExecDao(IRuleExecDao iRuleExecDao) {
		this.iRuleExecDao = iRuleExecDao;
	}

	public IRuleGroupExecDao getiRuleGroupExecDao() {
		return iRuleGroupExecDao;
	}

	public void setiRuleGroupExecDao(IRuleGroupExecDao iRuleGroupExecDao) {
		this.iRuleGroupExecDao = iRuleGroupExecDao;
	}

	public IRuleGroupDao getiRuleGroupDao() {
		return iRuleGroupDao;
	}

	public void setiRuleGroupDao(IRuleGroupDao iRuleGroupDao) {
		this.iRuleGroupDao = iRuleGroupDao;
	}

	public ISessionDao getiSessionDao() {
		return iSessionDao;
	}

	public void setiSessionDao(ISessionDao iSessionDao) {
		this.iSessionDao = iSessionDao;
	}

	public IUserDao getiUserDao() {
		return iUserDao;
	}

	public void setiUserDao(IUserDao iUserDao) {
		this.iUserDao = iUserDao;
	}

	public IVizpodDao getiVizpodDao() {
		return iVizpodDao;
	}

	public void setiVizpodDao(IVizpodDao iVizpodDao) {
		this.iVizpodDao = iVizpodDao;
	}

	public IVizpodExecDao getiVizpodExecDao() {
		return iVizpodExecDao;
	}

	public void setiVizpodExecDao(IVizpodExecDao iVizpodExecDao) {
		this.iVizpodExecDao = iVizpodExecDao;
	}
	
	public GraphInfo getGraphFlag() {
		return graphFlag;
	}

	public void setGraphFlag(GraphInfo graphFlag) {
		this.graphFlag = graphFlag;
	}

	/*public JavaSparkContext getJavaSparkContext() {
		return javaSparkContext;
	}

	public void setJavaSparkContext(JavaSparkContext javaSparkContext) {
		this.javaSparkContext = javaSparkContext;
	}*/

	/*public HiveContext getHiveContext() {
		return hiveContext;
	}

	public void setHiveContext(HiveContext hiveContext) {
		this.hiveContext = hiveContext;
	}*/

	public List<Row> getVertexRowList() {
		return vertexRowList;
	}

	public void setVertexRowList(List<Row> vertexRowList) {
		this.vertexRowList = vertexRowList;
	}

	public List<Row> getEdgeRowList() {
		return edgeRowList;
	}

	public void setEdgeRowList(List<Row> edgeRowList) {
		this.edgeRowList = edgeRowList;
	}

	public List<Row> getTotalEdgeList() {
		return totalEdgeList;
	}

	public void setTotalEdgeList(List<Row> totalEdgeList) {
		this.totalEdgeList = totalEdgeList;
	}

	public List<Row> getTotalVertexList() {
		return totalVertexList;
	}

	public void setTotalVertexList(List<Row> totalVertexList) {
		this.totalVertexList = totalVertexList;
	}

	public IImportDao getiImportDao() {
		return iImportDao;
	}

	public void setiImportDao(IImportDao iImportDao) {
		this.iImportDao = iImportDao;
	}

	ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
	
	static final Logger logger = Logger.getLogger(GraphRegister.class);
	String resultDatapod;
	//private static final String GET = "get";
	
	List<Row> vertexRowList = new ArrayList<Row>();
	List<Row> edgeRowList = new ArrayList<Row>();
	List<Row> totalEdgeList = new ArrayList<Row>();
	List<Row> totalVertexList = new ArrayList<Row>();
	Map<String, Row> vertexRowMap = new HashMap<>();
	Map<String, Row> edgeRowMap = new HashMap<>();
	
	public List<Row> convertToEdgeRow(List<Edge> edgeList) {
		if (edgeList == null || edgeList.isEmpty()) {
			return new ArrayList<>();
		}
		List<Row> edgeRowList = new ArrayList<>();
		for (Edge edge : edgeList) {
			edgeRowList.add(RowFactory.create(edge.getSrc(), edge.getDst(), edge.getRelationType()));
		}
		return edgeRowList;
	}
	
	public List<Row> convertToVertexRow(List<Vertex> vertexList) {
		if (vertexList == null || vertexList.isEmpty()) {
			return new ArrayList<>();
		}
		List<Row> vertexRowList = new ArrayList<>();
		for (Vertex vertex : vertexList) {
			vertexRowList.add(RowFactory.create(vertex.getUuid(), vertex.getVersion(), vertex.getName(), vertex.getNodeType(), 
												vertex.getDataType(), vertex.getDesc(), vertex.getCreatedOn(), vertex.getActive()));
		}
		return vertexRowList;
	}
		
	public List<Row> createTotVertexList(Map<String, Row> vertexMap) {
		List<Row> vertexList = new ArrayList<>();
		for (String key : vertexMap.keySet()) {
			vertexList.add(vertexMap.get(key));
		}
		return vertexList;
	}
	
	public List<Row> createTotEdgeList(Map<String, Row> edgeMap) {
		List<Row> edgeList = new ArrayList<>();
		for (String key : edgeMap.keySet()) {
			edgeList.add(edgeMap.get(key));
		}
		return edgeList;
	}
	
	public void buildGraph() throws Exception {
		ProcessExec processExec = new ProcessExec();
		try {
			List<Row> totalEdgeList = new ArrayList<Row>();
			List<Row> totalVertexList = new ArrayList<Row>();
			java.util.Map<String, Row> edgeRowMap = new HashMap<>();
			java.util.Map<String, Row> verticesRowMap = new HashMap<>();
			
			SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			Date startDate = formatter.parse((new Date()).toString());
			
			processExec.setProcessType(ProcessType.BUILDING_GRAPH_ENGINE);
			processExec.setBaseEntity();		
			commonServiceImpl.setMetaStatus(processExec, MetaType.processExec, Status.Stage.PENDING);
			commonServiceImpl.setMetaStatus(processExec, MetaType.processExec, Status.Stage.STARTING);	
			commonServiceImpl.setMetaStatus(processExec, MetaType.processExec, Status.Stage.READY);
			commonServiceImpl.setMetaStatus(processExec, MetaType.processExec, Status.Stage.RUNNING);
			processExec.setStartTime(startDate);
			
			//java.util.Map<String, Object> objectMap = new HashMap<String, Object>();
			//ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
		    
	
			logger.info("Graph flag is set to " + graphFlag.isMode());
			if (!graphFlag.isMode()) {
				logger.info("Skipping building of graph.");
			}
			String result =null;
			List<MetaType> metaTypes =  MetaType.getMetaList();
			for(MetaType mType : metaTypes){
				try {
					//Object dao = this.getClass().getMethod(GET + Helper.getDaoClass(mType)).invoke(this);
					
					@SuppressWarnings("unchecked")
					//change method findAllLatestWithoutAppUuid to findAll due to version concept...
					//List<T> objectList = (List<T>) commonServiceImpl.findAllLatestWithoutAppUuid(mType);
					List<T> objectList = (List<T>) commonServiceImpl.findAll(mType);
					if (objectList == null ) {
						continue;
					}
					for (Object obj : objectList) {
						result = writer.writeValueAsString(obj);
						graphServiceImpl.createVnE(result, totalVertexList, totalEdgeList, verticesRowMap, edgeRowMap, mType.toString(), null);
					}
					logger.info("  Vertex size after "+mType + " : " + objectList.size()+" Total : "+verticesRowMap.size());
					//logger.info(" Total vertex size after "+mType + " : " + totalVertexList.size());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
						| SecurityException | NullPointerException e) {
					e.printStackTrace();
				}
			}
			/*List<T> objectList = commonServiceImpl.findAllLatest(MetaType.function);
			for(Object obj: objectList) {
				BaseEntity baseEntity = (BaseEntity)obj;
				logger.info(baseEntity.getCreatedBy().getRef().toString());
			}*/
			this.vertexRowMap = verticesRowMap;
			this.edgeRowMap = edgeRowMap;
			
			graphServiceImpl.deleteAllVertices();
			
			logger.info(" Total vertex size current appInfo: " + verticesRowMap.size());
			totalVertexList = createTotVertexList(verticesRowMap);
			graphServiceImpl.saveVertices(totalVertexList, null);
			graphServiceImpl.deleteAllEdges();
			logger.info(" Total edge size current appInfo  : " + edgeRowMap.size());
			//totalEdgeList = createTotEdgeList(edgeRowMap);
			graphServiceImpl.saveEdges(totalEdgeList, null);

			Date stopTime = formatter.parse((new Date()).toString());
			processExec.setStopTime(stopTime);
			commonServiceImpl.setMetaStatus(processExec, MetaType.processExec, Status.Stage.COMPLETED);
		}  catch (Exception e) {
			commonServiceImpl.setMetaStatus(processExec, MetaType.processExec, Status.Stage.FAILED);
			throw new RuntimeException(e);
		}	
	}
	
	/*public void loadGraph() {
		
		System.out.println("Graph flag is set to " + graphFlag.isMode() + ". Loading graph...");
		if (!graphFlag.isMode()) {
			System.out.println("Skipping loading of graph.");
			return;
		}
		
		long noOfEdges = graphServiceImpl.countEdges();
		System.out.println(" No of Edges : " + noOfEdges);
		long noOfVertices = graphServiceImpl.countVertices();
		System.out.println(" No of Vertices : " + noOfVertices);
		int pageSize = 10000;
		List<Edge> edgeList = null;
		List<Vertex> verticesList = null;
		totalEdgeList = new ArrayList<>();
		totalVertexList = new ArrayList<>();
		for (int i = 0; i < (noOfEdges/pageSize)+1; i++) {
			edgeList = graphServiceImpl.findEdges(i, pageSize);
			if (edgeList == null || edgeList.isEmpty()) {
				break;
			}
			totalEdgeList.addAll(convertToEdgeRow(edgeList));
			System.out.println(" Loading Edge Page : " + i);
		}
		for (int i = 0; i < (noOfVertices/pageSize)+1; i++) {
			verticesList = graphServiceImpl.findVertices(i, pageSize);
			if (verticesList == null || verticesList.isEmpty()) {
				break;
			}
			totalVertexList.addAll(convertToVertexRow(verticesList));
			System.out.println(" Loading Vertex Page : " + i);
		}
		for (int i = 0; i < totalVertexList.size(); i++) {
			if (i ==0) {
				System.out.println(totalVertexList.get(i).schema());
			}
		}
		graphServiceImpl.createGraph(totalVertexList, totalEdgeList);
		
		//totalVertexList.addAll(convertToVertexRow(graphServiceImpl.findVertices()));
	}
	*/
	public void updateGraph(Object metaObj, MetaType type) throws JSONException, java.text.ParseException, JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		String jsonString = writer.writeValueAsString(metaObj);
		graphServiceImpl.createVnE(jsonString, totalVertexList, totalEdgeList, vertexRowMap, edgeRowMap, type.toString(), null);
		//graphServiceImpl.createGraph(totalVertexList, totalEdgeList);
	}
	
}
