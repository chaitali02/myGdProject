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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.spark.sql.Dataset;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.common.GraphInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.WorkbookUtil;
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
import com.inferyx.framework.dao.IReconGroupDao;
import com.inferyx.framework.dao.IReconGroupExecDao;
import com.inferyx.framework.dao.IRelationDao;
import com.inferyx.framework.dao.IRoleDao;
import com.inferyx.framework.dao.IRuleDao;
import com.inferyx.framework.dao.IRuleExecDao;
import com.inferyx.framework.dao.IRuleGroupDao;
import com.inferyx.framework.dao.IRuleGroupExecDao;
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
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.DownloadExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.FeatureAttrMap;
import com.inferyx.framework.domain.FeatureRefHolder;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.Log;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaStatsHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamInfo;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSet;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.StageExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.TaskExec;
import com.inferyx.framework.domain.TaskOperator;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.UploadExec;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.register.GraphRegister;

@Service
public class CommonServiceImpl <T> {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	/*@Autowired
	HiveContext hiveContext;*/
	@Autowired
    DatapodServiceImpl datapodServiceImpl;
	@Autowired
    RelationServiceImpl relationServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	
	@Autowired
	RegisterService registerService;
	@Autowired
	IModelDao iModelDao;
	@Autowired
	UserServiceImpl userServiceImpl;
	
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
	IMetaDao iMetaDao;
	@Autowired
	IDatasourceDao iDatasourceDao;
	@Autowired
	IDataQualExecDao iDataQualExecDao;
	@Autowired
	MetadataUtil miUtil;
	@Autowired
	GraphInfo graphFlag;
	@Autowired
	IVertexDao iVertexDao;
	@Autowired
	IEdgeDao iEdgeDao;
	@Autowired
	IMapExecDao iMapExecDao;
	@Autowired
	GraphServiceImpl graphServiceImpl;
	@Autowired
	IImportDao iImportDao;
	@Autowired
	IExportDao iExportDao;
	@Autowired
	ExportServiceImpl exportServiceImpl;
	@Autowired
	ImportServiceImpl importServiceImpl;
	@Autowired
	IMessageDao iMessageDao;
	@Autowired
	RuleServiceImpl ruleServiceImpl;
	@Autowired
	RuleExecServiceImpl ruleExecServiceImpl;
	@Autowired
	RuleGroupServiceImpl ruleGroupServiceImpl;
	@Autowired
	RuleGroupExecServiceImpl ruleGroupExecServiceImpl;
	@Autowired
	DataQualServiceImpl dataQualServiceImpl;
	@Autowired
	DataQualExecServiceImpl dataQualExecServiceImpl;
	@Autowired
	DataQualGroupServiceImpl dataQualGroupServiceImpl;
	@Autowired
	DataQualGroupExecServiceImpl dataQualGroupExecServiceImpl;
	@Autowired
	MapServiceImpl mapServiceImpl;
	@Autowired
	MapExecServiceImpl mapExecServiceImpl;
	@Autowired
	ProfileServiceImpl profileServiceImpl;
	@Autowired
	ProfileExecServiceImpl profileExecServiceImpl;
	@Autowired
	ProfileGroupServiceImpl profileGroupServiceImpl;
	@Autowired
	ProfileGroupExecServiceImpl profileGroupExecServiceImpl;
	@Autowired
	DagExecServiceImpl dagExecServiceImpl;
	@Autowired
	LogServiceImpl logServiceImpl;
	@Autowired
	ILogDao iLogDao;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	@SuppressWarnings("rawtypes")
	@Resource(name="taskThreadMap")
	ConcurrentHashMap taskThreadMap;
	@Autowired
	DataFrameService dataFrameService;
	CustomLogger customLogger = new CustomLogger();
	@Autowired
	DownloadExec downloadExec;
	@Autowired
	UploadExec uploadExec;
	
	@Autowired
	IDownloadDao iDownloadDao;
	@Autowired
	IUploadDao iUploadDao;
	@Autowired
	protected ExecutorFactory execFactory;
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
	IReconExecDao iReconExecDao;
	@Autowired
	IReconDao iReconDao;
	@Autowired
	IReconGroupDao iReconGroupDao;
	@Autowired
	IReconGroupExecDao iReconGroupExecDao;	
	@Autowired
	ReconServiceImpl reconServiceImpl;
	@Autowired
	ReconExecServiceImpl reconExecServiceImpl;
	@Autowired
	ReconGroupServiceImpl reconGroupServiceImpl;
	@Autowired
	ReconGroupExecServiceImpl reconGroupExecServiceImpl;
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
	Helper helper;
	@Autowired
	ILovDao iLovDao;
	
	
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

	public ReconServiceImpl getReconServiceImpl() {
		return reconServiceImpl;
	}

	public void setReconServiceImpl(ReconServiceImpl reconServiceImpl) {
		this.reconServiceImpl = reconServiceImpl;
	}

	public ReconExecServiceImpl getReconExecServiceImpl() {
		return reconExecServiceImpl;
	}

	public void setReconExecServiceImpl(ReconExecServiceImpl reconExecServiceImpl) {
		this.reconExecServiceImpl = reconExecServiceImpl;
	}

	public ReconGroupServiceImpl getReconGroupServiceImpl() {
		return reconGroupServiceImpl;
	}

	public void setReconGroupServiceImpl(ReconGroupServiceImpl reconGroupServiceImpl) {
		this.reconGroupServiceImpl = reconGroupServiceImpl;
	}

	public ReconGroupExecServiceImpl getReconGroupExecServiceImpl() {
		return reconGroupExecServiceImpl;
	}

	public void setReconGroupExecServiceImpl(ReconGroupExecServiceImpl reconGroupExecServiceImpl) {
		this.reconGroupExecServiceImpl = reconGroupExecServiceImpl;
	}
	public IReconGroupDao getiReconGroupDao() {
		return iReconGroupDao;
	}

	public void setiReconGroupDao(IReconGroupDao iReconGroupDao) {
		this.iReconGroupDao = iReconGroupDao;
	}

	public IReconGroupExecDao getiReconGroupExecDao() {
		return iReconGroupExecDao;
	}

	public void setiReconGroupExecDao(IReconGroupExecDao iReconGroupExecDao) {
		this.iReconGroupExecDao = iReconGroupExecDao;
	}

	public IReconExecDao getiReconExecDao() {
		return iReconExecDao;
	}

	public void setiReconExecDao(IReconExecDao iReconExecDao) {
		this.iReconExecDao = iReconExecDao;
	}

	public IReconDao getiReconDao() {
		return iReconDao;
	}

	public void setiReconDao(IReconDao iReconDao) {
		this.iReconDao = iReconDao;
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
	public IPredictDao getiPredictDao() {
		return iPredictDao;
	}

	/**
	 * @Ganesh
	 *
	 * @param iPredictDao the iPredictDao to set
	 */
	public void setiPredictDao(IPredictDao iPredictDAo) {
		this.iPredictDao = iPredictDAo;
	}

	/**
	 * @Ganesh
	 *
	 * @return the iPredictExecDao
	 */
	public IPredictExecDao getiPredictExecDao() {
		return iPredictExecDao;
	}

	/**
	 * @Ganesh
	 *
	 * @param iPredictExecDao the iPredictExecDao to set
	 */
	public void setiPredictExecDao(IPredictExecDao iPredictExecDao) {
		this.iPredictExecDao = iPredictExecDao;
	}

	public IUploadDao getiUploadDao() {
		return iUploadDao;
	}

	public void setiUploadDao(IUploadDao iUploadDao) {
		this.iUploadDao = iUploadDao;
	}

	public UploadExec getUploadExec() {
		return uploadExec;
	}

	public void setUploadExec(UploadExec uploadExec) {
		this.uploadExec = uploadExec;
	}


	public IDownloadDao getiDownloadDao() {
		return iDownloadDao;
	}

	public void setiDownloadDao(IDownloadDao iDownloadDao) {
		this.iDownloadDao = iDownloadDao;
	}

	public DownloadExec getDownloadExec() {
		return downloadExec;
	}

	public void setDownloadExec(DownloadExec downloadExec) {
		this.downloadExec = downloadExec;
	}

	public ILogDao getiLogDao() {
		return iLogDao;
	}

	public void setiLogDao(ILogDao iLogDao) {
		this.iLogDao = iLogDao;
	}

	public LogServiceImpl getLogServiceImpl() {
		return logServiceImpl;
	}

	public void setLogServiceImpl(LogServiceImpl logServiceImpl) {
		this.logServiceImpl = logServiceImpl;
	}

	public IMessageDao getiMessageDao() {
		return iMessageDao;
	}

	public void setiMessageDao(IMessageDao iMessageDao) {
		this.iMessageDao = iMessageDao;
	}

	public IImportDao getiImportDao() {
		return iImportDao;
	}

	public void setiImportDao(IImportDao iImportDao) {
		this.iImportDao = iImportDao;
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

	public GraphInfo getGraphFlag() {
		return graphFlag;
	}

	public void setGraphFlag(GraphInfo graphFlag) {
		this.graphFlag = graphFlag;
	}

	public MetadataUtil getMiUtil() {
		return miUtil;
	}

	public void setMiUtil(MetadataUtil miUtil) {
		this.miUtil = miUtil;
	}

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

	public IMetaDao getiMetaDao() {
		return iMetaDao;
	}

	public void setiMetaDao(IMetaDao iMetaDao) {
		this.iMetaDao = iMetaDao;
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

	public RuleServiceImpl getRuleServiceImpl() {
		return ruleServiceImpl;
	}

	public void setRuleServiceImpl(RuleServiceImpl ruleServiceImpl) {
		this.ruleServiceImpl = ruleServiceImpl;
	}

	public RuleGroupServiceImpl getRuleGroupServiceImpl() {
		return ruleGroupServiceImpl;
	}

	public void setRuleGroupServiceImpl(RuleGroupServiceImpl ruleGroupServiceImpl) {
		this.ruleGroupServiceImpl = ruleGroupServiceImpl;
	}

	public DataQualServiceImpl getDataQualServiceImpl() {
		return dataQualServiceImpl;
	}

	public void setDataQualServiceImpl(DataQualServiceImpl dataQualServiceImpl) {
		this.dataQualServiceImpl = dataQualServiceImpl;
	}

	public DataQualGroupServiceImpl getDataQualGroupServiceImpl() {
		return dataQualGroupServiceImpl;
	}

	public void setDataQualGroupServiceImpl(DataQualGroupServiceImpl dataQualGroupServiceImpl) {
		this.dataQualGroupServiceImpl = dataQualGroupServiceImpl;
	}

	public MapServiceImpl getMapServiceImpl() {
		return mapServiceImpl;
	}

	public void setMapServiceImpl(MapServiceImpl mapServiceImpl) {
		this.mapServiceImpl = mapServiceImpl;
	}

	public ProfileServiceImpl getProfileServiceImpl() {
		return profileServiceImpl;
	}

	public void setProfileServiceImpl(ProfileServiceImpl profileServiceImpl) {
		this.profileServiceImpl = profileServiceImpl;
	}

	public ProfileGroupServiceImpl getProfileGroupServiceImpl() {
		return profileGroupServiceImpl;
	}

	public void setProfileGroupServiceImpl(ProfileGroupServiceImpl profileGroupServiceImpl) {
		this.profileGroupServiceImpl = profileGroupServiceImpl;
	}

	public RuleExecServiceImpl getRuleExecServiceImpl() {
		return ruleExecServiceImpl;
	}

	public void setRuleExecServiceImpl(RuleExecServiceImpl ruleExecServiceImpl) {
		this.ruleExecServiceImpl = ruleExecServiceImpl;
	}

	public RuleGroupExecServiceImpl getRuleGroupExecServiceImpl() {
		return ruleGroupExecServiceImpl;
	}

	public void setRuleGroupExecServiceImpl(RuleGroupExecServiceImpl ruleGroupExecServiceImpl) {
		this.ruleGroupExecServiceImpl = ruleGroupExecServiceImpl;
	}

	public DataQualExecServiceImpl getDataQualExecServiceImpl() {
		return dataQualExecServiceImpl;
	}

	public void setDataQualExecServiceImpl(DataQualExecServiceImpl dataQualExecServiceImpl) {
		this.dataQualExecServiceImpl = dataQualExecServiceImpl;
	}

	public DataQualGroupExecServiceImpl getDataQualGroupExecServiceImpl() {
		return dataQualGroupExecServiceImpl;
	}

	public void setDataQualGroupExecServiceImpl(DataQualGroupExecServiceImpl dataQualGroupExecServiceImpl) {
		this.dataQualGroupExecServiceImpl = dataQualGroupExecServiceImpl;
	}

	public MapExecServiceImpl getMapExecServiceImpl() {
		return mapExecServiceImpl;
	}

	public void setMapExecServiceImpl(MapExecServiceImpl mapExecServiceImpl) {
		this.mapExecServiceImpl = mapExecServiceImpl;
	}

	public ProfileExecServiceImpl getProfileExecServiceImpl() {
		return profileExecServiceImpl;
	}

	public void setProfileExecServiceImpl(ProfileExecServiceImpl profileExecServiceImpl) {
		this.profileExecServiceImpl = profileExecServiceImpl;
	}

	public ProfileGroupExecServiceImpl getProfileGroupExecServiceImpl() {
		return profileGroupExecServiceImpl;
	}

	public void setProfileGroupExecServiceImpl(ProfileGroupExecServiceImpl profileGroupExecServiceImpl) {
		this.profileGroupExecServiceImpl = profileGroupExecServiceImpl;
	}

	static final Logger logger = Logger.getLogger(CommonServiceImpl.class);
	private static final String GET = "get";
	private static final String SET = "set";
	
	public boolean csvToParquet(String csvFilePath, String parquetDir) throws Exception {
		// Apply a schema to an RDD of JavaBeans and register it as a table.
		// DataFrame schemaPeople = sqlContext.createDataFrame(people,
		// String.class);
		String appUuid = getApp().getUuid();
		IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
		List<Attribute> attributes = exec.fetchAttributeList(csvFilePath, parquetDir, false, true, appUuid);
		/*List<Attribute> attributes = dataFrameService.getAttributeList(csvFilePath, parquetDir, false, true);*/
		/*DataFrame df = hiveContext.read().format("com.databricks.spark.csv").option("inferSchema", "true")
				.option("header", "true").load(csvFilePath);
		df.printSchema();
		StructType st = df.schema();
		Seq<StructField> seqFields = st.thisCollection();
		Iterator<StructField>  iter= st.iterator();
		List<Attribute> attributes = new ArrayList<Attribute>();
		int i =0;
		while(iter.hasNext()){
			StructField sf = iter.next();
			Attribute attr = new Attribute();
			attr.setAttributeId(i++);
			attr.setType(sf.dataType().typeName());
			attr.setName(sf.name());
			attr.setDesc(sf.name());
			attributes.add(attr);
		
		}
				
		logger.info("Length of seq:"+seqFields.length());*/
		logger.info("Attributes:"+attributes);
		
		Datapod dp = new Datapod();
		dp.setName("stock_load");
		dp.setCache("Y");
		dp.setAttributes(attributes);
		try {
			dp = datapodServiceImpl.save(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Relation relation = new Relation();
		MetaIdentifierHolder mih = new MetaIdentifierHolder();
		mih.setRef(new MetaIdentifier());
		mih.getRef().setUuid(dp.getUuid());
		mih.getRef().setType(MetaType.datapod);
		mih.getRef().setVersion(dp.getVersion());
		relation.setDependsOn(mih);
		try {
			//relation = relationServiceImpl.save(relation);
			save(MetaType.relation.toString(), relation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		//df.write().parquet(parquetDir);
		return true;
	}
	
	public Datapod createDatapodFromCsv(String csvFilePath) throws Exception {

		// Apply a schema to an RDD of JavaBeans and register it as a table.
		// DataFrame schemaPeople = sqlContext.createDataFrame(people,
		// String.class);
		File f= new File(csvFilePath);
		Datapod datapod= datapodServiceImpl.findOneByName(f.getName());
		// If datapod is already present for this filename, then return here
		if(datapod != null) return datapod;
		String parquetDir = null;
		String appUuid = getApp().getUuid();
		IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
		List<Attribute> attributes = exec.fetchAttributeList(csvFilePath, parquetDir, false, false, appUuid);
		/*List<Attribute> attributes = dataFrameService.getAttributeList(csvFilePath, parquetDir, false, false);*/
		
		/*DataFrame df = hiveContext.read().format("com.databricks.spark.csv").option("inferSchema", "true")
				.option("header", "true").load(csvFilePath);
		df.printSchema();
		StructType st = df.schema();
		Seq<StructField> seqFields = st.thisCollection();
		Iterator<StructField>  iter= st.iterator();
		List<Attribute> attributes = new ArrayList<Attribute>();
		int i =0;
		while(iter.hasNext()){
			StructField sf = iter.next();
			Attribute attr = new Attribute();
			attr.setAttributeId(i++);
			attr.setType(sf.dataType().typeName());
			attr.setName(sf.name());
			attr.setDesc(sf.name());
			attributes.add(attr);
		
		}*/
				
		//logger.info("Length of seq:"+seqFields.length());
		logger.info("Attributes:"+attributes);
		
		Datapod dp = new Datapod();
		dp.setName(f.getName());
		dp.setCache("Y");
		dp.setAttributes(attributes);
		try {
			dp = datapodServiceImpl.save(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Relation relation = new Relation();
		MetaIdentifierHolder mih = new MetaIdentifierHolder();
		mih.setRef(new MetaIdentifier());
		mih.getRef().setUuid(dp.getUuid());
		mih.getRef().setType(MetaType.datapod);
		mih.getRef().setVersion(dp.getVersion());
		relation.setDependsOn(mih);
		try {
			//relation = relationServiceImpl.save(relation);
			save(MetaType.relation.toString(), relation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dp;
	}

	public List<BaseEntity> getAll(String type) throws JsonProcessingException{
		List<BaseEntity> newObjectList = new ArrayList<>();
		try { 
			List<T> objectList = findAll(Helper.getMetaType(type));
			for (Object obj : objectList){
				newObjectList.add((BaseEntity) resolveName(obj, Helper.getMetaType(type)));
			}
			return newObjectList;
		} catch (NullPointerException | IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
		  

	
	
	@SuppressWarnings({ "unchecked" })
	public List<T> findAll(MetaType type) {
		String appUuid = null;						
		if (!type.equals(MetaType.user) && !type.equals(MetaType.group)
			&& !type.equals(MetaType.role) && !type.equals(MetaType.privilege)
			&& !type.equals(MetaType.application) && !type.equals(MetaType.meta)) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		try {
			Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(type)).invoke(this);
			if (appUuid == null) {
				return (List<T>)(iDao).getClass().getMethod("findAll").invoke(iDao);
			} else {
				return (List<T>)(iDao).getClass().getMethod("findAll", String.class).invoke(iDao, appUuid);
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List findAllLatest(MetaType type) {
		List objectList = new ArrayList();
		List finalObjectList = new ArrayList();
		java.util.HashMap<String, BaseEntity> objectMap = new java.util.HashMap<>(); 
		BaseEntity baseEntity = null;
		String appUuid = null;	
		if (!type.equals(MetaType.user) && !type.equals(MetaType.group)
				&& !type.equals(MetaType.role) && !type.equals(MetaType.privilege)
				&& !type.equals(MetaType.application)) {
				appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		try {
			Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(type)).invoke(this);
			if (appUuid == null) {
				objectList = (List)(iDao).getClass().getMethod("findAll").invoke(iDao);
			} else {
				objectList = (List)(iDao).getClass().getMethod("findAll", String.class).invoke(iDao, appUuid);
			}
			//List<BaseEntity> baseEntityList = getBaseEntityList(objectList);
			
			for (int i = 0; i < objectList.size(); i++) {
				baseEntity = BaseEntity.class.cast(objectList.get(i));
				if (objectMap.containsKey(baseEntity.getUuid())) {
					if (Long.parseLong(baseEntity.getVersion()) > Long.parseLong(objectMap.get(baseEntity.getUuid()).getVersion())) {
						objectMap.put(baseEntity.getUuid(), baseEntity);
					}
				} else {
					objectMap.put(baseEntity.getUuid(), baseEntity);
				}
			}
			for (String uuid : objectMap.keySet()) {
				finalObjectList.add(Helper.getDomainClass(type).cast(objectMap.get(uuid)));
				//finalObjectList.add(objectMap.get(uuid));
			}
			return finalObjectList;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<BaseEntity> getBaseEntityList(List<BaseEntity> objectList) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		
		List<BaseEntity> baseEntityList = new ArrayList<>();

		for (BaseEntity objDet : objectList) {
			BaseEntity baseEntity = new BaseEntity();
			baseEntity.setId(objDet.getId());
			baseEntity.setUuid(objDet.getUuid());
			baseEntity.setVersion(objDet.getVersion());
			baseEntity.setName(objDet.getName());
			baseEntity.setDesc(objDet.getDesc());
			baseEntity.setCreatedBy(objDet.getCreatedBy());
			baseEntity.setCreatedOn(objDet.getCreatedOn());
			baseEntity.setTags(objDet.getTags());
			baseEntity.setActive(objDet.getActive());
			baseEntity.setPublished(objDet.getPublished());
			baseEntity.setAppInfo(objDet.getAppInfo());
			baseEntityList.add(baseEntity);			
		}
		return baseEntityList;
	}
	
	public List<BaseEntity> resolveBaseEntityList(List<BaseEntity> baseEntityList) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {

		 if (baseEntityList == null)
			 return null;
		 
		 List<BaseEntity> baseEntityListNew = new ArrayList<>();
		 for (BaseEntity be : baseEntityList) {			 
			 baseEntityListNew.add(metadataServiceImpl.resolveBaseEntity(be));			 
		 }
		 
		 return baseEntityListNew;		 
	 }

	@SuppressWarnings("rawtypes")
	public Object resolveName(Object object, MetaType type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ParseException, java.text.ParseException, NullPointerException, JsonProcessingException {
		if(object != null) {
			Method [] methodList = object.getClass().getMethods();
			ArrayList listObj = null;
			Class [] interfaces = null;
			String name = null;
			String attrId = null;
			try{
				for (Method method : methodList) {
					if (!method.getName().startsWith(GET) || method.getParameterCount() > 0) {
						continue;
					}
					//logger.info(" GET method name : " + method.getName());
					if (object instanceof MetaIdentifier) {
						type = (MetaType) object.getClass().getMethod(GET+"Type").invoke(object);
					}					
					if (method.getName().contains("Uuid")) {
						//logger.info(" Inside resolveName : " + type);
						name = resolveName((String)object.getClass().getMethod(GET+"Uuid").invoke(object), (String)object.getClass().getMethod(GET+"Version").invoke(object), type);
						object.getClass().getMethod(SET+"Name", String.class).invoke(object, name);
						name = null;
						continue;
					}
					if (method.getName().contains("UUID")) {
						//logger.info(" Inside resolveName : " + type);
						name = resolveName((String)object.getClass().getMethod(GET+"UUID").invoke(object), (String)object.getClass().getMethod(GET+"Version").invoke(object), type);
						object.getClass().getMethod(SET+"Name", String.class).invoke(object, name);
						name = null;
						continue;
					}
					if ((method.getName().contains("AttrId") || method.getName().contains("AttributeId")) && method.getName().startsWith(GET))  {
						//attrId = String.class.cast(method.invoke(object));
						Object attributeId = method.invoke(object); 
						if(attributeId != null)
							attrId = attributeId.toString();
						/*else
							logger.info("resolveName method: attributeId is null for the Object " + type);*/
						for (Method innerMethod : methodList) {
							if (innerMethod.getName().startsWith(SET + "Attr") /*|| innerMethod.getName().startsWith(SET + "Attribute")*/ && innerMethod.getName().contains("Name")) {
								innerMethod.invoke(object, resolveAttributeName(attrId, object));
							}
						}	
					}					
					
					if(object instanceof ParamSet) {
						ParamSet paramSet = (ParamSet) object;
						List<ParamInfo> paramInfo = paramSet.getParamInfo();
						List<ParamListHolder> paramSetVal = null;
						List<ParamInfo> paramInfos = new ArrayList<>();
						for(ParamInfo info : paramInfo) {
							paramSetVal = info.getParamSetVal();
							List<ParamListHolder> paramListHolders = new ArrayList<>();
							for(ParamListHolder paramListHolder : paramSetVal) {
								ParamList paramList = (ParamList) getLatestByUuid(paramListHolder.getRef().getUuid(), paramListHolder.getRef().getType().toString());
								for(Param param : paramList.getParams()) 
									if(paramListHolder.getParamId().equalsIgnoreCase(param.getParamId()))
										paramListHolder.setParamName(param.getParamName());
									
								paramListHolders.add(paramListHolder);
							}
							info.setParamSetVal(paramListHolders);
							paramInfos.add(info);
						}
						paramSet.setParamInfo(paramInfos);
						object = paramSet;
					}
					/*if (method.getName().contains("ParamListInfo") && method.getName().startsWith(GET) && (object instanceof ExecParams))  {
						System.out.println();
						List<ParamListHolder> paramListInfo = (List<ParamListHolder>) method.invoke(object);
						System.out.println();
					}*/
					if ((method.getName().contains("OperatorParams") || (object instanceof ExecParams)) && method.getName().startsWith(GET))  {
						if(method.getName().contains("OperatorParams")){
							@SuppressWarnings("unchecked")
							HashMap<String, Object> operatorParams = (HashMap<String, Object>) method.invoke(object);
							if(operatorParams != null) {
								if(operatorParams.containsKey("EXEC_PARAMS")) {
									ObjectMapper mapper = new ObjectMapper();
									ExecParams execParams = mapper.convertValue(operatorParams.get("EXEC_PARAMS"), ExecParams.class);
									execParams = resolveExecParams(execParams);
									operatorParams.put("EXEC_PARAMS", execParams);
								}
							}
						} else if((object instanceof ExecParams)) {
							object = resolveExecParams((ExecParams)object);
						}
						
					}
					
					if ((method.getName().contains("FeatureAttrMap")) && object instanceof Train && method.getName().startsWith(GET))  {
						@SuppressWarnings("unchecked")
						List<FeatureAttrMap> featureAttrMap = (List<FeatureAttrMap>) method.invoke(object);
						object = resolveFeatureAttrMap(featureAttrMap, object);
					}
					 
					if ((method.getName().contains("ParamListInfo")) && method.getName().startsWith(GET)){
						ParamListHolder paramListHolder = (ParamListHolder) method.invoke(object);
						ParamList paramList = (ParamList) getLatestByUuid(paramListHolder.getRef().getUuid(), paramListHolder.getRef().getType().toString());
						for(Param param : paramList.getParams()) {							
							if(paramListHolder.getParamId().equalsIgnoreCase(param.getParamId()))
								paramListHolder.setParamName(param.getParamName());
						}
						object = object.getClass().getMethod(SET+"ParamListInfo", List.class).invoke(object, paramListHolder);
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
						for (Class<?> interface1 : interfaces) {
							if (interface1.getName().equals("java.util.List")) {
								listObj = (ArrayList)invokedObj;
								for (Object arrayObj : listObj) {
									if (arrayObj.getClass().getPackage().getName().contains("inferyx")) {
										resolveName(arrayObj, null);
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
					
					resolveName(invokedObj, type);
				}
			}catch (NullPointerException | NoSuchMethodException e) {
				//e.printStackTrace();
			}
			return object;
		}
		return object;
	}
	
	/**
	 * @Ganesh
	 *
	 * @param operatorParams
	 * @return
	 * @throws JsonProcessingException 
	 */
	private ExecParams resolveExecParams(ExecParams execParams) throws JsonProcessingException {
				List<ParamListHolder> paramListInfo= execParams.getParamListInfo();
				
				if(paramListInfo != null)
					for(ParamListHolder holder : paramListInfo) {
						MetaIdentifier ref = holder.getRef();
						if(ref != null) {
							ParamList paramList = (ParamList) getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString());
							for(Param param : paramList.getParams())
								if(param.getParamId().equalsIgnoreCase(holder.getParamId())) {
									holder.setParamName(param.getParamName());
									holder.setParamType(param.getParamType());
								}
						}
						List<AttributeRefHolder> attributeInfo = holder.getAttributeInfo();
						if(attributeInfo != null) {
							for(AttributeRefHolder attributeRefHolder : attributeInfo) {
								MetaIdentifier attrRef = attributeRefHolder.getRef();
								if(attrRef != null) {
									Object attrRefObj = getOneByUuidAndVersion(attrRef.getUuid(), attrRef.getVersion(), attrRef.getType().toString());
									if(attrRefObj instanceof Datapod) {
										Datapod datapod = (Datapod) attrRefObj;
										
										for(Attribute attribute : datapod.getAttributes()) {
											if(attribute.getAttributeId().equals(Integer.parseInt(""+attributeRefHolder.getAttrId()))) {
												attributeRefHolder.setAttrName(attribute.getName());
											}
										}
										attributeRefHolder.getRef().setName(datapod.getName());
									} else if(attrRefObj instanceof DataSet) {
										DataSet dataSet = (DataSet) attrRefObj;
										
										for(AttributeSource attributeSource : dataSet.getAttributeInfo()) {
											if(attributeSource.getAttrSourceId().equalsIgnoreCase(""+attributeRefHolder.getAttrId())) {
												attributeRefHolder.setAttrName(attributeSource.getAttrSourceName());
											}
										}
										attributeRefHolder.getRef().setName(dataSet.getName());
									} else if(attrRefObj instanceof Rule) {
										Rule rule = (Rule) attrRefObj;

										for(AttributeSource attributeSource : rule.getAttributeInfo()) {
											if(attributeSource.getAttrSourceId().equalsIgnoreCase(""+attributeRefHolder.getAttrId())) {
												attributeRefHolder.setAttrName(attributeSource.getAttrSourceName());
											}
										}
										attributeRefHolder.getRef().setName(rule.getName());
									}
									
								}
							}
						}
					}				
		return execParams;
	}

	public T resolveName(String uuid, String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ParseException, java.text.ParseException, NullPointerException, JsonProcessingException {
		return getAllByUuid(uuid, type);
	}
		
	/*public String resolveName(String uuid, String version, MetaType type) throws ParseException, java.text.ParseException {
		List<BaseEntity> baseEntityList = null;
		baseEntityList = metadataServiceImpl.getBaseEntityByCriteria(type.toString(), null, null, null, null, null, null, uuid, version);
		if (baseEntityList == null || baseEntityList.isEmpty()) {
				return null;
			}
			//logger.info(" Inside resolveName : " + baseEntityList.get(0).getName());
			return baseEntityList.get(0).getName();
		}*/

	public String resolveName(String uuid, String version, MetaType type) throws ParseException, java.text.ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		if(type == MetaType.simple)
			return null;
		
		Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(type)).invoke(this);
		BaseEntity baseEntity = null;
		if (version != null)
			baseEntity = (BaseEntity) (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class).invoke(iDao,uuid,version);
		else{
			baseEntity = (BaseEntity) (iDao).getClass().getMethod("findLatestByUuid", String.class, Sort.class).invoke(iDao,uuid,new Sort(Sort.Direction.DESC, "version"));
		}
			return baseEntity.getName();
		}
	
	public String resolveAttributeName(String attributeId, Object object) throws JsonProcessingException {
		Method [] methodList = object.getClass().getMethods();
		Object attrObj = null;
		MetaType type = null;
		String uuid = null;
		String version = null;
		Object invokedObj = null;
		String attributeName = null;
		try{
			for (Method method : methodList) {
				if (!method.getName().startsWith(GET) || method.getParameterCount() > 0) {
					continue;
				}
				
				if (method.getName().contains("Uuid")) {
					type = (MetaType) object.getClass().getMethod(GET+"Type").invoke(object);
					if(type == null)
						continue;
					uuid = (String) object.getClass().getMethod(GET+"Uuid").invoke(object);
					version = (String) object.getClass().getMethod(GET+"Version").invoke(object);
					if (StringUtils.isBlank(uuid)) {
						continue;
					}
					//logger.info("uuid : version : type : " + uuid + ":" + version + ":" + type);
					attrObj = Helper.getDomainClass(type).cast(miUtil.getRefObject(new MetaIdentifier(type, uuid, version)));
					if (type.equals(MetaType.datapod) || type.equals(MetaType.rule) || type.equals(MetaType.dataset)) {
						attributeName = String.class.cast(attrObj.getClass().getMethod("getAttributeName", Integer.class).invoke(attrObj, Integer.parseInt(attributeId)));
						return attributeName;
					} else if (type.equals(MetaType.paramlist)) {
						if(attrObj instanceof ParamList) {
							ParamList paramList = (ParamList) attrObj;
							for(Param param : paramList.getParams()) {
								if(param.getParamId().equalsIgnoreCase(attributeId))
									return param.getParamName();
							}
						}
					} else {
						return null;
					}
				}
				
				invokedObj = method.invoke(object);
				if (invokedObj == null || invokedObj.getClass().isPrimitive()) {
					continue;
				}
				
				if (!invokedObj.getClass().getPackage().getName().contains("inferyx")) {
					continue;
				}
				attributeName = resolveAttributeName(attributeId, invokedObj);
				if (attributeName != null && StringUtils.isNotBlank(attributeName)) {
					return attributeName;
				}
			}
		}catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
		}
		
		return attributeName;
	}
		

	@SuppressWarnings({ "unchecked"})
	public List<BaseEntity> getAllVersionByUuid(String uuid, String type) throws JsonProcessingException, NullPointerException, ParseException {
		//String result = "";
		String appUuid = null;
		if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
			&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
			&& !type.equalsIgnoreCase(MetaType.application.toString())) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		List<BaseEntity> objectList = null;
		MetaType metaType = Helper.getMetaType(type);
		try {
				Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);
				if (appUuid != null) {
					objectList = (List<BaseEntity>)(iDao).getClass().getMethod("findAllVersion", String.class, String.class).invoke(iDao, appUuid, uuid);
				} else
					objectList = (List<BaseEntity>)(iDao).getClass().getMethod("findAllVersion", String.class).invoke(iDao, uuid);
				
				return getBaseEntityList(objectList);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isExists(MetaType type, String id) {
		Object iDao = null;
		try {
			iDao = this.getClass().getMethod(GET+Helper.getDaoClass(type)).invoke(this);
			return Boolean.class.cast((iDao).getClass().getMethod("exists", String.class).invoke(iDao, id));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<BaseEntity> getAllLatest(String type, String active)  throws JsonProcessingException, ParseException {
		MetaType metaType = Helper.getMetaType(type);
		List<BaseEntity> objectList = new ArrayList<>();
		try {			
			Aggregation aggr = null;
			if (active == "Y")
				 aggr = newAggregation(match(Criteria.where("active").is("Y")),
					match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
			else
				 aggr = newAggregation(
					match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
				

			String appUuid = null;
			if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
				&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
				&& !type.equalsIgnoreCase(MetaType.application.toString())) {
				appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
			}
			@SuppressWarnings("rawtypes")
			AggregationResults results = mongoTemplate.aggregate(aggr, type.toString().toLowerCase(), Helper.getDomainClass(metaType));
			List<T> metaList = results.getMappedResults();
			Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);
				
			for (int i = 0; i < metaList.size(); i++) {
				Object object = null;
				if (appUuid != null) {
					object = (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class, String.class).invoke(iDao, appUuid, ((BaseEntity)metaList.get(i)).getId(), ((BaseEntity)metaList.get(i)).getVersion());
				} else {
					object = (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class).invoke(iDao, ((BaseEntity)metaList.get(i)).getId(), ((BaseEntity)metaList.get(i)).getVersion());
				}
				if (object != null) {
					objectList.add((BaseEntity) Helper.getDomainClass(metaType).cast(object));
				}
			}
			return getBaseEntityList(objectList);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public T getAllByUuid(String uuid, String type) throws JsonProcessingException{
		String appUuid = null;
		if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
			&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
			&& !type.equalsIgnoreCase(MetaType.application.toString())) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		MetaType metaType = Helper.getMetaType(type);
		Object object = null;
		try {
				Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);
				if (appUuid != null) {
					object = (T)(iDao).getClass().getMethod("findAllByUuid", String.class, String.class).invoke(iDao, appUuid, uuid);
				} else
					object = (T)(iDao).getClass().getMethod("findLatestByUuid", String.class, Sort.class).invoke(iDao, uuid, new Sort(Sort.Direction.DESC, "version"));
			return (T) resolveName(object, Helper.getMetaType(type));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BaseEntity delete(String id, String type) {
		String appUuid = null;
		if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
			&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
			&& !type.equalsIgnoreCase(MetaType.application.toString())) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		MetaType metaType = Helper.getMetaType(type);
		Object iDao = null;
		try {
			iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);//finds respective Dao type
			Object obj = null;
			if (appUuid != null)
				obj = Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findOneById", String.class, String.class).invoke(iDao, appUuid, id));
			else
				obj = Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findOneById", String.class).invoke(iDao, id));
			
			Helper.getDomainClass(metaType).getMethod("setActive", String.class).invoke(obj, "N");
			return (BaseEntity) resolveName(save(type, obj), Helper.getMetaType(type));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException  e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
	public BaseEntity restore(String id, String type) {
		String appUuid = null;
		if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
			&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
			&& !type.equalsIgnoreCase(MetaType.application.toString())) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		MetaType metaType = Helper.getMetaType(type);
		Object iDao = null;
		try {
			iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);//finds respective Dao type
			Object obj = null;
			if (appUuid != null)
				obj = Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findOneById", String.class, String.class).invoke(iDao, appUuid, id));
			else
				obj = Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findOneById", String.class).invoke(iDao, id));
			
			Helper.getDomainClass(metaType).getMethod("setActive", String.class).invoke(obj, "Y");
			return (BaseEntity) resolveName(save(type, obj), Helper.getMetaType(type));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException  e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public T save(String type, Object object) throws JsonProcessingException, JSONException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		try{		
			if(type.equalsIgnoreCase(MetaType.log.toString())){
			Log log= mapper.convertValue(object, Log.class);
			return (T) logServiceImpl.save(log);
			}
			MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
			List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
			MetaType metaType = Helper.getMetaType(type);
			
			metaIdentifierHolderList.add(meta);
			BaseEntity objDet = null;

			Object metaObj = mapper.convertValue(object,Helper.getDomainClass(metaType));
			Helper.getDomainClass(metaType).getMethod("setAppInfo", List.class).invoke(metaObj, metaIdentifierHolderList);
			Helper.getDomainClass(metaType).getSuperclass().getMethod("setBaseEntity").invoke(metaObj);
				
			Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);
			objDet = (BaseEntity)(iDao.getClass().getMethod("save", Object.class).invoke(iDao, metaObj));
			registerGraph.updateGraph((Object) objDet, metaType);			
				
			BaseEntity baseEntity = new BaseEntity();
			baseEntity.setId(objDet.getId());
			baseEntity.setUuid(objDet.getUuid());
			baseEntity.setVersion(objDet.getVersion());
			baseEntity.setName(objDet.getName());
			baseEntity.setDesc(objDet.getDesc());
			baseEntity.setCreatedBy(objDet.getCreatedBy());
			baseEntity.setCreatedOn(objDet.getCreatedOn());
			baseEntity.setTags(objDet.getTags());
			baseEntity.setActive(objDet.getActive());
			baseEntity.setPublished(objDet.getPublished());
			baseEntity.setAppInfo(objDet.getAppInfo());
			return (T) baseEntity;
		}catch (ParseException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | IOException | JSONException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public BaseEntity saveAs(String uuid, String version, String type) throws JsonProcessingException {
		String appUuid = null;
		if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
			&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
			&& !type.equalsIgnoreCase(MetaType.application.toString())) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		 MetaType metaType = Helper.getMetaType(type);		 
		 try{
			 Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);
			 T metaObj = null;
			 if (appUuid != null)
				 metaObj =   (T) Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class, String.class).invoke(iDao, appUuid, uuid, version));
			else
				metaObj =   (T) Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class).invoke(iDao, uuid, version));
					
			T metaObjNew = metaObj;			
			BaseEntity be = new BaseEntity();
			be.setBaseEntity();
			//String createdBy = securityServiceImpl.getuserInfo().getRef().getName();
			Helper.getDomainClass(metaType).getMethod("setId", String.class).invoke(metaObjNew,be.getId());
			Helper.getDomainClass(metaType).getMethod("setUuid", String.class).invoke(metaObjNew,be.getUuid());
			Helper.getDomainClass(metaType).getMethod("setVersion", String.class).invoke(metaObjNew,be.getVersion());
			Helper.getDomainClass(metaType).getMethod("setCreatedBy", MetaIdentifierHolder.class).invoke(metaObjNew,be.getCreatedBy());
			Helper.getDomainClass(metaType).getMethod("setCreatedOn", String.class).invoke(metaObjNew,be.getCreatedOn());
			Helper.getDomainClass(metaType).getMethod("setName", String.class).invoke(metaObjNew, (Helper.getDomainClass(metaType).getMethod("getName").invoke(metaObj)+"_copy"));
			Helper.getDomainClass(metaType).getMethod("setActive", String.class).invoke(metaObjNew, Helper.getDomainClass(metaType).getMethod("getActive").invoke(metaObj));
			Helper.getDomainClass(metaType).getMethod("setDesc", String.class).invoke(metaObjNew, Helper.getDomainClass(metaType).getMethod("getDesc").invoke(metaObj));
			Helper.getDomainClass(metaType).getMethod("setTags", String[].class).invoke(metaObjNew, Helper.getDomainClass(metaType).getMethod("getTags").invoke(metaObj));

			BaseEntity baseEntity = (BaseEntity) save(type, metaObjNew);
			return (BaseEntity) resolveName(baseEntity, Helper.getMetaType(type));
		 }
		 catch(NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException  e){
		 e.printStackTrace();	
		 } catch (Exception e) {
			e.printStackTrace();
		}
		 return null;
	}

	@SuppressWarnings("unchecked")
	public T getOneById(String id, String type) throws JsonProcessingException {
		String appUuid = null;
		if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
			&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
			&& !type.equalsIgnoreCase(MetaType.application.toString())) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		MetaType metaType = Helper.getMetaType(type);
		Object iDao = null;
		try{
			T object = null;
			iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);
			if (appUuid == null) 
				object = (T) iDao.getClass().getMethod("findOneById", String.class,String.class).invoke(iDao, appUuid,id);	
			else
				object = (T) iDao.getClass().getMethod("findOneById", String.class).invoke(iDao,id);
							
			return (T) resolveName(object, Helper.getMetaType(type));
		} catch (IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ParseException e) {
			
			e.printStackTrace();
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	public T getOneByUuidAndVersion(String uuid, String version, String type) throws JsonProcessingException {
		String appUuid = null;
		if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
			&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
			&& !type.equalsIgnoreCase(MetaType.application.toString())) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		Object iDao = null;
		MetaType metaType = Helper.getMetaType(type);
		try{
			T object = null;
			iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);
			
			if (appUuid != null){
				if(StringUtils.isBlank(version))
					object = (T) iDao.getClass().getMethod("findLatestByUuid", String.class,String.class,Sort.class).invoke(iDao,appUuid, uuid,new Sort(Sort.Direction.DESC, "version"));
				else
					object = (T) iDao.getClass().getMethod("findOneByUuidAndVersion", String.class, String.class, String.class).invoke(iDao, appUuid, uuid,version);
			}else{
				if(StringUtils.isBlank(version))
					object = (T) iDao.getClass().getMethod("findLatestByUuid", String.class,Sort.class).invoke(iDao, uuid,new Sort(Sort.Direction.DESC, "version"));	
				else
					object = (T) iDao.getClass().getMethod("findOneByUuidAndVersion", String.class,String.class).invoke(iDao, uuid,version);
			}
			return (T) resolveName(object, Helper.getMetaType(type));
		} catch (IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ParseException e) {
			
			e.printStackTrace();
		}
		return null;
	}
		
		@SuppressWarnings("unchecked")
		public T getiDAO(MetaType type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
			Object iDao = this.getClass().getMethod(GET + Helper.getDaoClass(MetaType.application)).invoke(this);
			return (T) iDao;
		}
		
		@SuppressWarnings("unchecked")
		public T getLatestByUuid(String uuid, String type) throws JsonProcessingException {
			try {
				String appUuid = null;						
				if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
					&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
					&& !type.equalsIgnoreCase(MetaType.application.toString())) {
					appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
				}
				MetaType metaType = Helper.getMetaType(type);
				Object object = null;
				Object iDao = this.getClass().getMethod(GET + Helper.getDaoClass(metaType)).invoke(this);
				if(appUuid == null) 
					object = (T) Helper.getDomainClass(metaType).cast(iDao.getClass().getMethod("findLatestByUuid",String.class,Sort.class ).invoke(iDao, uuid,new Sort(Sort.Direction.DESC, "version")));	
				else 			    
					object = (T) Helper.getDomainClass(metaType).cast(iDao.getClass().getMethod("findLatestByUuid", String.class,String.class,Sort.class).invoke(iDao, appUuid,uuid, new Sort(Sort.Direction.DESC, "version")));
				
				return (T) resolveName(object, Helper.getMetaType(type));
			} catch (IllegalAccessException 
							| IllegalArgumentException 
							| InvocationTargetException
							| NoSuchMethodException 
							| SecurityException 
							| ParseException e) {
				e.printStackTrace();
			}
			return null;
		}		

		@SuppressWarnings("unchecked")
		public T getLatestByUuid(String uuid, String type, String resolveFlag) throws JsonProcessingException {
			try {
				String appUuid = null;
				if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
					&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
					&& !type.equalsIgnoreCase(MetaType.application.toString()))
				{
					appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
				}

				MetaType metaType = Helper.getMetaType(type);
				Object object = null;
				Object iDao = this.getClass().getMethod(GET + Helper.getDaoClass(metaType)).invoke(this);
				if(appUuid == null) 
					object = (T) Helper.getDomainClass(metaType).cast(iDao.getClass().getMethod("findLatestByUuid",String.class,Sort.class ).invoke(iDao, uuid,new Sort(Sort.Direction.DESC, "version")));	
				else 			    
					object = (T) Helper.getDomainClass(metaType).cast(iDao.getClass().getMethod("findLatestByUuid", String.class,String.class,Sort.class).invoke(iDao, appUuid,uuid, new Sort(Sort.Direction.DESC, "version")));
				
				if (resolveFlag.equalsIgnoreCase("Y"))
					return (T) resolveName(object, Helper.getMetaType(type));
				else
					return (T) object;

			} catch (IllegalAccessException 
							| IllegalArgumentException 
							| InvocationTargetException
							| NoSuchMethodException 
							| SecurityException 
							| ParseException e) {
				e.printStackTrace();
			}
			return null;
		}		
		
		@SuppressWarnings("unchecked")
		public T getAsOf(String uuid, String asOf, String type) throws JsonProcessingException {
			String appUuid = null;
			if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
				&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
				&& !type.equalsIgnoreCase(MetaType.application.toString())) {
				appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
			}
			MetaType metaType = Helper.getMetaType(type);
			try{
			T object = null;
			Object iDao = this.getClass().getMethod(GET + Helper.getDaoClass(metaType)).invoke(this);
			if (appUuid != null) {
				object = (T) Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findAsOf", String.class, String.class, String.class, Sort.class).invoke(iDao, appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version")));
			} else
				object = (T) Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findAsOf", String.class, String.class, Sort.class).invoke(iDao, uuid, asOf, new Sort(Sort.Direction.DESC, "version")));
		
			return (T) resolveName(object, Helper.getMetaType(type));
			}
			catch(NullPointerException 
					| IllegalArgumentException   
					| SecurityException 
					| IllegalAccessException 
					| InvocationTargetException 
					| NoSuchMethodException 
					| ParseException  e){
			e.printStackTrace();
			}
			return null;
			}
	
	@SuppressWarnings("unchecked")
	public List<BaseEntity> getList(String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<? extends BaseEntity> objectList = (List<? extends BaseEntity>) findAll(Helper.getMetaType(type));
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		
		for(BaseEntity baseObject : objectList){
			BaseEntity baseEntity = new BaseEntity();
			String id = baseObject.getId();
			String uuid = baseObject.getUuid();
			String version = baseObject.getVersion();
			String name = baseObject.getName();
			String desc = baseObject.getDesc();
			MetaIdentifierHolder createdBy = baseObject.getCreatedBy();
			String createdOn = baseObject.getCreatedOn();
			String[] tags = baseObject.getTags();
			String active = baseObject.getActive();
			List<MetaIdentifierHolder> appInfo = baseObject.getAppInfo();
			baseEntity.setId(id);
			baseEntity.setUuid(uuid);
			baseEntity.setVersion(version);
			baseEntity.setName(name);
			baseEntity.setDesc(desc);
			baseEntity.setCreatedBy(createdBy);
			baseEntity.setCreatedOn(createdOn);
			baseEntity.setTags(tags);
			baseEntity.setActive(active);
			baseEntity.setPublished(baseObject.getPublished());
			baseEntity.setAppInfo(appInfo);
			baseEntityList.add(baseEntity);
		}
		
		return resolveBaseEntityList(baseEntityList);
	}
	
	public List<MetaStatsHolder> getMetaStats(String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ParseException, JsonProcessingException {
		logger.info("Inside getMetaStats - type : " + type);
		String appUuid = null;
//		if ((type != null)&&(!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
//			&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
//			&& !type.equalsIgnoreCase(MetaType.application.toString()))) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
//		}
		List<MetaStatsHolder> countHolder = new ArrayList<>();
		List<MetaType> metaTypes = MetaType.getMetaList();
		if(type == null){
			for(MetaType mType : metaTypes){//logger.info("MetaType: "+mType+"\n");
				long count = 0;
				Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(Helper.getMetaType(mType.toString().toLowerCase()))).invoke(this);
				if (appUuid == null) {
					//count = (long) iDao.getClass().getMethod("count").invoke(iDao);
					count = metadataServiceImpl.getBaseEntityByCriteria(mType.toString(), null, null, null, null, null, null, null, null, null).size();
           
				}
				
				else{
					/*Query query = new Query();
					query.addCriteria(Criteria.where("appInfo.ref.uuid").is(appUuid));    
					count = mongoTemplate.count(query, Helper.getDomainClass(Helper.getMetaType(mType.toString().toLowerCase())));*/
				
					count = metadataServiceImpl.getBaseEntityByCriteria(mType.toString(), null, null, null, null, null, null, null, null, null).size();
					//count = getAllLatest(mType.toString(), "Y").size();
				}
				if(count > 0){
					Object metaObj = iDao.getClass().getMethod("findLatest", Sort.class).invoke(iDao, new Sort(Sort.Direction.DESC, "version"));
					Object metaobjNew = metadataServiceImpl.resolveBaseEntity((BaseEntity) metaObj);
					Object createdBy = metaobjNew.getClass().getMethod("getCreatedBy").invoke(metaobjNew);
					Object ref = createdBy.getClass().getMethod("getRef").invoke(createdBy);
					String nameLastUpdatedBy = (String) ref.getClass().getMethod("getName").invoke(ref);
					String lastUpdatedOn = (String) metaobjNew.getClass().getMethod("getCreatedOn").invoke(metaobjNew);
					countHolder.add(new MetaStatsHolder(mType.toString().toLowerCase(), Long.toString(count), nameLastUpdatedBy, lastUpdatedOn));
					if(mType.toString().equalsIgnoreCase(MetaType.paramlist.toString())){
						count= metadataServiceImpl.getParamList(MetaType.rule.toString(), MetaType.paramlist.toString(), null, null, null, null, null, null, null, null, null).size();
						countHolder.add(new MetaStatsHolder("paramlistrule", Long.toString(count), nameLastUpdatedBy, lastUpdatedOn));
						count= metadataServiceImpl.getParamList(MetaType.model.toString(), MetaType.paramlist.toString(), null, null, null, null, null, null, null, null, null).size();
						countHolder.add(new MetaStatsHolder("paramlistmodel", Long.toString(count), nameLastUpdatedBy, lastUpdatedOn));
			
					}	
				}				
			}
		}else{
			MetaType metaType = Helper.getMetaType(type);
			long count = 0;
			Object iDao = this.getClass().getMethod(GET + Helper.getDaoClass(metaType)).invoke(this);
			if (appUuid == null) {
				count = (long) iDao.getClass().getMethod("count").invoke(iDao);
			}else{
				/*Query query = new Query();
				query.addCriteria(Criteria.where("appInfo.ref.uuid").is(appUuid)); 
				count = mongoTemplate.count(query, Helper.getDomainClass(metaType));*/
				count = metadataServiceImpl.getBaseEntityByCriteria(type, null, null, null, null, null, null, null, null, null).size();
			}
			countHolder.add(new MetaStatsHolder(type, Long.toString(count), null, null));
		}
		return countHolder;	 
	}
	
	private List<Status> setNotStartedStatus (List<Status> statusList) {
		if (Helper.getLatestStatus(statusList) != null 
				&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
			logger.info("Latest Status is not in InProgress/Completed/OnHold. Exiting... ");
			return statusList;
		}
		
		if (statusList == null || statusList.isEmpty()) {
			statusList = new ArrayList<Status>();
			statusList.add(new Status(Status.Stage.NotStarted, new Date()));
		} else if (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Resume, new Date())) 
					|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Failed, new Date()))
					||Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Killed, new Date()))) {
				statusList.add(new Status(Status.Stage.InProgress, new Date()));
		}
		return statusList;
	}
	
	private List<Status> setOnHoldStatus (List<Status> statusList) {
		if (!Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.NotStarted, new Date()))) {
			logger.info("Latest Status is not in NotStarted. Exiting...");
			return statusList;
		}
		statusList.add(new Status(Status.Stage.OnHold, new Date()));
		return statusList;
	}

	private List<Status> setResumeStatus (List<Status> statusList) {
		if (!Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date()))) {
			logger.info("Latest Status is not in OnHold. Exiting...");
			return statusList;
		}
		statusList.add(new Status(Status.Stage.Resume, new Date()));
		return statusList;
	}
	
	private List<Status> setInProgressStatus (List<Status> statusList) {
		if (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Resume, new Date())) 
					|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Failed, new Date())) 
					|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.NotStarted, new Date())) 
					|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Killed, new Date())) 
					) {
				statusList.add(new Status(Status.Stage.InProgress, new Date()));
			}
		else {
			logger.info("Latest Status is not in Resume/Failed/NotStarted/Completed. Exiting...");
		}
		return statusList;
	}
	
	private List<Status> setFailedStatus (List<Status> statusList) {
		/*if (!Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date()))) {
			logger.info("Latest Status is not in InProgress. Exiting...");
			return statusList;
		}*/
		if (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date()))) {
			logger.info("Latest Status is not in Completed. Exiting...");
			return statusList;
		}
		Status failedStatus = new Status(Status.Stage.Failed, new Date());
		if (Helper.getLatestStatus(statusList).equals(failedStatus)) {
			statusList.remove(statusList.size()-1);
		}
		statusList.add(failedStatus);
		return statusList;
	}
	
	private List<Status> setCompletedStatus (List<Status> statusList) {
		if (!Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date()))
			&& !Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Terminating, new Date()))) {
			logger.info("Latest Status is not in InProgress. Exiting...");
			return statusList;
		}
		statusList.add(new Status(Status.Stage.Completed, new Date()));
		return statusList;
	}
	
	private List<Status> setTerminatingStatus (List<Status> statusList) {
		if (!Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date()))){
			logger.info("Latest Status is not in InProgress. Exiting...");
			return statusList;
		}
		statusList.add(new Status(Status.Stage.Terminating, new Date()));
		return statusList;
	}
	
	private List<Status> setKilledStatus (List<Status> statusList) {
		if (!Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Terminating, new Date()))) {
			logger.info("Latest Status is not in Terminating. Exiting...");
			return statusList;
		}
		statusList.add(new Status(Status.Stage.Killed, new Date()));
		return statusList;
	}
	/**
	 * Sets status of Meta Exec objects
	 * @param uuid
	 * @param version
	 * @param type
	 * @param status
	 * @return
	 */
//	public List<Status> setMetaStatus (String uuid, String version, MetaType metaType, Status.Stage stage) {
//		Object object = null;
//		Object iDao = null;
//		List<Status> statusList = null;
//		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
//				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
//		try {
//			// Get the object
//			iDao = this.getClass().getMethod(GET + Helper.getDaoClass(metaType)).invoke(this);
//			if (appUuid != null) {
//				object = (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class, String.class).invoke(iDao, appUuid, uuid, version);
//			} else {
//				object = (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class).invoke(iDao, uuid, version);
//			}
//			statusList = (List<Status>) Helper.getDomainClass(metaType).getMethod(GET+"Status").invoke(object);
//		} catch(NullPointerException  | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e){
//			e.printStackTrace();
//		}
//		
//		switch (stage) {
//			case NotStarted:
//				statusList = setNotStartedStatus(statusList);
//				break;
//			case InProgress:
//				statusList = setInProgressStatus(statusList);
//				break;
//			case Failed:
//				statusList = setFailedStatus(statusList);
//				break;
//			case Completed:
//				statusList = setCompletedStatus(statusList);
//				break;
//			default:
//				break;
//		}
//		return statusList;
//	}
	
	
	/**
	 * Set Meta Status for stage
	 * @param dagExec
	 * @param retObj
	 * @param stage
	 * @param stageId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Object setMetaStatusForStage (DagExec dagExec, Object retObj, Status.Stage stage, String stageId) throws Exception {
		String uuid = dagExec.getUuid();
		String version = dagExec.getVersion();
		Object metaObj = null;
		Object iDao = null;
		List<Status> statusList = null;
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		try {
			// Get the object
			iDao = this.getClass().getMethod(GET + Helper.getDaoClass(MetaType.dagExec)).invoke(this);
			if (appUuid != null) {
				dagExec = (DagExec) (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class, String.class).invoke(iDao, appUuid, uuid, version);
			} else {
				dagExec = (DagExec) (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class).invoke(iDao, uuid, version);
			}
			if (dagExec == null) {
				return null;
			}
			metaObj = dagExecServiceImpl.getStageExec(dagExec, stageId);
			if (metaObj == null) {
				metaObj = retObj;
			}
			statusList = (List<Status>) StageExec.class.getMethod(GET+"StatusList").invoke(metaObj);
		} catch(NullPointerException  | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e){
			e.printStackTrace();
		}
		
		switch (stage) {
		case NotStarted:
			statusList = setNotStartedStatus(statusList);
			break;
		case OnHold:
			statusList = setOnHoldStatus(statusList);
			break;
		case Resume:
			statusList = setResumeStatus(statusList);
			break;
		case InProgress:
			statusList = setInProgressStatus(statusList);
			break;
		case Failed:
			statusList = setFailedStatus(statusList);
			break;
		case Completed:
			statusList = setCompletedStatus(statusList);
			break;
		case Terminating : 
			logger.info("Going to set terminating status for stage ");
			logger.info(Helper.getLatestStatus(statusList).getStage().toString());
			statusList = setTerminatingStatus(statusList);
			break;
		case Killed : 
			statusList = setKilledStatus(statusList);
			break;
		default:
			break;
		}

		//Save the status in mongo
		StageExec.class.getMethod("setStatusList", List.class).invoke(retObj, statusList);
		dagExecServiceImpl.setStageExec(dagExec, (StageExec) retObj);
		save(MetaType.dagExec.toString(), dagExec);
		return retObj;
	}


	@SuppressWarnings("unchecked")
	public Object setMetaStatusForTask (DagExec dagExec, Object retObj, Status.Stage stage, String stageId, String taskId) throws Exception {
		String uuid = dagExec.getUuid();
		String version = dagExec.getVersion();
		Object metaObj = null;
		Object iDao = null;
		List<Status> statusList = null;
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		try {
			// Get the object
			iDao = this.getClass().getMethod(GET + Helper.getDaoClass(MetaType.dagExec)).invoke(this);
			if (appUuid != null) {
				dagExec = (DagExec) (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class, String.class).invoke(iDao, appUuid, uuid, version);
			} else {
				dagExec = (DagExec) (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class).invoke(iDao, uuid, version);
			}
			if (dagExec == null) {
				return null;
			}
			metaObj = dagExecServiceImpl.getTaskExec(dagExec, stageId, taskId);
			if (metaObj == null) {
				metaObj = retObj;
			}
			statusList = (List<Status>) TaskExec.class.getMethod(GET+"StatusList").invoke(metaObj);
		} catch(NullPointerException  | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e){
			e.printStackTrace();
		}
		
		switch (stage) {
		case NotStarted:
			statusList = setNotStartedStatus(statusList);
			break;
		case OnHold:
			statusList = setOnHoldStatus(statusList);
			break;
		case Resume:
			statusList = setResumeStatus(statusList);
			break;
		case InProgress:
			statusList = setInProgressStatus(statusList);
			break;
		case Failed:
			statusList = setFailedStatus(statusList);
			break;
		case Completed:
			statusList = setCompletedStatus(statusList);
			break;
		case Terminating : 
			statusList = setTerminatingStatus(statusList);
			break;
		case Killed : 
			logger.info("Going to kill task : " + taskId + " : before setKilledStatus");
			statusList = setKilledStatus(statusList);
			break;
		default:
			break;
		}

		//Save the status in mongo
		TaskExec.class.getMethod("setStatusList", List.class).invoke(retObj, statusList);
		dagExecServiceImpl.setTaskExec(dagExec, (TaskExec) retObj);
		save(MetaType.dagExec.toString(), dagExec);
		return retObj;
	}

	
	
	@SuppressWarnings("unchecked")
	public Object setMetaStatus (Object retObj, MetaType metaType, Status.Stage stage) throws Exception {
		String uuid = (String) Helper.getDomainClass(metaType).getMethod(GET+"Uuid").invoke(retObj);
		String version = (String) Helper.getDomainClass(metaType).getMethod(GET+"Version").invoke(retObj);		
		Object metaObj = null;
		Object iDao = null;
		List<Status> statusList = null;
		String appUuid = null;
		if (!metaType.equals(MetaType.user) && !metaType.equals(MetaType.group)
			&& !metaType.equals(MetaType.role) && !metaType.equals(MetaType.privilege)
			&& !metaType.equals(MetaType.application)) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		try {
			// Get the object
			iDao = this.getClass().getMethod(GET + Helper.getDaoClass(metaType)).invoke(this);
			if (appUuid != null) {
				metaObj = (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class, String.class).invoke(iDao, appUuid, uuid, version);
			} else {
				metaObj = (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class).invoke(iDao, uuid, version);
			}
			if (metaObj == null) {
				metaObj = retObj;
			}
			statusList = (List<Status>) Helper.getDomainClass(metaType).getMethod(GET+"StatusList").invoke(metaObj);
		} catch(NullPointerException  | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e){
			e.printStackTrace();			
		}
		
		switch (stage) {
			case NotStarted:
				statusList = setNotStartedStatus(statusList);
				break;
			case OnHold:
				statusList = setOnHoldStatus(statusList);
				break;
			case Resume:
				statusList = setResumeStatus(statusList);
				break;
			case InProgress:
				statusList = setInProgressStatus(statusList);
				break;
			case Failed:
				statusList = setFailedStatus(statusList);
				break;
			case Completed:
				statusList = setCompletedStatus(statusList);
				break;
			case Terminating : 
				statusList = setTerminatingStatus(statusList);
				break;
			case Killed : 
				statusList = setKilledStatus(statusList);
				break;
			default:
				break;
		}

		//Save the status in mongo
		Helper.getDomainClass(metaType).getMethod("setStatusList", List.class).invoke(retObj, statusList);
		save(metaType.toString(), retObj);
		return retObj;
	}

	public void onHold (MetaType type, String uuid, String version) {
		Object service = null;
		try {
			service = this.getClass().getMethod(GET + Helper.getServiceClass(type)).invoke(this);
			(service).getClass().getMethod("onHold", String.class, String.class).invoke(service,uuid,version);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resume (MetaType type, String uuid, String version) {
		Object service = null;
		try {
			service = this.getClass().getMethod(GET + Helper.getServiceClass(type)).invoke(this);
			(service).getClass().getMethod("resume", String.class, String.class).invoke(service,uuid,version);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void kill (MetaType type, String uuid, String version) {
		Object service = null;
		try {
			service = this.getClass().getMethod(GET + Helper.getServiceClass(type)).invoke(this);
			(service).getClass().getMethod("kill", String.class, String.class).invoke(service,uuid,version);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setStatus(String type, String uuid, String version, String status) throws JsonProcessingException, Exception {
		if (!StringUtils.isBlank(status)) {
			if(status.toLowerCase().equalsIgnoreCase(Status.Stage.OnHold.toString().toLowerCase())){
				onHold(Helper.getMetaType(type),uuid,version);
			}
			else if(status.toLowerCase().equalsIgnoreCase(Status.Stage.Resume.toString().toLowerCase())){
				resume(Helper.getMetaType(type),uuid,version);
			}
			else if(status.toLowerCase().equalsIgnoreCase(Status.Stage.Killed.toString().toLowerCase())){
				kill(Helper.getMetaType(type),uuid,version);
			}
		}
	}
	
	/*public String invalidateSession() {
		String message = null;
		try{
			SessionCounter.invalidateSessions();
			message = "Session(s) destroyed successfully.";
		}catch (NullPointerException e) {
			e.printStackTrace();
			return "Can not destroy session(s).";
		}catch (Exception e) {
			e.printStackTrace();
			return "Can not destroy session(s).";
		}
		return message;
	}*/
	
	public boolean nonBlockingCompleteTaskThread(List<FutureTask<String>> taskList) {
		String outputThreadName = null;
		boolean isComplete = true;
		for (FutureTask<String> futureTask : taskList) {
			if (futureTask == null || !futureTask.isDone()) {
				isComplete = false;
				continue;
			}
			try {
            	outputThreadName = futureTask.get();
                logger.info("Thread " + outputThreadName + " completed ");
                taskThreadMap.remove(outputThreadName);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
		}
		return isComplete;
	}
	
	/**
	 * Collect result and clear map
	 * @param taskList
	 */
	public void completeTaskThread(List<FutureTask<TaskHolder>> taskList) {
		String outputThreadName = null;
		TaskHolder taskHolder = null;
		for (FutureTask<TaskHolder> futureTask : taskList) {
			try {
				taskHolder = futureTask.get();
				outputThreadName = taskHolder.getName();
                logger.info("Thread " + outputThreadName + " completed ");
                taskThreadMap.remove(outputThreadName);
            } catch (InterruptedException | CancellationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Status> getAllStatusForExec(MetaIdentifier ref) {
		Object iDao = null;
		Object metaObj = null;
		List<Status> statusList = null;
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (ref == null || StringUtils.isBlank(ref.getUuid()) || ref.getType() == null) {
			logger.info(" Inside getAllStatusForExec. ref is null or does not have uuid or type. Aborting ... ");
			return null;
		}
		if (!ref.getType().toString().toLowerCase().contains("exec")) {
			logger.info(" Ref is not an exec. Aborting ... ");
			return null;
		}
		String uuid = ref.getUuid();
		String version = ref.getVersion();
		try {
			iDao = this.getClass().getMethod(GET + Helper.getDaoClass(ref.getType())).invoke(this);
			if (appUuid != null) {
				metaObj = (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class, String.class).invoke(iDao, appUuid, uuid, version);
			} else {
				metaObj = (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class).invoke(iDao, uuid, version);
			}
			// Get status list from metaObj
			statusList = (List<Status>) Helper.getDomainClass(ref.getType()).getMethod(GET+"Status").invoke(metaObj);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException e) {
			logger.error("Exception in getAllStatusForExec ");
			e.printStackTrace();
		}
		return statusList;
	}
	
	/**
	 * 
	 * @param baseRuleGroupExec
	 * @param groupExecType
	 * @param ruleExecType
	 * @return Status
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Status getGroupStatus(BaseRuleGroupExec baseRuleGroupExec,MetaType groupExecType, MetaType ruleExecType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Status defaultStatus = new Status(Status.Stage.InProgress, new Date());
		List<MetaIdentifierHolder> metaIdentifierHolderList=null;
		BaseRuleExec baseRuleExec = null;
		ConcurrentHashMap<Status.Stage,Integer> stausMap=new ConcurrentHashMap<Status.Stage,Integer>();
		stausMap.put(defaultStatus.getStage(),Integer.valueOf(0));
		
			metaIdentifierHolderList=baseRuleGroupExec.getExecList();
			for(MetaIdentifierHolder baseRuleExecHolder : baseRuleGroupExec.getExecList()) {
				try {
					baseRuleExec = (BaseRuleExec) getOneByUuidAndVersion(baseRuleExecHolder.getRef().getUuid(),baseRuleExecHolder.getRef().getVersion(), ruleExecType.toString());
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				Helper.getLatestStatus(baseRuleExec.getStatusList()).getStage();
				if(stausMap.containsKey(Helper.getLatestStatus(baseRuleExec.getStatusList()).getStage())){ 
					stausMap.put(Helper.getLatestStatus(baseRuleExec.getStatusList()).getStage(),stausMap.get(Helper.getLatestStatus(baseRuleExec.getStatusList()).getStage())+1);
				}
				 else{
					 stausMap.putIfAbsent(Helper.getLatestStatus(baseRuleExec.getStatusList()).getStage(),1);
				 }
			}
			
		if(stausMap.containsKey(com.inferyx.framework.domain.Status.Stage.InProgress) && (stausMap.get(com.inferyx.framework.domain.Status.Stage.InProgress) >=1)){ 
			defaultStatus.setStage(Status.Stage.InProgress);
		}
		else if(stausMap.containsKey(com.inferyx.framework.domain.Status.Stage.Failed) && (stausMap.get(com.inferyx.framework.domain.Status.Stage.Failed) >=1) && !(stausMap.get(com.inferyx.framework.domain.Status.Stage.InProgress) >0)){
			defaultStatus.setStage(Status.Stage.Failed);
		}
		else if(stausMap.containsKey(com.inferyx.framework.domain.Status.Stage.Killed) && (stausMap.get(com.inferyx.framework.domain.Status.Stage.Killed) >=1) && !(stausMap.get(com.inferyx.framework.domain.Status.Stage.InProgress) >0) && !stausMap.containsKey(com.inferyx.framework.domain.Status.Stage.Failed)){
			defaultStatus.setStage(Status.Stage.Killed);
		}
		else if(stausMap.containsKey(com.inferyx.framework.domain.Status.Stage.Resume) && (stausMap.get(com.inferyx.framework.domain.Status.Stage.Resume) >=1) && !stausMap.containsKey(com.inferyx.framework.domain.Status.Stage.Killed) && !(stausMap.get(com.inferyx.framework.domain.Status.Stage.InProgress) >0) && !stausMap.containsKey(com.inferyx.framework.domain.Status.Stage.Failed)){
			defaultStatus.setStage(Status.Stage.InProgress);
		}
		else if(stausMap.containsKey(com.inferyx.framework.domain.Status.Stage.Completed) && (metaIdentifierHolderList.size()== stausMap.get(com.inferyx.framework.domain.Status.Stage.Completed))){ 
			defaultStatus.setStage(Status.Stage.Completed);
		}

		logger.info("mapStatus: "+stausMap.toString());
		logger.info("FinalStatus: "+defaultStatus.getStage().toString());
		return defaultStatus;
 
      }

	
	@SuppressWarnings("unchecked")
	public T getLatestByUuidWithoutAppUuid(String uuid, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		MetaType metaType = Helper.getMetaType(type);
		Object iDao = this.getClass().getMethod(GET + Helper.getDaoClass(metaType)).invoke(this);
		Object object = (T) Helper.getDomainClass(metaType).cast(iDao.getClass().getMethod("findLatestByUuid",String.class,Sort.class ).invoke(iDao, uuid,new Sort(Sort.Direction.DESC, "version")));	
		return (T) resolveName(object, Helper.getMetaType(type));
		//return (T) object;
	}
	
	@SuppressWarnings("unchecked")
	public T getOneByUuidAndVersionWithoutAppUuid(String uuid, String version, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		MetaType metaType = Helper.getMetaType(type);
		T object = null;
		Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);
		if(StringUtils.isBlank(version))
			object = (T) iDao.getClass().getMethod("findLatestByUuid", String.class,Sort.class).invoke(iDao, uuid,new Sort(Sort.Direction.DESC, "version"));	
		else
			object = (T) iDao.getClass().getMethod("findOneByUuidAndVersion", String.class,String.class).invoke(iDao, uuid,version);
		return (T) resolveName(object, Helper.getMetaType(type));
	}	
	
	public BaseEntity published(String id, String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException, ParseException, JSONException {
		String appUuid = null;
		if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
			&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
			&& !type.equalsIgnoreCase(MetaType.application.toString())) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		MetaType metaType = Helper.getMetaType(type);
		Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);//finds respective Dao type
		Object obj = null;
		if (appUuid != null)
			obj = Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findOneById", String.class, String.class).invoke(iDao, appUuid, id));
		else
			obj = Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findOneById", String.class).invoke(iDao, id));
		Helper.getDomainClass(metaType).getMethod("setPublished", String.class).invoke(obj, "Y");
		return (BaseEntity) resolveName(save(type, obj), Helper.getMetaType(type));
	}
	public BaseEntity unPublished(String id, String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException, ParseException, JSONException {
		String appUuid = null;
		if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
			&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
			&& !type.equalsIgnoreCase(MetaType.application.toString())) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		MetaType metaType = Helper.getMetaType(type);
		Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);//finds respective Dao type
		Object obj = null;
		if (appUuid != null)
			obj = Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findOneById", String.class, String.class).invoke(iDao, appUuid, id));
		else
			obj = Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findOneById", String.class).invoke(iDao, id));
		MetaIdentifierHolder createdBy = (MetaIdentifierHolder) obj.getClass().getMethod("getCreatedBy").invoke(obj);
		if(isCurrentUser(createdBy))
			Helper.getDomainClass(metaType).getMethod("setPublished", String.class).invoke(obj, "N");
		else
			return null;
		return (BaseEntity) resolveName(save(type, obj), Helper.getMetaType(type));
	}	
	public boolean isCurrentUser(MetaIdentifierHolder createdBy) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		User currentUser = metadataServiceImpl.getCurrentUser();
		if(currentUser != null && currentUser.getUuid().equalsIgnoreCase(createdBy.getRef().getUuid())) {
			return true;
		}else
			return false;
	}

	public Datasource getDatasourceByApp() throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datasource datasource = null;
		MetaIdentifierHolder holder = securityServiceImpl.getAppInfo();
		Application application = (Application) getOneByUuidAndVersionWithoutAppUuid(holder.getRef().getUuid(), holder.getRef().getVersion(), MetaType.application.toString());
		holder = application.getDataSource();
		datasource = (Datasource) getOneByUuidAndVersionWithoutAppUuid(holder.getRef().getUuid(), holder.getRef().getVersion(), MetaType.datasource.toString());
		return datasource;
	}
	
	public Application getApp() throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		MetaIdentifierHolder holder = securityServiceImpl.getAppInfo();
		Application application = (Application) getOneByUuidAndVersionWithoutAppUuid(holder.getRef().getUuid(), holder.getRef().getVersion(), "application");
		return application;
	}
	
	public String getSessionParametresPropertyValue(String property, String defaultValue) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datasource datasource_2 = getDatasourceByApp();
		String sessionParameters = datasource_2.getSessionParameters();
		String partitionPropVal = defaultValue;
		if(sessionParameters != null && !StringUtils.isBlank(sessionParameters)) {
			String[] splits = sessionParameters.split(",");
			for(String split : splits) {
				if(split.contains(property)) {
					partitionPropVal = split.substring(split.indexOf("=")+1);
					logger.info("partitionPropVal: "+partitionPropVal);
				}							
			}
		}
		return partitionPropVal;
	}
	public Object getLatest(String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonProcessingException {
		String appUuid = null;
		if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
			&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
			&& !type.equalsIgnoreCase(MetaType.application.toString())) {
			appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
		}
		MetaType metaType = Helper.getMetaType(type);
		Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(Helper.getMetaType(metaType.toString().toLowerCase()))).invoke(this);//finds respective Dao type
		Object obj = null;
		if (appUuid != null)
			obj = Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findLatest", String.class, Sort.class).invoke(iDao, appUuid, new Sort(Sort.Direction.DESC, "version")));
		else
			obj = Helper.getDomainClass(metaType).cast((iDao).getClass().getMethod("findLatest", Sort.class).invoke(iDao, new Sort(Sort.Direction.DESC, "version")));
		return  resolveName(obj, metaType);
	}

	public String upload(MultipartFile file, String extension, String fileType, String fileName, String metaType) throws FileNotFoundException, IOException, JSONException, ParseException {
		String uploadFileName = file.getOriginalFilename();
		FileType type = Helper.getFileType(fileType);
		String fileLocation = null;
		String directoryLocation = Helper.getFileDirectoryByFileType(type);
		String metaUuid = null;
		String metaVersion = null;
		if(fileName == null) {
			fileName = Helper.getFileCustomNameByFileType(type, extension);
			String splits[] = fileName.split("_");
			metaUuid = splits[0];
			metaVersion = splits[1].substring(0, splits[1].lastIndexOf("."));
		} 
		
		fileLocation = directoryLocation+"/" + fileName;
		
		File scriptFile = new File(fileLocation);
		file.transferTo(scriptFile);
		if(metaType==null)
		{
			metaType="model";
		}
		UploadExec uploadExec=new UploadExec();
		uploadExec.setFileName(uploadFileName);
		uploadExec.setBaseEntity();
		uploadExec.setLocation(fileLocation);
		uploadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(Helper.getMetaType(metaType), metaUuid, metaVersion)));
		save(MetaType.uploadExec.toString(), uploadExec);
		return fileName;
	}
	
	public Object getDomainFromDomainExec(String execType, String execUuid, String execVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Object domainObject = null;
		Object domainExecObj = getOneByUuidAndVersion(execUuid, execVersion, execType);
		MetaIdentifierHolder dependsOn = (MetaIdentifierHolder) domainExecObj.getClass().getMethod("getDependsOn").invoke(domainExecObj);
		domainObject = getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
		return domainObject;
	}

	 public HttpServletResponse download(String fileType, String fileName, HttpServletResponse response) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		try {
			FileType type = Helper.getFileType(fileType);			
        	
            String directoryLocation = Helper.getFileDirectoryByFileType(type);
            String filePath = directoryLocation+"/" + fileName;
            File file = new File(filePath);
            if (file.exists()) {
            	logger.info("File found.");
                String mimeType = null;//context.getMimeType(file.getPath());
 
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
 
                response.setContentType(mimeType);
                response.setContentLength((int) file.length());
                response.setContentType("application/xml charset=utf-16");
				response.setHeader("Content-disposition", "attachment");
				response.setHeader("filename",fileName);
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
            	logger.info("Requested " + fileName + " file not found!!");
            	response.setStatus(300);
            	throw new FileNotFoundException("Requested " + fileName + " file not found!!");
            }
        } catch (IOException e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "Requested " + fileName + " file not found!!");
			throw new IOException((message != null) ? message : "Requested " + fileName + " file not found!!");
        }
	return response;
	}
	 

		public HttpServletResponse download(String uuid, String version, String format, int offset,
				int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId,
				RunMode runMode, List<Map<String, Object>> results,MetaType metaType, MetaIdentifierHolder dependsOn) throws Exception {
			
			String downloadPath = Helper.getPropertyValue("framework.file.download.path");
	       DownloadExec downloadExec=new DownloadExec();
	       
	       downloadExec.setBaseEntity();
	       downloadExec.setLocation(downloadPath + "/" + downloadExec.getUuid() + "_" + downloadExec.getVersion() + ".xls");
	       downloadExec.setDependsOn(dependsOn);
			try {
				FileOutputStream fileOut = null;
				HSSFWorkbook workbook = WorkbookUtil.getWorkbook(results);
				downloadPath = Helper.getPropertyValue("framework.file.download.path");
				//response.addHeader("Content-Disposition", "attachment; filename=" + uuid + ".xls");
				response.setContentType("application/xml charset=utf-16");
				response.setHeader("Content-disposition", "attachment");
				response.setHeader("filename", "" + uuid+"_"+version + ".xls");
				ServletOutputStream os = response.getOutputStream();
				workbook.write(os);

				fileOut = new FileOutputStream(downloadPath + "/" + downloadExec.getUuid() + "_" + downloadExec.getVersion() + ".xls");
				workbook.write(fileOut);
				os.write(workbook.getBytes());
				save(metaType.toString(), downloadExec);
				
				fileOut.close();

			} catch (IOException e1) {
				e1.printStackTrace();
				logger.info("exception caught while download file");
				response.setStatus(300);
	        	throw new FileNotFoundException();
			}
			return response;			
		}
		
		
		/*public String upload(MultipartFile file, String extension, String fileType, String fileName,String uuid,String version,MetaType metaType) throws FileNotFoundException, IOException {
			String uploadFileName = file.getOriginalFilename();
			FileType type = Helper.getFileType(fileType);
			String fileLocation = null;
			String directoryLocation = Helper.getFileDirectoryByFileType(type);
			if(fileName == null) {
				fileName = Helper.getFileCustomNameByFileType(type, extension);
			} 
			 
			fileLocation = directoryLocation+"/" + fileName;
			UploadExec uploadExec=new UploadExec();
			uploadExec.setFileName(uploadFileName);
			uploadExec.setBaseEntity();
			uploadExec.setLocation(fileLocation+"/"+uploadExec.getUuid()+"_"+uploadExec.getVersion()+"");
			uploadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(metaType,uuid,version)));
			File scriptFile = new File(fileLocation);
			file.transferTo(scriptFile);
			return fileName;
		}*/
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public List<T> findAllLatestWithoutAppUuid(MetaType type) {
			List objectList = new ArrayList();
			List<T> finalObjectList = new ArrayList<>();
			java.util.HashMap<String, BaseEntity> objectMap = new java.util.HashMap<>(); 
			BaseEntity baseEntity = null;
			try {
				Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(type)).invoke(this);
				objectList = (List)(iDao).getClass().getMethod("findAll").invoke(iDao);
				
				for (int i = 0; i < objectList.size(); i++) {
					baseEntity = BaseEntity.class.cast(objectList.get(i));
					if (objectMap.containsKey(baseEntity.getUuid())) {
						if (Long.parseLong(baseEntity.getVersion()) > Long.parseLong(objectMap.get(baseEntity.getUuid()).getVersion())) {
							objectMap.put(baseEntity.getUuid(), baseEntity);
						}
					} else {
						objectMap.put(baseEntity.getUuid(), baseEntity);
					}
				}
				for (String uuid : objectMap.keySet()) {
					finalObjectList.add((T) Helper.getDomainClass(type).cast(objectMap.get(uuid)));
				}
				return finalObjectList;
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return finalObjectList;
		}
		
		@SuppressWarnings("unchecked")
		public List<T> getAllLatestCompleteObjects(String type, String active)  throws JsonProcessingException, ParseException {
			MetaType metaType = Helper.getMetaType(type);
			List<T> objectList = new ArrayList<>();
			try {			
				Aggregation aggr = null;
				if (active == "Y")
					 aggr = newAggregation(match(Criteria.where("active").is("Y")),
						match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
				else
					 aggr = newAggregation(
						match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
					

				String appUuid = null;
				if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
					&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
					&& !type.equalsIgnoreCase(MetaType.application.toString())) {
					appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
				}
				@SuppressWarnings("rawtypes")
				AggregationResults results = mongoTemplate.aggregate(aggr, type.toString().toLowerCase(), Helper.getDomainClass(metaType));
				List<T> metaList = results.getMappedResults();
				Object iDao = this.getClass().getMethod(GET+Helper.getDaoClass(metaType)).invoke(this);
					
				for (int i = 0; i < metaList.size(); i++) {
					Object object = null;
					if (appUuid != null) {
						object = (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class, String.class).invoke(iDao, appUuid, ((BaseEntity)metaList.get(i)).getId(), ((BaseEntity)metaList.get(i)).getVersion());
					} else {
						object = (iDao).getClass().getMethod("findOneByUuidAndVersion", String.class, String.class).invoke(iDao, ((BaseEntity)metaList.get(i)).getId(), ((BaseEntity)metaList.get(i)).getVersion());
					}
					if (object != null) {
						objectList.add((T) Helper.getDomainClass(metaType).cast(object));
					}
				}
				return objectList;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException | NullPointerException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public String findAppId(String type)
		{
			String appUuid=null;
			if (!type.equalsIgnoreCase(MetaType.user.toString()) && !type.equalsIgnoreCase(MetaType.group.toString())
					&& !type.equalsIgnoreCase(MetaType.role.toString()) && !type.equalsIgnoreCase(MetaType.privilege.toString())
					&& !type.equalsIgnoreCase(MetaType.application.toString()) && !type.equalsIgnoreCase(MetaType.meta.toString())) {
					appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid() : null;							
				}
			return appUuid;
			
		}

		public Map<String, List<Object>> getAllByMetaList(String[] type) {
			//List<Object> metaList = new ArrayList<>();
			Map<String, List<Object>> metaList = new HashMap<>();
			for(String meta : type) {
				@SuppressWarnings("unchecked")
				List<Object> metaObjectList = (List<Object>) findAll(Helper.getMetaType(meta));
				metaList.put(meta, metaObjectList);
			}
			return metaList;
		}
		
		public HttpServletResponse sendResponse(String code, String status, String msg) throws JSONException, ParseException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if(requestAttributes != null) {
				HttpServletResponse response = requestAttributes.getResponse();
				if(response != null) {
						Message message = new Message(code, status, msg);
						Message savedMessage = messageServiceImpl.save(message);
						
						ObjectMapper mapper = new ObjectMapper();
						String messageJson = mapper.writeValueAsString(savedMessage);
						response.setContentType("application/json");
						response.setStatus(Integer.parseInt(code));
						response.getOutputStream().write(messageJson.getBytes());
						response.getOutputStream().close();
						return response;
				}else
					logger.info("HttpServletResponse response is \""+null+"\"");
			}else
				logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");
			return null;
		}

		/**
		 * 
		 * @param operator
		 * @return
		 */
		public ExecParams getExecParams (TaskOperator operator) {
			if (operator == null 
					|| operator.getOperatorParams() == null 
					|| !operator.getOperatorParams().containsKey(ConstantsUtil.EXEC_PARAMS)
					|| operator.getOperatorParams().get(ConstantsUtil.EXEC_PARAMS) == null) {
				return null;
			}
			logger.info("ExecParams : " + operator.getOperatorParams().get(ConstantsUtil.EXEC_PARAMS));
			ObjectMapper mapper = new ObjectMapper();
			ExecParams execParams = mapper.convertValue(operator.getOperatorParams().get(ConstantsUtil.EXEC_PARAMS), ExecParams.class);
//			return (ExecParams) operator.getOperatorParams().get(ConstantsUtil.EXEC_PARAMS);
			return execParams;
		}
		
		private Object resolveFeatureAttrMap(List<FeatureAttrMap> featureAttrMapList, Object object) throws JsonProcessingException {
			try {
				for(FeatureAttrMap featureAttrMap : featureAttrMapList) {
					FeatureRefHolder featureHolder = featureAttrMap.getFeature(); 
					AttributeRefHolder attributeHolder = featureAttrMap.getAttribute();
					
					MetaIdentifier featureIdentifier = featureHolder.getRef();
					MetaIdentifier attributeIdentifier = attributeHolder.getRef();
					Model model = (Model) getOneByUuidAndVersion(featureIdentifier.getUuid(), featureIdentifier.getVersion(), featureIdentifier.getType().toString());
					Object source = getOneByUuidAndVersion(attributeIdentifier.getUuid(), attributeIdentifier.getVersion(), attributeIdentifier.getType().toString());
					
					for(Feature feature : model.getFeatures()) {
						if(featureAttrMap.getFeature().getFeatureId().equalsIgnoreCase(feature.getFeatureId())) {
							featureHolder.setFeatureName(feature.getName());
							featureAttrMap.setFeature(featureHolder);
						}
					}
					if(source instanceof Datapod)
						for(Attribute attribute : ((Datapod)source).getAttributes()) {
							if(featureAttrMap.getAttribute().getAttrId().equalsIgnoreCase(attribute.getAttributeId()+"")) {
								attributeHolder.setAttrName(attribute.getName());
								featureAttrMap.setAttribute(attributeHolder);
							}
						}
					else if(source instanceof DataSet)
						for(AttributeSource attributeSource : ((DataSet)source).getAttributeInfo()) {
							if(featureAttrMap.getAttribute().getAttrId().equalsIgnoreCase(attributeSource.getAttrSourceId())) {
								attributeHolder.setAttrName(attributeSource.getAttrSourceName());
								featureAttrMap.setAttribute(attributeHolder);
							}
						}
					else if(source instanceof Rule)
						for(AttributeSource attributeSource : ((Rule)source).getAttributeInfo()) {
							if(featureAttrMap.getAttribute().getAttrId().equalsIgnoreCase(attributeSource.getAttrSourceId())) {
								attributeHolder.setAttrName(attributeSource.getAttrSourceName());
								featureAttrMap.setAttribute(attributeHolder);
							}
						}
				}
				Train train = (Train) object;
				train.setFeatureAttrMap(featureAttrMapList);
				object = train;
				return object;				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return object;
		}
		

		
		public String resolveLabel(AttributeRefHolder labelInfo) throws JsonProcessingException {
			String attributeName = null;
			Object source = getOneByUuidAndVersion(labelInfo.getRef().getUuid(), labelInfo.getRef().getVersion(), labelInfo.getRef().getType().toString());
			if(source instanceof Datapod) {
				Datapod datapod = (Datapod) source;
				attributeName = datapod.getAttributeName(Integer.parseInt(labelInfo.getAttrId()));
			} else if(source instanceof DataSet) {
				DataSet dataset = (DataSet) source;
				attributeName = dataset.getAttributeName(Integer.parseInt(labelInfo.getAttrId()));
			} else if(source instanceof Rule) {
				Rule rule = (Rule) source;
				attributeName = rule.getAttributeName(Integer.parseInt(labelInfo.getAttrId()));
			}		
			return attributeName;
		}
		@SuppressWarnings("unchecked")
		public List<BaseEntity> getResolveNameByUuidandType(String uuid,String type)  {
			Query query = new Query();
			query.fields().include("uuid");
			query.fields().include("name");
			query.fields().include("version");
			query.fields().include("type");
			
			query.addCriteria(Criteria.where("uuid").is(uuid));
			List<BaseEntity> obj=new ArrayList<>();
			obj = (List<BaseEntity>) mongoTemplate.find(query, Helper.getDomainClass(Helper.getMetaType(type)));
			//String name=obj.getName();
			
			return obj;
			
		}
		
	public boolean uploadCommentFile(List<MultipartFile> multiPartFile, String filename, String fileType,String uuid)
			throws FileNotFoundException, IOException, JSONException, ParseException {

		String directoryPath = Helper.getPropertyValue("framework.file.comment.upload.path");
		if (null != multiPartFile && multiPartFile.size() > 0) {
			for (MultipartFile multipartFile : multiPartFile) {
				
				UploadExec uploadExec = new UploadExec();
				
				uploadExec.setBaseEntity();
				
				String fileName = multipartFile.getOriginalFilename();
				String fileExtention = fileName.substring(fileName.lastIndexOf("."));
				String filename1 = fileName.substring(0, fileName.lastIndexOf("."));
				String location = directoryPath + "/" + uploadExec.getUuid() + fileExtention;
				File dest = new File(location);
				multipartFile.transferTo(dest);
				String contenetType = multipartFile.getContentType();
				
				uploadExec.setName(filename1);
				uploadExec.setLocation(location);
				uploadExec.setFileName(fileName);
				MetaIdentifierHolder metaIdentifierHolder = new MetaIdentifierHolder();
				MetaIdentifier identifier = new MetaIdentifier();
				identifier.setUuid(uuid);
				identifier.setName(filename1);
				identifier.setType(MetaType.comment);
				metaIdentifierHolder.setRef(identifier);
				uploadExec.setDependsOn(metaIdentifierHolder);
				save(MetaType.uploadExec.toString(), uploadExec);
			}
		}
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	public HttpServletResponse download(String fileType, String fileName, HttpServletResponse response,String uuid) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		try {
			List<UploadExec> uploadExec = new ArrayList<UploadExec>();
			Query query = new Query();
			query.fields().include("uuid");
			query.fields().include("name");
			query.fields().include("location");
			query.fields().include("fileName");
			query.addCriteria(Criteria.where("uuid").is(uuid));
	
			uploadExec = (List<UploadExec>) mongoTemplate.find(query, Helper.getDomainClass(MetaType.uploadExec));
			
		//fileName=uploadExec.get(0).getFileName();
            String filePath = uploadExec.get(0).getLocation();
            String FileName =uploadExec.get(0).getFileName();
			String fileExtention = FileName.substring(FileName.lastIndexOf("."));
			//String filename1 = FileName.substring(0, fileName.lastIndexOf("."));
            File file = new File(filePath);
            
            if (file.exists()) {
            	logger.info("File found.");
                 String mimeType = null;//context.getMimeType(file.getPath());
                 mimeType= new MimetypesFileTypeMap().getContentType(file);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
 
                response.setContentType(mimeType);
                response.setContentLength((int) file.length());
             //   response.setContentType("application/xml charset=utf-16");
				response.setHeader("Content-disposition", "attachment");
				response.setHeader("filename",fileName+fileExtention);
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
            	logger.info("Requested " + fileName + " file not found!!");
            	response.setStatus(300);
            	throw new FileNotFoundException("Requested " + fileName + " file not found!!");
            }
        } catch (IOException e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "Requested " + fileName + " file not found!!");
			throw new IOException((message != null) ? message : "Requested " + fileName + " file not found!!");
        }
	return response;
	}


	/**
	 * 
	 * @param metaType
	 * @param ref
	 * @return
	 */
	public T createExec(MetaType metaType, MetaIdentifier ref) {
		logger.info("Metatype string : " + metaType.toString());
		BaseExec baseExec = helper.createExec(metaType);
		logger.info(baseExec);
		T object = (T) Helper.getDomainClass(metaType).cast(baseExec);
		baseExec.setDependsOn(new MetaIdentifierHolder(ref));
		baseExec.setBaseEntity();
		baseExec.setName(ref.getName());
		return object;
	}
	
	/**
	 * 
	 * @param metaType
	 * @param ref
	 * @param taskExec
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public T createAndSetOperator(MetaType metaType, MetaIdentifier ref, TaskExec taskExec) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		T execObject = (T)createExec(metaType, ref);
		MetaIdentifier metaExecIdentifier = new MetaIdentifier(metaType, String.class.cast(execObject.getClass().getMethod("getUuid", null).invoke(execObject, null)),
				String.class.cast(execObject.getClass().getMethod("getVersion", null).invoke(execObject, null)));
		taskExec.getOperators().get(0).getOperatorInfo().setRef(metaExecIdentifier);
		return execObject;
	}

	public List<MetaIdentifierHolder> uploadGenric(List<MultipartFile> multiPartFile, String extension, String fileType,
			String type, String uuid,String version, String action)
			throws FileNotFoundException, IOException, JSONException, ParseException {

		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		if (null != multiPartFile && multiPartFile.size() > 0) {
			for (MultipartFile multipartFile : multiPartFile) {

				FileType type1 = Helper.getFileType(fileType);

				String directoryPath = Helper.getFileDirectoryByFileType(fileType, type);
				UploadExec uploadExec = new UploadExec();
				uploadExec.setBaseEntity();
				String originalFileName = multipartFile.getOriginalFilename();
				String fileExtention = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
				String filename1 = originalFileName.substring(0, originalFileName.lastIndexOf("."));

				String fileName_Uuid = Helper.getFileCustomNameByFileType(type1, fileExtention, type);
				String splits[] = fileName_Uuid.split("_");
				String metaUuid = splits[0];
				String metaVersion = splits[1].substring(0, splits[1].lastIndexOf("."));
				String location;
				if (fileType != null && fileType.equalsIgnoreCase(FileType.ZIP.toString())
						&& type.equalsIgnoreCase(MetaType.Import.toString())) {
					ObjectMapper mapper = new ObjectMapper();/*
																 * uploadExec.setDependsOn(new MetaIdentifierHolder(new
																 * MetaIdentifier(
																 * Helper.getMetaType(MetaType.Import.toString()),
																 * metaUuid, metaVersion, originalFileName)));
																 */
					mapper.writeValueAsString(importServiceImpl.uploadFile(multipartFile, originalFileName));
					MetaIdentifierHolder metaIdentifierHolder2 = new MetaIdentifierHolder();
					metaIdentifierHolder2.setRef(new MetaIdentifier(Helper.getMetaType(MetaType.Import.toString()),
							metaUuid, metaVersion, originalFileName));
					metaIdentifierHolderList.add(metaIdentifierHolder2);
					continue;
				}
				// if req comming form admin then file name should be original
				if (fileType != null && fileType.equalsIgnoreCase("csv") && uuid == null) {
					location = directoryPath + "/" + originalFileName;
				} else {
					location = directoryPath + "/" + fileName_Uuid;
				}
				File dest = new File(location);
				multipartFile.transferTo(dest);

				uploadExec.setName(filename1);
				uploadExec.setLocation(location);
				uploadExec.setFileName(originalFileName);
				if (fileType != null && fileType.equalsIgnoreCase(FileType.ZIP.toString())
						&& type.equalsIgnoreCase(MetaType.Import.toString())) {
					ObjectMapper mapper = new ObjectMapper();/*
																 * uploadExec.setDependsOn(new MetaIdentifierHolder(new
																 * MetaIdentifier(
																 * Helper.getMetaType(MetaType.Import.toString()),
																 * metaUuid, metaVersion, originalFileName)));
																 * mapper.writeValueAsString(importServiceImpl.
																 * uploadFile(multipartFile, originalFileName));
																 */
					MetaIdentifierHolder metaIdentifierHolder2 = new MetaIdentifierHolder();
					metaIdentifierHolder2.setRef(new MetaIdentifier(Helper.getMetaType(MetaType.uploadExec.toString()),
							uploadExec.getUuid(), uploadExec.getVersion(), filename1));
					metaIdentifierHolderList.add(metaIdentifierHolder2);
				}
				if (type != null && type.equalsIgnoreCase("comment")) {
					uploadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(
							Helper.getMetaType(MetaType.comment.toString()), uuid, version, null)));
					save(MetaType.uploadExec.toString(), uploadExec);
					MetaIdentifierHolder metaIdentifierHolder2 = new MetaIdentifierHolder();
					metaIdentifierHolder2.setRef(new MetaIdentifier(Helper.getMetaType(MetaType.uploadExec.toString()),
							uploadExec.getUuid(), uploadExec.getVersion(), filename1));
					metaIdentifierHolderList.add(metaIdentifierHolder2);
				} else if (fileType != null && fileType.equalsIgnoreCase("script")) {
					uploadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(
							Helper.getMetaType(MetaType.model.toString()), uuid, version, null)));
					save(MetaType.uploadExec.toString(), uploadExec);
					MetaIdentifierHolder metaIdentifierHolder2 = new MetaIdentifierHolder();
/*					metaIdentifierHolder2.setRef(new MetaIdentifier(Helper.getMetaType(MetaType.model.toString()),
							metaUuid, metaVersion, filename1));*/
					metaIdentifierHolder2.setRef(new MetaIdentifier(Helper.getMetaType(MetaType.uploadExec.toString()),
							uploadExec.getUuid(), uploadExec.getVersion(), filename1));
					metaIdentifierHolderList.add(metaIdentifierHolder2);
				} else {
					save(MetaType.uploadExec.toString(), uploadExec);
					MetaIdentifierHolder metaIdentifierHolder2 = new MetaIdentifierHolder();
					metaIdentifierHolder2.setRef(new MetaIdentifier(Helper.getMetaType(MetaType.uploadExec.toString()),
							uploadExec.getUuid(), uploadExec.getVersion(), filename1));
					metaIdentifierHolderList.add(metaIdentifierHolder2);
				}

			}

		}
		return (List<MetaIdentifierHolder>) metaIdentifierHolderList;

	}
	
	
	@SuppressWarnings("unchecked")
	public HttpServletResponse genricDownload(String fileType, String fileName, HttpServletResponse response,String uuid) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		try {
			List<UploadExec> uploadExec = new ArrayList<UploadExec>();
			Query query = new Query();
			query.fields().include("uuid");
			query.fields().include("name");
			query.fields().include("location");
			query.fields().include("fileName");
			query.addCriteria(Criteria.where("uuid").is(uuid));
	
			uploadExec = (List<UploadExec>) mongoTemplate.find(query, Helper.getDomainClass(MetaType.uploadExec));
			
		//fileName=uploadExec.get(0).getFileName();
            String filePath = uploadExec.get(0).getLocation();
            String FileName =uploadExec.get(0).getFileName();
			String fileExtention = FileName.substring(FileName.lastIndexOf("."));
			//String filename1 = FileName.substring(0, fileName.lastIndexOf("."));
            File file = new File(filePath);
            
            if (file.exists()) {
            	logger.info("File found.");
                 String mimeType = null;//context.getMimeType(file.getPath());
                 mimeType= new MimetypesFileTypeMap().getContentType(file);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
 
                response.setContentType(mimeType);
                response.setContentLength((int) file.length());
             //   response.setContentType("application/xml charset=utf-16");
				response.setHeader("Content-disposition", "attachment");
				response.setHeader("filename",fileName);
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
            	logger.info("Requested " + fileName + " file not found!!");
            	response.setStatus(300);
            	throw new FileNotFoundException("Requested " + fileName + " file not found!!");
            }
        } catch (IOException e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "Requested " + fileName + " file not found!!");
			throw new IOException((message != null) ? message : "Requested " + fileName + " file not found!!");
        }
	return response;
	}
}
