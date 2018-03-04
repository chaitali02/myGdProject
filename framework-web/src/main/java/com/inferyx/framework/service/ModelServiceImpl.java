package com.inferyx.framework.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.param.ParamMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.dao.IAlgorithmDao;
import com.inferyx.framework.dao.IModelDao;
import com.inferyx.framework.dao.IModelExecDao;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ModelExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.PythonExecutor;
import com.inferyx.framework.executor.RExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ModelServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IModelDao iModelDao;
	@Autowired
	IAlgorithmDao iAlgorithmDao;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	IModelExecDao iModelExecDao;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	ModelExecServiceImpl modelExecServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	ParamListServiceImpl paramListServiceImpl;
	@Autowired
	SparkContext sparkContext;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	DatasetServiceImpl datasetServiceImpl;
	@Autowired
	private AlgorithmServiceImpl algorithmServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	private Mode runMode;
	@Autowired
	private DataFrameService dataFrameService;
	@Autowired
	SessionHelper sessionHelper;
	@Autowired
	private ExecutorFactory execFactory;
	
	static final Logger logger = Logger.getLogger(ModelServiceImpl.class);
	CustomLogger customLogger = new CustomLogger();
	
	/********************** UNUSED **********************/
	/*public Model findLatest() throws JsonProcessingException {
		return resolveName(iModelDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Model findAllByUuid(String uuid) {
		return iModelDao.findAllByUuid(uuid);	
	}*/

	/********************** UNUSED **********************/
	/*public Model findLatestByUuid(String uuid){
		return iModelDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public Model findOneByUuidAndVersion(String uuid,String version){
		return iModelDao.findOneByUuidAndVersion(uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public Model findOneById(String id){
		return iModelDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public List<Model> findAll(){
		return iModelDao.findAll();
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String Id){
		Model model = iModelDao.findOne(Id);
		model.setActive("N");
		iModelDao.save(model);
//		String ID=application.getId();
//		iApplicationDao.delete(ID);
//		application.exportBaseProperty();
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public Model save(Model model) throws Exception{
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		model.setAppInfo(metaIdentifierHolderList);
		model.setBaseEntity();
		Model app=iModelDao.save(model);
		registerGraph.updateGraph((Object) app, MetaType.model);
		return app;
	}*/

	/**
	 * @return the runMode
	 */
	public Mode getRunMode() {
		return runMode;
	}

	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(Mode runMode) {
		this.runMode = runMode;
	}

	/********************** UNUSED **********************/
	/*public List<Model> resolveName(List<Model> models) throws JsonProcessingException {
		List<Model> modelList = new ArrayList<Model>(); 
		for(Model model : models)
		{
			String createdByRefUuid = model.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			model.getCreatedBy().getRef().setName(user.getName());
			modelList.add(model);
		}
		return modelList;
	}*/
	
	public Model resolveName(Model model) throws JsonProcessingException {
		if (model.getCreatedBy() != null) {
			String createdByRefUuid = model.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			model.getCreatedBy().getRef().setName(user.getName());
		}
		if (model.getAppInfo() != null) {
			for (int i = 0; i < model.getAppInfo().size(); i++) {
				String appUuid = model.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				model.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		if(model.getFeatures().size() >0){
			for (int i = 0; i < model.getFeatures().size(); i++) {
				String attributeId = model.getFeatures().get(i).getAttrId();
				//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(model.getFeatures().get(i).getRef().getUuid());
				Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(model.getFeatures().get(i).getRef().getUuid(), MetaType.datapod.toString());
				String datapodName = datapodDO.getName();
				model.getFeatures().get(i).getRef().setName(datapodName);
				List<Attribute> attributeList = datapodDO.getAttributes();
				model.getFeatures().get(i).setAttrName(attributeList.get(Integer.parseInt(attributeId)).getName());
			}
		}
		Algorithm algo= null;
		if (model.getAlgorithm().getRef().getVersion() != null)
			algo = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(model.getAlgorithm().getRef().getUuid(), model.getAlgorithm().getRef().getVersion(), MetaType.algorithm.toString());
		else 
			algo = (Algorithm) commonServiceImpl.getLatestByUuid(model.getAlgorithm().getRef().getUuid(), MetaType.algorithm.toString());
				
		if(model.getLabel().getRef() != null && algo.getLabelRequired().equalsIgnoreCase("Y")){
			String attributeId = model.getLabel().getAttrId();
			List<Attribute> attributeList = null;
			if(model.getSource().getRef().getType().toString().equals(MetaType.datapod.toString())){
				//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(model.getLabel().getRef().getUuid());
				Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(model.getLabel().getRef().getUuid(), MetaType.datapod.toString());
				model.getLabel().getRef().setName(datapodDO.getName());
				attributeList = datapodDO.getAttributes();
				model.getLabel().setAttrName(attributeList.get(Integer.parseInt(attributeId)).getName());
			} else if(model.getSource().getRef().getType().toString().equals(MetaType.dataset.toString())){
				//Dataset dataset = datasetServiceImpl.findLatestByUuid(model.getLabel().getRef().getUuid());
				DataSet dataset = (DataSet) commonServiceImpl.getLatestByUuid(model.getLabel().getRef().getUuid(), MetaType.dataset.toString());
				model.getLabel().getRef().setName(dataset.getName());
				List<AttributeSource> attributeSourceList = dataset.getAttributeInfo();
				model.getLabel().setAttrName(attributeSourceList.get(Integer.parseInt(attributeId)).getAttrSourceName());
			}
		}
		return model;
	}	

	/********************** UNUSED **********************/
	/*public List<Model> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iModelDao.findAllVersion(appUuid, uuid);
		} else
			return iModelDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public List<Model> findAllLatest() {	
			   Aggregation ModelAggr = newAggregation(group("uuid").max("version").as("version"));
			   AggregationResults<Model> ModelResults = mongoTemplate.aggregate(ModelAggr, "model", Model.class);	   
			   List<Model> ModelList = ModelResults.getMappedResults();

			   // Fetch the relation details for each id
			   List<Model> result=new  ArrayList<Model>();
			   for(Model a :ModelList)
			   {   
				   Model ModelLatest = iModelDao.findOneByUuidAndVersion(a.getId(),a.getVersion());
				   result.add(ModelLatest);
			   }
			   return result;			
	}*/

	/********************** UNUSED **********************/
	/*public List<Model> findAllLatestActive() 	
	{ 
		Aggregation appAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Model> appResults = mongoTemplate.aggregate(appAggr, "model", Model.class);	   
	   List<Model> appList = appResults.getMappedResults();

	   // Fetch the application details for each id
	   List<Model> result=new  ArrayList<Model>();
	   for(Model a : appList)
	   {   		     
		   Model appLatest = iModelDao.findOneByUuidAndVersion(a.getId(), a.getVersion());  		   
		   result.add(appLatest);
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Model model) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Model appNew = new Model();
		appNew.setName(model.getName()+"_copy");
		appNew.setActive(model.getActive());		
		appNew.setDesc(model.getDesc());		
		appNew.setTags(model.getTags());	
		save(appNew);
		ref.setType(MetaType.model);
		ref.setUuid(appNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/
	
	/********************** UNUSED **********************/	
	/*public List<BaseEntity> findList(List<? extends BaseEntity> modelList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity model : modelList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = model.getId();
			String uuid = model.getUuid();
			String version = model.getVersion();
			String name = model.getName();
			String desc = model.getDesc();
			String published=model.getPublished();
			MetaIdentifierHolder createdBy = model.getCreatedBy();
			String createdOn = model.getCreatedOn();
			String[] tags = model.getTags();
			String active = model.getActive();
			List<MetaIdentifierHolder> appInfo = model.getAppInfo();
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
	
	public ModelExec create( String modelUUID, String modelVersion, ExecParams execParams, ParamMap paramMap, ModelExec modelExec) throws Exception {
		MetaIdentifierHolder modelRef = new MetaIdentifierHolder();
		//Model model = null;

		//model = iModelDao.findOneByUuidAndVersion(modelUUID, modelVersion);
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelUUID, modelVersion, MetaType.model.toString());
		
		String logPath = null;
		if (modelExec == null) {
			modelExec = new ModelExec();
			modelRef.setRef(new MetaIdentifier(MetaType.model, modelUUID, modelVersion));
			modelExec.setDependsOn(modelRef);
			modelExec.setBaseEntity();
			
			/*
			 * 
			 * log file_name formation : modelexecuuid + modelexecversion + modelversion
			 * 
			 */
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				logPath = Helper.getPropertyValue("framework.model.log.path") + "/" + modelExec.getUuid() + "_" + modelExec.getVersion() + "_"+ model.getVersion()+".log";
			}
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(),
						"Created raw model exec, uuid: " + modelExec.getUuid(), 
						logPath,
						Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
		}
		modelExec.setExecParams(execParams);
		try {
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			List<Status> statusList = modelExec.getStatusList();
			if (statusList == null) {
				statusList = new ArrayList<Status>();
			}
			modelExec.setName(model.getName());
			modelExec.setAppInfo(model.getAppInfo());	
			//iModelExecDao.save(modelExec);
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(), 
						"Saving raw modelExec, uuid: " + modelExec.getUuid(),
						logPath, 
						Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
			commonServiceImpl.save(MetaType.modelExec.toString(), modelExec);
			
			if (Helper.getLatestStatus(statusList) != null 
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
				logger.info(" This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun. ");
				if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					customLogger.writeLog(this.getClass(),
							"This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun.", 
							logPath,
							Thread.currentThread().getStackTrace()[1].getLineNumber());
				}
				return modelExec;
			}
			
			//modelExec.setExec(dqOperator.generateSql(dataQual, datapodList, dataQualExec, dagExec, usedRefKeySet));
			modelExec.setRefKeyList(new ArrayList<>(usedRefKeySet));

			modelExec = (ModelExec) commonServiceImpl.setMetaStatus(modelExec, MetaType.modelExec, Status.Stage.NotStarted);
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(),
						modelExec.getStatusList().size()>0 ? "Latest status: "+modelExec.getStatusList().get(modelExec.getStatusList().size()-1).getStage() : "Status list is empty", 
						logPath,
						Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
		} catch (Exception e) {
			logger.error(e);
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeErrorLog(this.getClass(), StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), 
						logPath,
						Thread.currentThread().getStackTrace()[1].getLineNumber());
			
			}
			
			modelExec = (ModelExec) commonServiceImpl.setMetaStatus(modelExec, MetaType.modelExec, Status.Stage.Failed);
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(), modelExec.getStatusList().size()>0 ? "Model creation failed, status: "+modelExec.getStatusList().get(modelExec.getStatusList().size()-1).getStage() : "Status list is empty", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
			throw new Exception("Model creation failed");
		}		
		return modelExec;
	}
	
	public ModelExec train(String modelUUID, String modelVersion, ModelExec modelExec, ExecParams execParams, ParamMap paramMap) throws FileNotFoundException, IOException{
		RunModelServiceImpl runModelServiceImpl = new RunModelServiceImpl();
		List<Status> statusList = modelExec.getStatusList();
		if (statusList == null) {
			statusList = new ArrayList<>();
			modelExec.setStatusList(statusList);
		}
		
		//Model model = iModelDao.findOneByUuidAndVersion(modelUUID, modelVersion);
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelUUID, modelVersion, MetaType.model.toString());
		if(!model.getType().equalsIgnoreCase(ExecContext.R.toString()) && !model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
			runModelServiceImpl.setAlgorithmUUID(model.getAlgorithm().getRef().getUuid());
			runModelServiceImpl.setAlgorithmVersion(model.getAlgorithm().getRef().getVersion());
		}
		runModelServiceImpl.setAlgorithmServiceImpl(algorithmServiceImpl);
		runModelServiceImpl.setDataStoreServiceImpl(dataStoreServiceImpl);
		runModelServiceImpl.setSparkContext(sparkContext);
		runModelServiceImpl.setModelExec(modelExec);
		runModelServiceImpl.setModel(model);
		runModelServiceImpl.setModelExecServiceImpl(modelExecServiceImpl);
		runModelServiceImpl.setHdfsInfo(hdfsInfo);
		runModelServiceImpl.setExecParams(execParams);
		runModelServiceImpl.setParamSetServiceImpl(paramSetServiceImpl);
		runModelServiceImpl.setParamListServiceImpl(paramListServiceImpl);
		runModelServiceImpl.setParamMap(paramMap);
		runModelServiceImpl.setCommonServiceImpl(commonServiceImpl);
		runModelServiceImpl.setRunMode(runMode);
		runModelServiceImpl.setDataFrameService(dataFrameService);
		runModelServiceImpl.setSessionContext(sessionHelper.getSessionContext());
		runModelServiceImpl.setCustomLogger(new CustomLogger());
		runModelServiceImpl.setModelType(model.getType());
		runModelServiceImpl.setModelServiceImpl(this);
		if(model.getType().equalsIgnoreCase(ExecContext.R.toString())|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) 
			runModelServiceImpl.setLogPath(Helper.getPropertyValue("framework.model.log.path") + "/" + modelExec.getUuid() + "_" + modelExec.getVersion() + "_"+ model.getVersion()+".log");
		runModelServiceImpl.setExecFactory(execFactory);
		runModelServiceImpl.setSecurityServiceImpl(securityServiceImpl);
		runModelServiceImpl.start();
		return modelExec;
	}

	public String uploadScript(MultipartFile customScriptFile, Model model, String modelId, String modelUuid, String modelVersion, String scriptName) {
		//scriptName = customScriptFile.getOriginalFilename();
		//String uploadPath = null;
		String scriptPath = null;
		try {
			//uploadPath = Helper.getProperty("framework.r.location")+"/";
			
			//model.setName(scriptName.substring(0, scriptName.lastIndexOf(".")-1));
						
			scriptPath = Helper.getPropertyValue("framework.model.script.path")+"/" + scriptName;
			//model.setScriptName(scriptLocation);
			File scriptFile = new File(scriptPath);
			customScriptFile.transferTo(scriptFile);
			
			commonServiceImpl.save(MetaType.model.toString(), model);
		}catch (IllegalArgumentException
				| SecurityException 
				| NullPointerException
				| IllegalStateException 
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return scriptPath;
	}

	public Object executeScript(String type, String scriptName, String modelExecUuid, String modelExecVersion, String object) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Object result = null;
		Model model = (Model) commonServiceImpl.getDomainFromDomainExec(MetaType.modelExec.toString(), modelExecUuid, modelExecVersion);
		String logPath = Helper.getPropertyValue("framework.model.log.path") + "/" + modelExecUuid + "_" + modelExecVersion + "_"+ model.getVersion()+".log";
		String scriptPath = Helper.getPropertyValue("framework.model.script.path")+"/"+scriptName;
		
		IExecutor exec = execFactory.getExecutor(type);
		if(exec instanceof PythonExecutor) {
			PythonExecutor pythonExecutor = (PythonExecutor) exec;
			pythonExecutor.setLogPath(logPath);
		} else if(exec instanceof RExecutor) {
			RExecutor rExecutor = (RExecutor) exec;
			rExecutor.setLogPath(logPath);
		}		
		result = exec.executeScript(scriptPath, null);
		return result;
	}
	
	public List<String> readLog(String filePath, String type, String modelExecUUID, String modelExecVersion) throws IOException {
		if(filePath == null) {
			ModelExec modelExec = (ModelExec) commonServiceImpl.getOneByUuidAndVersion(modelExecUUID, modelExecVersion, type);
			MetaIdentifierHolder dependsOn = modelExec.getDependsOn();
			filePath = Helper.getPropertyValue("framework.model.log.path") + "/" + modelExec.getUuid() + "_" + modelExec.getVersion() + "_" + dependsOn.getRef().getVersion() + ".log";
		}
		FileInputStream fstream = new FileInputStream(filePath);
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(fstream));
		String strLine = "";
		StringBuilder log = new StringBuilder();
		List<String> logList = new ArrayList<>();
	   
		while ((strLine = buffReader.readLine()) != null)   {
			log.append(strLine).append("\n");
			//logList.add(strLine);
		}
		logList.add(log.toString());
		buffReader.close();
		fstream.close();
		return logList;
	}
}