package com.inferyx.framework.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.feature.RFormula;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.dmg.pmml.PMML;
import org.jpmml.model.JAXBUtil;
import org.jpmml.sparkml.ConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IModelExecDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ModelExec;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;

@Service
public class ModelExecServiceImpl{
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private IModelExecDao iModelExecDao;	
	/*@Autowired
	private IModelDao iModelDao;	*/
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;	
	/*@Autowired
	private DatapodServiceImpl datapodServiceImpl;	*/
	@Autowired
	UserServiceImpl userServiceImpl;	
	@Autowired
	SecurityServiceImpl securityServiceImpl;	
	@Autowired
	ModelServiceImpl modelServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;	
    @Autowired
    private DataFrameService dataFrameService;
	@Autowired
	private ExecutorFactory execFactory; 
    
	
	static final Logger logger = Logger.getLogger(ModelExecServiceImpl.class);	

	/********************** UNUSED **********************/
	/*public ModelExec findLatest() {
		ModelExec modelexec=null;
		if(iModelExecDao.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			modelexec=resolveName(iModelExecDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return modelexec ;
	}*/	

	/********************** UNUSED **********************/
	/*public List<ModelExec> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iModelExecDao.findAll(); 
		}
		return iModelExecDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*public ModelExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iModelExecDao.findOneById(appUuid,id);
		}
		return iModelExecDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public ModelExec save(ModelExec modelExec) {
		if(modelExec.getAppInfo() == null)
		{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		modelExec.setAppInfo(metaIdentifierHolderList);
		}
		modelExec.setBaseEntity();
		return iModelExecDao.save(modelExec);		
	}*/

	/********************** UNUSED **********************/
	/*public ModelExec findLatestByUuid(String modelExecUUID) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iModelExecDao.findLatestByUuid(modelExecUUID,new Sort(Sort.Direction.DESC, "version"));
		}
		return iModelExecDao.findLatestByUuid(appUuid,modelExecUUID,new Sort(Sort.Direction.DESC, "version"));		
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		ModelExec DataQualExec = iModelExecDao.findOneById(appUuid,id);
		DataQualExec.setActive("N");
		iModelExecDao.save(DataQualExec);
//		String ID=DataQualExec.getId();
//		iDataQualExecDao.delete(ID);		
	}*/

	/********************** UNUSED **********************/
	/*public ModelExec findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iModelExecDao.findAllByUuid(appUuid,uuid);
		
	}*/

	/********************** UNUSED **********************/
	/*public List<ModelExec> findAllLatest()
	{		
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Aggregation modelExecAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<ModelExec> DataQualExecResults = mongoTemplate.aggregate(modelExecAggr, "modelexec", ModelExec.class);
		List<ModelExec> modelExecList = DataQualExecResults.getMappedResults();
		// Fetch the VizExec details for each id
		List<ModelExec> result = new ArrayList<ModelExec>();
		for (ModelExec v : modelExecList) {
			ModelExec modelExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
			//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();;
				modelExecLatest = iModelExecDao.findOneByUuidAndVersion(appUuid,v.getId(), v.getVersion());
			}
			else
			{
				modelExecLatest = iModelExecDao.findOneByUuidAndVersion(v.getId(), v.getVersion());
			}
			//logger.debug("datapodLatest is " + datapodLatest.getName());
			if(modelExecLatest != null)
			{
			result.add(modelExecLatest);
			}
		}	
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<ModelExec> findAllLatestActive() 	
	{	   
	   Aggregation modelExecAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<ModelExec> modelExecResults = mongoTemplate.aggregate(modelExecAggr,"modelexec", ModelExec.class);	   
	   List<ModelExec> modelExecList = modelExecResults.getMappedResults();

	   // Fetch the DataQualExec details for each id
	   List<ModelExec> result=new  ArrayList<ModelExec>();
	   for(ModelExec r : modelExecList)
	   {   
		   ModelExec modelExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				modelExecLatest = iModelExecDao.findOneByUuidAndVersion(appUuid,r.getId(), r.getVersion());
			}
			else
			{
				modelExecLatest = iModelExecDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if(modelExecLatest != null)
			{
			result.add(modelExecLatest);
			}
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public ModelExec findOneByUuidAndVersion(String uuid, String version){
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		//String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd";
		return iModelExecDao.findOneByUuidAndVersion(uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public List<ModelExec> resolveName(List<ModelExec> modelExec) {
		List<ModelExec> modelExecList = new ArrayList<>();
		for(ModelExec modelE : modelExec)
		{
		String createdByRefUuid = modelE.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		modelE.getCreatedBy().getRef().setName(user.getName());
		modelExecList.add(modelE);
		}
		return modelExecList;
	}*/

	public ModelExec resolveName(ModelExec modelExec) throws JsonProcessingException {
		String createdByRefUuid = modelExec.getCreatedBy().getRef().getUuid();
		//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		modelExec.getCreatedBy().getRef().setName(user.getName());
		
		String dependsOnUuid=modelExec.getDependsOn().getRef().getUuid();
		//com.inferyx.framework.domain.Model model=modelServiceImpl.findLatestByUuid(dependsOnUuid);
		com.inferyx.framework.domain.Model model = (Model) commonServiceImpl.getLatestByUuid(dependsOnUuid, MetaType.model.toString());
		modelExec.getDependsOn().getRef().setName(model.getName());

		if(modelExec.getResult() != null){
			String datastoreUuid=modelExec.getResult().getRef().getUuid();
			//DataStore dataStore=dataStoreServiceImpl.findAllByUuid(datastoreUuid);
			DataStore dataStore = (DataStore) commonServiceImpl.getLatestByUuid(datastoreUuid, MetaType.datastore.toString());
			   modelExec.getResult().getRef().setName(dataStore.getName());
		}
		return modelExec;
	}

	/********************** UNUSED **********************/
	/*public List<ModelExec> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iModelExecDao.findAllVersion(appUuid, uuid);
		}
		else
		return iModelExecDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public ModelExec getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iModelExecDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iModelExecDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(ModelExec modelExec) {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		ModelExec modelExecNew = new ModelExec();
		modelExecNew.setName(modelExec.getName()+"_copy");
		modelExecNew.setActive(modelExec.getActive());		
		modelExecNew.setDesc(modelExec.getDesc());		
		modelExecNew.setTags(modelExec.getTags());
		modelExecNew.setStatusList(modelExec.getStatusList());
		modelExecNew.setDependsOn(modelExec.getDependsOn());
		modelExecNew.setExec(modelExec.getExec());
		modelExecNew.setExecParams(modelExec.getExecParams());
		modelExecNew.setResult(modelExec.getResult());
		save(modelExecNew);
		ref.setType(MetaType.dqExec);
		ref.setUuid(modelExecNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/
	
	public List<ModelExec> findModelExecByModel(String modelUUID) throws JsonProcessingException {
		List<ModelExec> modelExecList=null;
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if (appUuid != null)
			modelExecList = iModelExecDao.findOneByModel(appUuid, modelUUID);
		else
			modelExecList = iModelExecDao.findOneByModel(modelUUID);
		
		List<ModelExec> resolvedModelExecList = new ArrayList<>();
		for(ModelExec modelExec : modelExecList){
			resolveName(modelExec);
			resolvedModelExecList.add(modelExec);
		}
		return resolvedModelExecList;
	}

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> modelExecList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity modelExec : modelExecList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = modelExec.getId();
			String uuid = modelExec.getUuid();
			String version = modelExec.getVersion();
			String name = modelExec.getName();
			String desc = modelExec.getDesc();
			String published=modelExec.getPublished();
			MetaIdentifierHolder createdBy = modelExec.getCreatedBy();
			String createdOn = modelExec.getCreatedOn();
			String[] tags = modelExec.getTags();
			String active = modelExec.getActive();
			List<MetaIdentifierHolder> appInfo = modelExec.getAppInfo();
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
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//ModelExec modelExec = iModelExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		ModelExec modelExec = (ModelExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.modelExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.model);
		mi.setUuid(modelExec.getDependsOn().getRef().getUuid());
		mi.setVersion(modelExec.getDependsOn().getRef().getVersion());
		return mi;
	}
	
	public String[] getAttributeNames(Model model) throws JsonProcessingException{
		model = modelServiceImpl.resolveName(model);
		List<AttributeRefHolder> listAttrSource = model.getFeatures();
		List<String> listStr = new ArrayList<String>();
		for(AttributeRefHolder attrSource: listAttrSource){
			listStr.add(attrSource.getAttrName());
		}
		return listStr.toArray(new String[listStr.size()]);
	}
	
	public String getLabelName(Model model) throws JsonProcessingException{
		model = modelServiceImpl.resolveName(model);
		AttributeRefHolder attrSource = model.getLabel();
		return attrSource.getAttrName();
	}
	
	public List<String> getModelResults(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//ModelExec modelExec = iModelExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		ModelExec modelExec = (ModelExec) commonServiceImpl.getOneByUuidAndVersion(execUuid,  execVersion, MetaType.modelExec.toString());
		//Model model = iModelDao.findOneByUuidAndVersion(modelExec.getDependsOn().getRef().getUuid(), modelExec.getDependsOn().getRef().getVersion());
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelExec.getDependsOn().getRef().getUuid(),  modelExec.getDependsOn().getRef().getVersion(), MetaType.model.toString());
		//Datapod datapod = datapodServiceImpl.findOneByUuidAndVersion(model.getSource().getRef().getUuid(),  model.getSource().getRef().getVersion());
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(model.getSource().getRef().getUuid(), model.getSource().getRef().getVersion(), MetaType.datapod.toString());
		if(null == datapod){
			//datapod = datapodServiceImpl.findLatestByUuid(model.getSource().getRef().getUuid());
			datapod = (Datapod) commonServiceImpl.getLatestByUuid(model.getSource().getRef().getUuid(), MetaType.datapod.toString());
		}
		/*Dataset<Row> df = dataFrameService.getDataFrameByDataStore(modelExec.getResult().getRef().getUuid(),  modelExec.getResult().getRef().getVersion(),
				datapod);*/
		
		DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(modelExec.getResult().getRef().getUuid(),  modelExec.getResult().getRef().getVersion(), MetaType.datastore.toString());
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		List<String> strList = exec.fetchModelResults(datastore, datapod, securityServiceImpl.getAppInfo().getRef().getUuid());
		/*df.printSchema();
		List<String> strList = new ArrayList<>();
		
		for(Row row:df.javaRDD().collect()){
			strList.add(row.toString());
		}*/
		return strList;		
	}

	public String getModelScript(String modelUUID, String modelVersion) throws Exception {

		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelUUID,  modelVersion, MetaType.model.toString());
		String fileName = model.getScriptName();
	    String directoryLocation = Helper.getFileDirectoryByFileType(FileType.SCRIPT);
	    String filePath = directoryLocation+"/" + fileName;
	    File file = new File(filePath);
	    String fileContent = null;
	    
	    if (file.exists()) {
	    	logger.info("File " + file + " found.");
			byte[] encoded = Files.readAllBytes(Paths.get(filePath));
			fileContent = new String(encoded, Charset.defaultCharset());	    	
	    }	
	    else {
	    	logger.info("File " + file + " not found.");
	    	throw new FileNotFoundException();
	    }
		return fileContent;		
	}
	
	public String exportAsPMML(String execUuid, String execVersion) throws Exception {
		/* String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid(); */
		// ModelExec modelExec = iModelExecDao.findOneByUuidAndVersion(appUuid,
		// execUuid, execVersion);
		ModelExec modelExec = (ModelExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
				MetaType.modelExec.toString());
		// DataStore datastore =
		// dataStoreServiceImpl.findOneByUuidAndVersion(modelExec.getResult().getRef().getUuid(),
		// modelExec.getResult().getRef().getVersion());
		DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(
				modelExec.getResult().getRef().getUuid(), modelExec.getResult().getRef().getVersion(),
				MetaType.datastore.toString());
		/*org.apache.spark.ml.clustering.KMeans kmeansModel = org.apache.spark.ml.clustering.KMeans
				.load(datastore.getLocation());*/

		// Model model =
		// iModelDao.findOneByUuidAndVersion(modelExec.getDependsOn().getRef().getUuid(),
		// modelExec.getDependsOn().getRef().getVersion());
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelExec.getDependsOn().getRef().getUuid(),
				modelExec.getDependsOn().getRef().getVersion(), MetaType.model.toString());
		//
		// datapodServiceImpl.findOneByUuidAndVersion(model.getSource().getRef().getUuid(),
		// model.getSource().getRef().getVersion());
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(model.getSource().getRef().getUuid(),
				model.getSource().getRef().getVersion(), MetaType.datapod.toString());
		if (null == datapod) {
			// datapod =
			// datapodServiceImpl.findLatestByUuid(model.getSource().getRef().getUuid());
			datapod = (Datapod) commonServiceImpl.getLatestByUuid(model.getSource().getRef().getUuid(),
					MetaType.datapod.toString());
		}
		/*Dataset<Row> df = dataFrameService.getDataFrameByDataStore(modelExec.getResult().getRef().getUuid(),
				modelExec.getResult().getRef().getVersion(), datapod);*/

		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		String result = exec.fetchAndCreatePMML(datastore, datapod, securityServiceImpl.getAppInfo().getRef().getUuid());
		/*RFormula formula = new RFormula().setFormula("Species ~ .");
		DecisionTreeClassifier classifier = new DecisionTreeClassifier().setLabelCol(formula.getLabelCol())
				.setFeaturesCol(formula.getFeaturesCol());
		Pipeline pipeline = new Pipeline().setStages(new PipelineStage[] { formula, classifier });
		PipelineModel pipelineModel = pipeline.fit(df);

		PMML pmml = ConverterUtil.toPMML(df.schema(), pipelineModel);
		JAXBUtil.marshalPMML(pmml, new StreamResult(System.out));*/
		return result;

	}	
}
