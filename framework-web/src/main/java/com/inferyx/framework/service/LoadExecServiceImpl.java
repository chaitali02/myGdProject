package com.inferyx.framework.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.inferyx.framework.dao.ILoadExecDao;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.User;

@Service
public class LoadExecServiceImpl {
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ILoadExecDao iLoadExecDao;
	@Autowired
	LoadServiceImpl loadServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(LoadExecServiceImpl.class);

	/********************** UNUSED **********************/
	/*public LoadExec findLatest() {
		LoadExec loadexec=null;
		if(iLoadExecDao.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			loadexec=resolveName(iLoadExecDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return loadexec ;
	}*/

	/********************** UNUSED **********************/
	/*public List<LoadExec> findOneByrule(String loadUuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iLoadExecDao.findOneByLoad(appUuid, loadUuid);
	}*/

	/********************** UNUSED **********************/
	/*public List<LoadExec> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iLoadExecDao.findAll();
		}
		return iLoadExecDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*public LoadExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iLoadExecDao.findOneById(appUuid, id);
		}
		return iLoadExecDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public LoadExec save(LoadExec loadExec) {
		if (loadExec.getAppInfo() == null) {
			MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
			List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
			metaIdentifierHolderList.add(meta);
			loadExec.setAppInfo(metaIdentifierHolderList);
		}
		loadExec.setBaseEntity();
		return iLoadExecDao.save(loadExec);
	}*/

	/********************** UNUSED **********************/
	/*public LoadExec findLatestByUuid(String loadExecUUID) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iLoadExecDao.findLatestByUuid(loadExecUUID, new Sort(Sort.Direction.DESC, "version"));
		}
		return iLoadExecDao.findLatestByUuid(appUuid, loadExecUUID, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		LoadExec LoadExec = iLoadExecDao.findOneById(appUuid, id);
		LoadExec.setActive("N");
		iLoadExecDao.save(LoadExec);
		String ID = LoadExec.getId();
		iLoadExecDao.delete(ID);
	}*/

	/********************** UNUSED **********************/
	/*public LoadExec findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iLoadExecDao.findAllByUuid(appUuid, uuid);

	}*/

	/********************** UNUSED **********************/
	/*public List<LoadExec> findAllLatest() {
		Aggregation loadExecAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<LoadExec> loadExecResults = mongoTemplate.aggregate(loadExecAggr,"loadexec",
				LoadExec.class);
		List<LoadExec> loadExecList = loadExecResults.getMappedResults();
		// Fetch the LoadExec details for each id
		List<LoadExec> result = new ArrayList<LoadExec>();
		for (LoadExec v : loadExecList) {
			LoadExec loadExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				loadExecLatest = iLoadExecDao.findOneByUuidAndVersion(appUuid, v.getId(), v.getVersion());
			} else {
				loadExecLatest = iLoadExecDao.findOneByUuidAndVersion(v.getId(), v.getVersion());
			}
			if(loadExecLatest!=null)
			{
			result.add(loadExecLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
/*	public List<LoadExec> findAllLatestActive() {
		Aggregation loadExecAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<LoadExec> loadExecResults = mongoTemplate.aggregate(loadExecAggr, "loadexec",
				LoadExec.class);
		List<LoadExec> loadExecList = loadExecResults.getMappedResults();

		// Fetch the LoadExec details for each id
		List<LoadExec> result = new ArrayList<LoadExec>();
		for (LoadExec r : loadExecList) {
			LoadExec loadExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				loadExecLatest = iLoadExecDao.findOneByUuidAndVersion(appUuid, r.getId(), r.getVersion());
			} else {
				loadExecLatest = iLoadExecDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if (loadExecLatest != null) {
				result.add(loadExecLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public LoadExec findOneByUuidAndVersion(String uuid, String version) {
		// String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iLoadExecDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else {
			return iLoadExecDao.findOneByUuidAndVersion(uuid, version);
		}
	}*/

	/********************** UNUSED **********************/
	/*public List<LoadExec> resolveName(List<LoadExec> loadExec) {
		List<LoadExec> loadExecList = new ArrayList<>();
		for (LoadExec loadE : loadExec) {
			LoadExec LoadExecLatest = findOneByUuidAndVersion(loadE.getUuid(), loadE.getVersion());
			String createdByRefUuid = loadE.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			LoadExecLatest.getCreatedBy().getRef().setName(user.getName());
			loadExecList.add(LoadExecLatest);
		}
		return loadExecList;
	}*/

	/********************** UNUSED **********************/
	/*public LoadExec resolveName(LoadExec loadExec) {
		String createdByRefUuid = loadExec.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		loadExec.getCreatedBy().getRef().setName(user.getName());
		
		String dependsOnUuid=loadExec.getDependsOn().getRef().getUuid();
		com.inferyx.framework.domain.Load load=loadServiceImpl.findLatestByUuid(dependsOnUuid);
		loadExec.getDependsOn().getRef().setName(load.getName());
		return loadExec;
	}*/

	/********************** UNUSED **********************/
	/*public List<LoadExec> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iLoadExecDao.findAllVersion(appUuid, uuid);
		} else
			return iLoadExecDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public List<LoadExec> findLoadExecByRule(String loadUuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		// String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		List<LoadExec> loadExecList = null;
		if (appUuid == null) {
			loadExecList = iLoadExecDao.findOneByLoad(loadUuid);
		} else {
			loadExecList = iLoadExecDao.findOneByLoad(appUuid, loadUuid);
		}
		List<LoadExec> resolvedLoadExecList = new ArrayList<>();
		for (LoadExec loadExec : loadExecList) {
			resolveName(loadExec);
			resolvedLoadExecList.add(loadExec);
		}
		return resolvedLoadExecList;
	}*/

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//LoadExec loadExec = iLoadExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		LoadExec loadExec = (LoadExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.loadExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.load);
		mi.setUuid(loadExec.getDependsOn().getRef().getUuid());
		mi.setVersion(loadExec.getDependsOn().getRef().getVersion());
		return mi;
	}	
}
