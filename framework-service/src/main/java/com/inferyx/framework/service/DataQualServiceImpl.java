package com.inferyx.framework.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.DQInfo;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.WorkbookUtil;
import com.inferyx.framework.dao.IDataQualDao;
import com.inferyx.framework.dao.IDataQualExecDao;
import com.inferyx.framework.dao.IFilterDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.operator.DQOperator;
import com.inferyx.framework.register.GraphRegister;
import com.inferyx.framework.view.metadata.DQView;

@Service
public class DataQualServiceImpl  extends RuleTemplate{

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IDataQualDao iDataQualDao;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	MetadataUtil daoRegister;
	@Autowired
	DQOperator dqOperator;
	@Autowired
	protected DataSourceFactory datasourceFactory;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private DataQualExecServiceImpl dataQualExecServiceImpl;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	DQInfo dqInfo;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	@Autowired 
	RegisterService registerService;
	@Autowired
	IDataQualExecDao iDataQualExecDao;
	@Autowired
	IFilterDao ifilterDao;	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ConnectionFactory connFactory;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	@Autowired
	Engine engine;
	
	Map<String, String> requestMap = new HashMap<String, String>();

	static final Logger logger = Logger.getLogger(DataQualServiceImpl.class);
	
	/*public DataQual findLatest() {
		return resolveName(iDataQualDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}
	
	public DataQual findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDataQualDao.findOneById(appUuid, id);
		}
		return iDataQualDao.findOne(id);
	}

	public DataQual save(DataQual dataQual) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		dataQual.setAppInfo(metaIdentifierHolderList);
		dataQual.setBaseEntity();
		*//**** Insert Target Datapod - START ****//*
		MetaIdentifier targetDPMeta = new MetaIdentifier(MetaType.datapod, dqInfo.getDqTargetUUID(), null);
		dataQual.getTarget().setRef(targetDPMeta);
		*//**** Insert Target Datapod - END ****//*
		DataQual dq=iDataQualDao.save(dataQual);
		registerGraph.updateGraph((Object) dq, MetaType.dq);
		return dq;
	}*/
		

	public DataQual save(DQView dqView) throws Exception {
		DataQual dq = new DataQual();		
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		if(dqView.getUuid() != null)
		{
			dq.setUuid(dqView.getUuid());
		}
		dq.setAppInfo(metaIdentifierHolderList);
		dq.setName(dqView.getName());
		dq.setPublished(dqView.getPublished());
		dq.setDesc(dqView.getDesc());
		dq.setTags(dqView.getTags());
		dq.setAttribute(dqView.getAttribute());
		dq.setCustomFormatCheck(dqView.getCustomFormatCheck());
		dq.setDataTypeCheck(dqView.getDataTypeCheck());
		dq.setCustomFormatCheck(dqView.getCustomFormatCheck());
		dq.setDateFormatCheck(dqView.getDateFormatCheck());
		dq.setDependsOn(dqView.getDependsOn());
		dq.setLengthCheck(dqView.getLengthCheck());
		dq.setDuplicateKeyCheck(dqView.getDuplicateKeyCheck());
		dq.setNullCheck(dqView.getNullCheck());
		dq.setRangeCheck(dqView.getRangeCheck());
		dq.setRefIntegrityCheck(dqView.getRefIntegrityCheck());
		//dq.setStdDevCheck(dqView.getStdDevCheck());
		dq.setValueCheck(dqView.getValueCheck());
		dq.setTarget(dqView.getTarget());
		Filter filter = null;
		List<AttributeRefHolder> filterList = new ArrayList<AttributeRefHolder>();
		AttributeRefHolder filterMeta = new AttributeRefHolder();
		if(dqView.getFilter() != null)
		{
		filter = dqView.getFilter();
		filter.setName(dqView.getName());
		filter.setDesc(dqView.getDesc());
		filter.setTags(dqView.getTags());
		MetaIdentifierHolder filterInfo = new MetaIdentifierHolder();
		filterInfo.setRef(dqView.getDependsOn().getRef());
		filter.setDependsOn(filterInfo);		
		}
		if (dqView.getFilterChg().equalsIgnoreCase("y")) {
			//filterServiceImpl.save(filter);
			commonServiceImpl.save(MetaType.filter.toString(), filter);
		}		
		if(filter != null)
		{
		MetaIdentifier filterInfo = new MetaIdentifier(MetaType.filter, filter.getUuid(), null);
		filterMeta.setRef(filterInfo);
		filterList.add(filterMeta);
		dq.setFilterInfo(filterList);
		}
		dq.setBaseEntity();
		dq.setPublished(dqView.getPublished());
		DataQual dataqual=iDataQualDao.save(dq);
		registerGraph.updateGraph((Object) dataqual, MetaType.dq);
		return dataqual;
	}

	public List<DataQual> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDataQualDao.findAll();
		}
		return iDataQualDao.findAll(appUuid);
	}

	/*public DataQual update(DataQual dataQual) throws IOException {
		dataQual.exportBaseProperty();
		DataQual dataqual=iDataQualDao.save(dataQual);
		registerService.createGraph();
		return dataqual;
	}*/

	public boolean isExists(String id) {
		return iDataQualDao.exists(id);
	}

	public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		DataQual dataQual = iDataQualDao.findOneById(appUuid, Id);
		dataQual.setActive("N");
		iDataQualDao.save(dataQual);
	}

	public List<DataQual> findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDataQualDao.findAllByUuid(appUuid, uuid);

	}

	public DataQual findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDataQualDao.findOneByUuidAndVersion(uuid, version);
		} else
			return iDataQualDao.findOneByUuidAndVersion(appUuid, uuid, version);
	}

	public DataQual findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDataQualDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iDataQualDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}

	public List<DataQual> findAllLatest() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Aggregation dqAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<DataQual> dqResults = mongoTemplate.aggregate(dqAggr, "dq", DataQual.class);
		List<DataQual> dataQualList = dqResults.getMappedResults();

		List<DataQual> result = new ArrayList<DataQual>();
		for (DataQual d : dataQualList) {
			DataQual dqLatest;
			if (appUuid != null) {
				dqLatest = iDataQualDao.findOneByUuidAndVersion(appUuid, d.getId(), d.getVersion());
			} else {
				dqLatest = iDataQualDao.findOneByUuidAndVersion(d.getId(), d.getVersion());
			}
			if(dqLatest != null)
			{
			result.add(dqLatest);
			}
		}
		return result;
	}

	public List<DataQual> findAllLatestActive() {
		Aggregation dqAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<DataQual> dqResults = mongoTemplate.aggregate(dqAggr, "dq", DataQual.class);
		List<DataQual> dqList = dqResults.getMappedResults();

		List<DataQual> result = new ArrayList<DataQual>();
		for (DataQual r : dqList) {
			DataQual dqLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				dqLatest = iDataQualDao.findOneByUuidAndVersion(appUuid, r.getId(), r.getVersion());
			} else {
				dqLatest = iDataQualDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if(dqLatest != null)
			{
			result.add(dqLatest);
			}
		}
		return result;
	}

	/*public List<DataQual> resolveName(List<DataQual> dataQual) {
		List<DataQual> dataQualList = new ArrayList<>();
		for (DataQual dq : dataQual) {
			String createdByRefUuid = dq.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			dq.getCreatedBy().getRef().setName(user.getName());
			dataQualList.add(dq);
		}
		return dataQualList;
	}

	public DataQual resolveName(DataQual dataQual) {
		String createdByRefUuid = dataQual.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		dataQual.getCreatedBy().getRef().setName(user.getName());
		String datapodUuid = dataQual.getAttribute().getRef().getUuid();
		Datapod datapod = datapodServiceImpl.findLatestByUuid(datapodUuid);
		if (datapod != null) {
			dataQual.getAttribute().getRef().setName(datapod.getName());
			int attrId = Integer.parseInt(dataQual.getAttribute().getAttrId());
			String attrName = datapod.getAttributes().get(attrId).getName();
			dataQual.getAttribute().setAttrName(attrName);
		}
		if (dataQual.getRefIntegrityCheck().getRef() != null) {
			String uuid = dataQual.getRefIntegrityCheck().getRef().getUuid();
			Datapod datapod1 = datapodServiceImpl.findLatestByUuid(uuid);
			dataQual.getRefIntegrityCheck().getRef().setName(datapod1.getName());
			int attrId = Integer.parseInt(dataQual.getRefIntegrityCheck().getAttrId());
			String attrName1 = datapod1.getAttributes().get(attrId).getName();
			dataQual.getRefIntegrityCheck().setAttrName(attrName1);
		}
		return dataQual;
	}*/

	/*public List<DataQual> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		List<DataQual> dqList;
		if (appUuid != null) {
			dqList = iDataQualDao.findAllVersion(appUuid, uuid);
		} else {
			dqList = iDataQualDao.findAllVersion(uuid);
		}
		return resolveName(dqList);
	}*/

	public DataQualExec create(String dataQualUUID, String dataQualVersion,
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		try {
			return (DataQualExec) super.create(dataQualUUID, dataQualVersion, MetaType.dq, MetaType.dqExec, null, refKeyMap, datapodList, dagExec);
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create DQExec.");
			throw new Exception((message != null) ? message : "Can not create DQExec.");
		}
	}
	
	public DataQualExec create(String dataQualUUID, String dataQualVersion, DataQualExec dataQualExec,
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		try {
			return (DataQualExec) super.create(dataQualUUID, dataQualVersion, MetaType.dq, MetaType.dqExec, dataQualExec, refKeyMap, datapodList, dagExec);
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create DQExec.");
			throw new Exception((message != null) ? message : "Can not create DQExec.");
		}
	}
	
	public DataQualExec execute(String dataqualUUID, String dataqualVersion, DataQualExec dataqualExec,
			DataQualGroupExec dataqualGroupExec, Mode runMode) throws Exception {
		execute(dataqualUUID, dataqualVersion, null, dataqualExec, dataqualGroupExec, null, runMode);
		return dataqualExec;
	}

	public DataQualExec execute(String dataqualUUID, String dataqualVersion,
			ThreadPoolTaskExecutor metaExecutor, DataQualExec dataqualExec, DataQualGroupExec dataqualGroupExec, List<FutureTask<TaskHolder>> taskList, Mode runMode) throws Exception {
		Datapod targetDatapod = (Datapod) daoRegister
				.getRefObject(new MetaIdentifier(MetaType.datapod, dqInfo.getDqTargetUUID(), null));
		MetaIdentifier targetDatapodKey = new MetaIdentifier(MetaType.datapod, targetDatapod.getUuid(),
				targetDatapod.getVersion());		
		try {
			return (DataQualExec) super.execute(dataqualUUID, dataqualVersion, MetaType.dq, MetaType.dqExec, metaExecutor, dataqualExec, dataqualGroupExec, targetDatapodKey, taskList, runMode);
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Data Quality execution failed.");
			throw new Exception((message != null) ? message : "Data Quality execution failed.");
		}
	}
	
	@Override
	public BaseRuleExec execute(String uuid, String version, ThreadPoolTaskExecutor metaExecutor,
			BaseRuleExec baseRuleExec, BaseRuleGroupExec baseGroupExec, MetaIdentifier datapodKey,
			List<FutureTask<TaskHolder>> taskList, Mode runMode) throws Exception {
			return execute(uuid, version, metaExecutor, (DataQualExec) baseRuleExec, (DataQualGroupExec)baseGroupExec, taskList, runMode);
	}
	
	
	
	/*public DataQualExec create(String DataQualUUID, String DataQualVersion,
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		return create(DataQualUUID, DataQualVersion, null, refKeyMap, datapodList, dagExec);
	}

	public DataQualExec create(String dataQualUUID, String dataQualVersion, DataQualExec dataQualExec,
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		DataQual dataQual = null;
		MetaIdentifierHolder dataQualRef = new MetaIdentifierHolder();
		MetaIdentifier dataQualExecRef = null;

		if (StringUtils.isBlank(dataQualVersion)) {
			dataQual = iDataQualDao.findLatestByUuid(dataQualUUID, new Sort(Sort.Direction.DESC, "version"));
			dataQualVersion = dataQual.getVersion();
		} else {
			dataQual = iDataQualDao.findOneByUuidAndVersion(dataQualUUID, dataQualVersion);
		}

		if (dataQualExec == null) {
			dataQualExec = new DataQualExec();
			dataQualRef.setRef(new MetaIdentifier(MetaType.dq, dataQualUUID, dataQualVersion));
			dataQualExec.setDependsOn(dataQualRef);
			dataQualExec.setBaseEntity();
		}

		dataQualExec.setAppInfo(dataQual.getAppInfo());
		dataQualExec.setName(dataQual.getName());
		dataQualExecRef = new MetaIdentifier(MetaType.dqExec, dataQualExec.getUuid(), dataQualExec.getVersion());
		
		try {
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			List<Status> statusList = dataQualExec.getStatusList();
			if (Helper.getLatestStatus(statusList) != null 
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
				logger.info(" This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun. ");
				return dataQualExec;
			}
			
			logger.info("Before setting notstarted : " + dataQualExec.getUuid());
			synchronized (dataQualExec.getUuid()) {
				dataQualExec = (DataQualExec) commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.NotStarted);
			}
			dataQualExec.setExec(dqOperator.generateSql(dataQual, datapodList, dataQualExec, dagExec, usedRefKeySet));
			dataQualExec.setRefKeyList(new ArrayList<>(usedRefKeySet));

			synchronized (dataQualExec.getUuid()) {
				DataQualExec dataQualExec1 = (DataQualExec) daoRegister.getRefObject(dataQualExecRef);
				dataQualExec1.setExec(dataQualExec.getExec());
				dataQualExec1.setRefKeyList(dataQualExec.getRefKeyList());
				iDataQualExecDao.save(dataQualExec1);
			}
		} catch (Exception e) {
			logger.error(e);
			dataQualExec = ((DataQualExec) daoRegister.getRefObject(Helper.getMetaIdentifier(dataQualExec, MetaType.dqExec)));
			List<Status> statusList = dataQualExec.getStatusList();
			logger.info("Dataqual exec failed : " + dataQualExec.getUuid());
			synchronized (dataQualExec.getUuid()) {
				dataQualExec = (DataQualExec) commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.Failed);
			}
			e.printStackTrace();
			throw new RuntimeException();
		}
		return dataQualExec;
	}

	public DataQualExec execute(String dataqualUUID, String dataqualVersion, DataQualExec dataqualExec,
			DataQualGroupExec dataqualGroupExec) {
		execute(dataqualUUID, dataqualVersion, null, dataqualExec, dataqualGroupExec, null);
		return dataqualExec;
	}

	public DataQualExec execute(String dataqualUUID, String dataqualVersion,
			ThreadPoolTaskExecutor metaExecutor, DataQualExec dataqualExec, DataQualGroupExec dataqualGroupExec, List<FutureTask> taskList) {
		logger.info("Inside execute DQExec : " + dataqualExec.getUuid() + ":"+dataqualExec.getName());
		RunDataQualServiceImpl runDataqualServiceImpl = new RunDataQualServiceImpl();

		Datapod targetDatapod = (Datapod) daoRegister
				.getRefObject(new MetaIdentifier(MetaType.datapod, dqInfo.getDqTargetUUID(), null));
		MetaIdentifier targetDatapodKey = new MetaIdentifier(MetaType.datapod, targetDatapod.getUuid(),
				targetDatapod.getVersion());
		

		List<Status> statusList = dataqualExec.getStatusList();
		if (Helper.getLatestStatus(statusList) != null 
				&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
			logger.info(" This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun. ");
			return dataqualExec;
		}
		
		try {
			synchronized (dataqualExec.getUuid()) {
				dataqualExec = (DataQualExec) commonServiceImpl.setMetaStatus(dataqualExec, MetaType.dqExec, Status.Stage.InProgress);
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		runDataqualServiceImpl.setHdfsInfo(hdfsInfo);
		runDataqualServiceImpl.setiDataQualDao(iDataQualDao);
		runDataqualServiceImpl.setDqExec(dataqualExec);
		runDataqualServiceImpl.setDataqualExecServiceImpl(dataQualExecServiceImpl);
		runDataqualServiceImpl.setDatapodKey(targetDatapodKey); 
		runDataqualServiceImpl.setDqOperator(dqOperator);
		runDataqualServiceImpl.setDqUUID(dataqualUUID);
		runDataqualServiceImpl.setDqVersion(dataqualVersion);
		runDataqualServiceImpl.setHiveContext(hiveContext);
		runDataqualServiceImpl.setDataStoreServiceImpl(dataStoreServiceImpl);
		runDataqualServiceImpl.setDatasourceFactory(datasourceFactory);
		runDataqualServiceImpl.setDaoRegister(daoRegister);
		runDataqualServiceImpl.setExecFactory(execFactory);
		runDataqualServiceImpl.setDataqualGroupExec(dataqualGroupExec);
		runDataqualServiceImpl.setName(MetaType.dqExec+"_"+dataqualExec.getUuid()+"_"+dataqualExec.getVersion());
		
		try {
			
			if (metaExecutor != null) {
				FutureTask<TaskHolder> futureTask = new FutureTask<TaskHolder>(runDataqualServiceImpl);
				metaExecutor.execute(futureTask);
				taskList.add(futureTask);
				taskThreadMap.put(MetaType.dqExec+"_"+dataqualExec.getUuid()+"_"+dataqualExec.getVersion(), futureTask);
				logger.info(" taskThreadMap : " + taskThreadMap);
				String outputThreadName = futureTask.get(); 
				if(outputThreadName ==null){ 
					throw new Exception("sql not generated");
				}
                logger.info("Thread " + outputThreadName + " completed ");
				
			} else {
				runDataqualServiceImpl.execute();
				runDataqualServiceImpl = null;
			}
		} catch (Exception e) {
			Status failedStatus = new Status(Status.Stage.Failed, new Date());
			logger.info("Dataqual exec failed : " + dataqualExec.getUuid());
			try {
				synchronized (dataqualExec.getUuid()) {
					dataqualExec = (DataQualExec) commonServiceImpl.setMetaStatus(dataqualExec, MetaType.dqExec, Status.Stage.Failed);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			// throw new RuntimeException();
		} finally {
			
			statusList = dataqualExec.getStatusList();
			try {
				
				synchronized (dataqualExec.getUuid()) {
					dataqualExec = (DataQualExec) commonServiceImpl.setMetaStatus(dataqualExec, MetaType.dqExec, Status.Stage.Completed);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dataqualExec;
	}
*/	
	public String getTableName(Datapod datapod, Mode runMode) throws Exception {
		return dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
	}

	public List<Map<String, Object>> getDQResults(String dataQualExecUUID, String dataQualExecVersion, int offset, int limit, String sortBy, String order, String requestId, Mode runMode) throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset+limit;
			offset = offset+1;			

			DataStore datastore = dataStoreServiceImpl.findDatastoreByExec(dataQualExecUUID, dataQualExecVersion);
			
			data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order);
			
			/*String appUuid = null;
			boolean requestIdExistFlag = false;
			StringBuilder orderBy = new StringBuilder();
			DataStore datastore = dataStoreServiceImpl.findDatastoreByExec(dataQualExecUUID, dataQualExecVersion);
			dataStoreServiceImpl.setRunMode(runMode);
			String tableName = dataStoreServiceImpl.getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(), runMode);
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			ExecContext execContext = null;
			IExecutor exec = null;
			String sql= null;
			if (runMode.equals(Mode.ONLINE)) {
				execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark") || engine.getExecEngine().equalsIgnoreCase("livy_spark"))
						? helper.getExecutorContext(engine.getExecEngine()) : helper.getExecutorContext(ExecContext.spark.toString());
				appUuid = commonServiceImpl.getApp().getUuid();
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			exec = execFactory.getExecutor(execContext.toString());
			appUuid = commonServiceImpl.getApp().getUuid();
			if(requestId == null || requestId.equals("null") || requestId.isEmpty())	{
				if(datasource.getType().toLowerCase().toLowerCase().contains(ExecContext.spark.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
					data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over(ORDER BY version DESC) AS rownum, * FROM " + tableName + ") AS tab WHERE rownum >= " +offset+ " AND rownum <= " + limit, appUuid);
				}else {
					if(datasource.getType().toLowerCase().toLowerCase().contains("oracle"))
						if(runMode.equals(Mode.ONLINE))
							data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
						else	
							data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum< " + limit, appUuid);
					else
						data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
				}
			} else {
				List<String> orderList = Arrays.asList(order.split("\\s*,\\s*"));
				List<String> sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));		
				
				for(int i=0; i<sortList.size(); i++) 
						orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));			
				if (requestId != null) {
					String tabName = null;
					for(Map.Entry<String, String> entry : requestMap.entrySet()) {
						String id = entry.getKey();
						if(id.equals(requestId)) {
							requestIdExistFlag = true;
						}					
					}
					if(requestIdExistFlag) {						
						tabName = requestMap.get(requestId);
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
							data = exec.executeAndFetch("SELECT * FROM "+tabName+" WHERE rownum >= " + offset + " AND rownum <= " + limit, appUuid);
						} else {
							if(datasource.getType().toLowerCase().toLowerCase().contains("oracle"))
								if(runMode.equals(Mode.ONLINE))
									data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
								else	
									data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum< " + limit, appUuid);
							else{
								data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
							}
						}
					}else { 
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
								data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM (SELECT * FROM "
										+tableName+" ORDER BY "+ orderBy.toString() +") AS tab) AS tab1", appUuid);
							}else {
								if(datasource.getType().toLowerCase().toLowerCase().contains("oracle"))
									if(runMode.equals(Mode.ONLINE))
										data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
									else	
										data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum< " + limit, appUuid);
								else{
									data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
								}
							}
						tabName = requestId.replace("-", "_");
						requestMap.put(requestId, tabName);
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
							data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit, null);
						}else {
							if(datasource.getType().toLowerCase().toLowerCase().contains("oracle"))
								if(runMode.equals(Mode.ONLINE))
									data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
								else	
									data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum< " + limit, appUuid);
							else{
								data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
							}
						}
					}
				}
			}*/
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("402", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.");
			throw new Exception((message != null) ? message : "Table not found.");
		}
		return data;
	}

	/*public DataQual getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iDataQualDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iDataQualDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}

	public MetaIdentifierHolder saveAs(DataQual dq) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		DataQual dqNew = new DataQual();
		dqNew.setName(dq.getName()+"_copy");
		dqNew.setActive(dq.getActive());		
		dqNew.setDesc(dq.getDesc());		
		dqNew.setTags(dq.getTags());	
		dqNew.setTarget(dq.getTarget());
		dqNew.setAttribute(dq.getAttribute());
		dqNew.setDependsOn(dq.getDependsOn());
		dqNew.setDuplicateKeyCheck(dq.getDuplicateKeyCheck());
		dqNew.setNullCheck(dq.getNullCheck());
		dqNew.setValueCheck(dq.getValueCheck());
		dqNew.setRangeCheck(dq.getRangeCheck());
		dqNew.setDataTypeCheck(dq.getDataTypeCheck());
		dqNew.setDateFormatCheck(dq.getDateFormatCheck());
		dqNew.setCustomFormatCheck(dq.getCustomFormatCheck());
		dqNew.setLengthCheck(dq.getLengthCheck());
		dqNew.setRefIntegrityCheck(dq.getRefIntegrityCheck());
		dqNew.setPublished(dq.getPublished());
		//dqNew.setStdDevCheck(dq.getStdDevCheck());
		dqNew.setFilterInfo(dq.getFilterInfo());
		save(dqNew);
		ref.setType(MetaType.dq);
		ref.setUuid(dqNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}

	public List<BaseEntity> findList(List<? extends BaseEntity> dqList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity dq : dqList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = dq.getId();
			String uuid = dq.getUuid();
			String version = dq.getVersion();
			String name = dq.getName();
			String desc = dq.getDesc();
			String published=dq.getPublished();
			MetaIdentifierHolder createdBy = dq.getCreatedBy();
			String createdOn = dq.getCreatedOn();
			String[] tags = dq.getTags();
			String active = dq.getActive();
			List<MetaIdentifierHolder> appInfo = dq.getAppInfo();
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

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		DataQualExec dataQualExec = iDataQualExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.dq);
		mi.setUuid(dataQualExec.getDependsOn().getRef().getUuid());
		mi.setVersion(dataQualExec.getDependsOn().getRef().getVersion());
		return mi;
	}
	public void restart(String type,String uuid,String version, Mode runMode) throws JsonProcessingException{
		//DataQualExec dataQualExec= dataQualExecServiceImpl.findOneByUuidAndVersion(uuid,version);
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(uuid,version, MetaType.dqExec.toString());
//		try {
//			dataQualExec = create(dataQualExec.getDependsOn().getRef().getUuid(),dataQualExec.getDependsOn().getRef().getVersion(),dataQualExec, null, null, null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		try {
			dataQualExec = (DataQualExec) parse(uuid,version, null, null, null, runMode);
			execute(dataQualExec.getDependsOn().getRef().getUuid(),dataQualExec.getDependsOn().getRef().getVersion(),dataQualExec,null, runMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec, Mode runMode)
			throws Exception {
		logger.info("Inside dataQualServiceImpl.parse");
		DataQual dataQual = null;
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.dqExec.toString());
		dataQual = (DataQual) commonServiceImpl.getOneByUuidAndVersion(dataQualExec.getDependsOn().getRef().getUuid(), dataQualExec.getDependsOn().getRef().getVersion(), MetaType.dq.toString());
		try{
		dataQualExec.setExec(dqOperator.generateSql(dataQual, datapodList, dataQualExec, dagExec, usedRefKeySet, runMode));
		dataQualExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		
		synchronized (dataQualExec.getUuid()) {
			DataQualExec dataQualExec1 = (DataQualExec) daoRegister.getRefObject(
					new MetaIdentifier(MetaType.dqExec, dataQualExec.getUuid(), dataQualExec.getVersion()));
			dataQualExec1.setExec(dataQualExec.getExec());
			dataQualExec1.setRefKeyList(dataQualExec.getRefKeyList());
			commonServiceImpl.save(MetaType.dqExec.toString(), dataQualExec1);
			dataQualExec1 = null;
		}
		}catch(Exception e){
			commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.Failed);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Failed data quality parsing.");
			throw new Exception((message != null) ? message : "Failed data quality parsing.");
		}
		return dataQualExec;
	}
	public HttpServletResponse download(String dataQualExecUUID, String dataQualExecVersion, String format, String download, int offset,
			int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId,
			Mode runMode) throws Exception {
		
		List<Map<String, Object>> results =getDQResults(dataQualExecUUID,dataQualExecVersion,offset,limit,sortBy,order,requestId, runMode);
		response = commonServiceImpl.download(dataQualExecUUID, dataQualExecVersion, format, offset, limit, response, rowLimit, sortBy, order, requestId, runMode, results,MetaType.downloadExec,new MetaIdentifierHolder(new MetaIdentifier(MetaType.dqExec,dataQualExecUUID,dataQualExecVersion)));
	/*	
		try {
			FileOutputStream fileOut = null;
			response.setContentType("application/xml charset=utf-16");
			response.setHeader("Content-type", "application/xml");
			HSSFWorkbook workbook = WorkbookUtil.getWorkbook(results);

			String downloadPath = Helper.getPropertyValue("framework.file.download.path");
			response.addHeader("Content-Disposition", "attachment; filename=" + dataQualExecUUID + ".xlsx");
			ServletOutputStream os = response.getOutputStream();
			workbook.write(os);

			fileOut = new FileOutputStream(downloadPath + "/" + dataQualExecUUID + "_" + dataQualExecVersion + ".xlsx");
			workbook.write(fileOut);
			os.write(workbook.getBytes());
			os.close();
			fileOut.close();

		} catch (IOException e1) {
			e1.printStackTrace();
			logger.info("exception caught while download file");
		}*/
		return response;

	}
	
	public String getSummarySql(String tableName) {
		String sql = "SELECT datapodname, attributeId, attributename, 'NULL CHECK' as check_type, 'PASS' as result_type, count(nullCheck_pass) AS count FROM " + tableName + " WHERE nullCheck_pass = 'Y' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL " 
					+ "SELECT datapodname, attributeId, attributename, 'VALUE CHECK' as check_type, 'PASS' as result_type, count(valueCheck_pass) AS count FROM " + tableName + " WHERE valueCheck_pass = 'Y' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+  "SELECT datapodname, attributeId, attributename, 'RANGE CHECK' as check_type, 'PASS' as result_type, count(rangeCheck_pass) AS count FROM " + tableName + " WHERE rangeCheck_pass = 'Y' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DATA TYPE CHECK' as check_type, 'PASS' as result_type, count(dataTypeCheck_pass) AS count FROM " + tableName + " WHERE dataTypeCheck_pass = 'Y' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DATA FORMAT CHECK' as check_type, 'PASS' as result_type, count(dataFormatCheck_pass) AS count FROM " + tableName + " WHERE dataFormatCheck_pass = 'Y' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'LENGTH CHECK' as check_type, 'PASS' as result_type, count(lengthCheck_pass) AS count FROM " + tableName + " WHERE lengthCheck_pass = 'Y' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'REF INT CHECK' as check_type, 'PASS' as result_type, count(refIntegrityCheck_pass) AS count FROM " + tableName + " WHERE refIntegrityCheck_pass = 'Y' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DUP CHECK' as check_type, 'PASS' as result_type, count(dupCheck_pass) AS count FROM " + tableName + " WHERE dupCheck_pass = 'Y' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'CUSTOM CHECK' as check_type, 'PASS' as result_type, count(customCheck_pass) AS count FROM " + tableName + " WHERE customCheck_pass = 'Y' GROUP BY datapodname, attributeId, attributename "
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'NULL CHECK' as check_type, 'FAIL' as result_type, count(nullCheck_pass) AS count FROM " + tableName + " WHERE nullCheck_pass = 'N' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL " 
					+ "SELECT datapodname, attributeId, attributename, 'VALUE CHECK' as check_type, 'FAIL' as result_type, count(valueCheck_pass) AS count FROM " + tableName + " WHERE valueCheck_pass = 'N' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+  "SELECT datapodname, attributeId, attributename, 'RANGE CHECK' as check_type, 'FAIL' as result_type, count(rangeCheck_pass) AS count FROM " + tableName + " WHERE rangeCheck_pass = 'N' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DATA TYPE CHECK' as check_type, 'FAIL' as result_type, count(dataTypeCheck_pass) AS count FROM " + tableName + " WHERE dataTypeCheck_pass = 'N' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DATA FORMAT CHECK' as check_type, 'FAIL' as result_type, count(dataFormatCheck_pass) AS count FROM " + tableName + " WHERE dataFormatCheck_pass = 'N' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'LENGTH CHECK' as check_type, 'FAIL' as result_type, count(lengthCheck_pass) AS count FROM " + tableName + " WHERE lengthCheck_pass = 'N' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'REF INT CHECK' as check_type, 'FAIL' as result_type, count(refIntegrityCheck_pass) AS count FROM " + tableName + " WHERE refIntegrityCheck_pass = 'N' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'DUP CHECK' as check_type, 'FAIL' as result_type, count(dupCheck_pass) AS count FROM " + tableName + " WHERE dupCheck_pass = 'N' GROUP BY datapodname, attributeId, attributename " 
					+ " UNION ALL "
					+ "SELECT datapodname, attributeId, attributename, 'CUSTOM CHECK' as check_type, 'FAIL' as result_type, count(customCheck_pass) AS count FROM " + tableName + " WHERE customCheck_pass = 'N' GROUP BY datapodname, attributeId, attributename ";
		return sql;
	}
	
	
	public List<Map<String, Object>> getDataQualSummary(String dataQualExecUUID, String dataQualExecVersion, Mode runMode) {
		
		DataStore datastore = dataStoreServiceImpl.findDatastoreByExec(dataQualExecUUID, dataQualExecVersion);
		dataStoreServiceImpl.setRunMode(runMode);
		String tableName = null;
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			tableName = dataStoreServiceImpl.getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(),
					runMode);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		ExecContext execContext = null;
		IExecutor exec = null;
		// String sql = null;
		String appUuid = null;
		if (runMode.equals(Mode.ONLINE)) {
			execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark")
					|| engine.getExecEngine().equalsIgnoreCase("livy_spark"))
							? helper.getExecutorContext(engine.getExecEngine())
							: helper.getExecutorContext(ExecContext.spark.toString());
		} else {
			execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
		}
		exec = execFactory.getExecutor(execContext.toString());
		appUuid = commonServiceImpl.getApp().getUuid();
		if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
				|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
				|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
				|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
			data = exec
					.executeAndFetch(getSummarySql(tableName), appUuid);
		} else {
			if (datasource.getType().toLowerCase().contains("oracle"))
				if (runMode.equals(Mode.ONLINE))
					data = exec.executeAndFetch(getSummarySql(tableName), appUuid);
				else
					data = exec.executeAndFetch(getSummarySql(tableName), appUuid);
			else {
				data = exec.executeAndFetch(getSummarySql(tableName), appUuid);
			}
		}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

}
