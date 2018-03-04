/**
 * 
 */
package com.inferyx.framework.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.domain.BaseRule;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.factory.RuleExecFactory;
import com.inferyx.framework.factory.RunRuleFactory;
import com.inferyx.framework.register.DatapodRegister;

/**
 * @author joy
 *
 */
public abstract class RuleTemplate {
	
	@Autowired
	protected CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	protected DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	protected HDFSInfo hdfsInfo;
	@Resource(name="taskThreadMap")
	protected ConcurrentHashMap taskThreadMap;
	@Autowired
	RuleExecFactory ruleExecFactory;
	@Autowired
	protected RunRuleFactory runBaseFactory;
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	private RunBaseRuleService baseRuleService;
	@Autowired
	private DatapodRegister datapodRegister;
	@Autowired
	ConnectionFactory connFactory;
	@Autowired
	Engine engine;
	@Autowired
	Helper helper;
	
	static final Logger logger = Logger.getLogger(RuleTemplate.class);

	/**
	 * 
	 */
	public RuleTemplate() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Creates exec if it is not already present. 
	 * 
	 * 1. Checks whether uuid, type, execType, and corresponding baseRule is present
	 * 2. If any of the above is not present then returns null, aborts
	 * 3. If exec is not already present then created and saves exec
	 * 4. Checks whether its status is InProgress, Completed, Terminating or OnHold. 
	 * 5. If status is one of #1 then control returns without doing anything
	 * 6. Else status is set to not started
	 * Note - Setting of status happens in a synchronized block with the exec uuid as key 
	 * and data is fetched from mongo and then set after adding status based on some prerequisites 
	 * checked in commonServiceImpl.setMetaStatus   
	 * @param uuid
	 * @param version
	 * @param type
	 * @param execType
	 * @param inputBaseRuleExec
	 * @param refKeyMap
	 * @param datapodList
	 * @param dagExec
	 * @return inputBaseRuleExec
	 * @throws Exception
	 */
	public BaseRuleExec create(String uuid, String version, MetaType type, MetaType execType, BaseRuleExec inputBaseRuleExec, 
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		logger.info("Inside BaseRuleExec.create ");
		List<Status> statusList = null;
		BaseRule baseRule = null;
		if (StringUtils.isBlank(uuid)) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		baseRule = (BaseRule) commonServiceImpl.getOneByUuidAndVersion(uuid, version, type.toString());
		if (baseRule == null || type == null || execType == null) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		MetaIdentifierHolder baseRuleMeta = new MetaIdentifierHolder(new MetaIdentifier(type, baseRule.getUuid(), baseRule.getVersion()));
		if (inputBaseRuleExec == null) {
			inputBaseRuleExec = ruleExecFactory.getRuleExec(execType);
			inputBaseRuleExec.setDependsOn(baseRuleMeta);
			inputBaseRuleExec.setBaseEntity();
			inputBaseRuleExec.setName(baseRule.getName());
			inputBaseRuleExec.setAppInfo(baseRule.getAppInfo());
			synchronized (inputBaseRuleExec.getUuid()) {
				commonServiceImpl.save(execType.toString(), inputBaseRuleExec);
			}
		}
		MetaIdentifier baseruleExecInfo = new MetaIdentifier(execType, inputBaseRuleExec.getUuid(), inputBaseRuleExec.getVersion());
		
		statusList = inputBaseRuleExec.getStatusList();
		if (Helper.getLatestStatus(statusList) != null 
				&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date())) 
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Terminating, new Date()))
						|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
			logger.info(" This process is In Progress or has been completed previously or is Terminating or is On Hold. Hence it cannot be rerun. ");
			return inputBaseRuleExec;
		}
		
		synchronized (inputBaseRuleExec.getUuid()) {
			inputBaseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(inputBaseRuleExec, execType, Status.Stage.NotStarted);
		}
		return inputBaseRuleExec;
	}
	
	public abstract BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec, Mode runMode) throws Exception;
	
	/**
	 * Defines sample execute. Should be overridden if required.
	 * @param uuid
	 * @param version
	 * @param type
	 * @param execType
	 * @param metaExecutor
	 * @param baseRuleExec
	 * @param baseGroupExec
	 * @param taskList
	 * @return BaseRuleExec
	 * @throws Exception
	 */
	public abstract BaseRuleExec execute(String uuid, String version, 
			ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec, BaseRuleGroupExec baseGroupExec, MetaIdentifier datapodKey, List<FutureTask<TaskHolder>> taskList, Mode runMode) throws Exception;
	
	/**
	 * Defines sample execute. Should be overridden if required.
	 * @param uuid
	 * @param version
	 * @param type
	 * @param execType
	 * @param metaExecutor
	 * @param baseRuleExec
	 * @param baseGroupExec
	 * @param taskList
	 * @return BaseRuleExec
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public BaseRuleExec execute(String uuid, String version, MetaType type, MetaType execType, 
			ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec, BaseRuleGroupExec baseGroupExec, MetaIdentifier datapodKey, List<FutureTask<TaskHolder>> taskList, Mode runMode) throws Exception {
		logger.info("Inside BaseRuleExec.execute ");
		BaseRule baseRule = null;
		
		if (StringUtils.isBlank(uuid) || baseRuleExec == null) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		baseRule = (BaseRule) commonServiceImpl.getOneByUuidAndVersion(baseRuleExec.getDependsOn().getRef().getUuid(), baseRuleExec.getDependsOn().getRef().getVersion(), type.toString());
		if (baseRule == null || type == null || execType == null) {
			logger.info(" Nothing to create exec upon. Aborting ... ");
			return null;
		}
		/*synchronized (baseRuleExec.getUuid()) {
			baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, execType, Status.Stage.InProgress);
		}*/
		logger.info(" After status set to InProgress for baseRuleExec : " + baseRuleExec.getUuid());
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();	// Check and remove

		/*// get datapod from baseRuleExec
		if(datapodKey == null && (baseRuleExec.getRefKeyList() != null)) {
			for(MetaIdentifier mi : baseRuleExec.getRefKeyList()) {
				if(mi.getType().equals(MetaType.datapod)) {
					datapodKey = mi;
					break;
				}
			}
		}*/
		
		RunBaseRuleService runBaseRuleService = runBaseFactory.getRuleService(type);	// Get this from a factory
		runBaseRuleService.setDataStoreServiceImpl(dataStoreServiceImpl);
		runBaseRuleService.setHdfsInfo(hdfsInfo);
		runBaseRuleService.setBaseRule(baseRule);
		runBaseRuleService.setBaseGroupExec(baseGroupExec);
		runBaseRuleService.setExecFactory(execFactory);
		runBaseRuleService.setAuthentication(authentication);
		runBaseRuleService.setBaseRuleExec(baseRuleExec);
		runBaseRuleService.setCommonServiceImpl(commonServiceImpl);
		runBaseRuleService.setName(execType+"_"+baseRuleExec.getUuid()+"_"+baseRuleExec.getVersion());
		runBaseRuleService.setDatapodKey(datapodKey);
		runBaseRuleService.setRuleExecType(execType);
		runBaseRuleService.setSessionContext(sessionHelper.getSessionContext());
		runBaseRuleService.setRunMode(runMode);
		runBaseRuleService.setDatapodRegister(datapodRegister);
		runBaseRuleService.setConnFactory(connFactory);
		runBaseRuleService.setEngine(engine);
		runBaseRuleService.setHelper(helper);

		if (metaExecutor == null) {
			runBaseRuleService.execute();
		} else {
			FutureTask<TaskHolder> futureTask = new FutureTask<TaskHolder>(runBaseRuleService);
			metaExecutor.execute(futureTask);
			taskList.add(futureTask);
			taskThreadMap.put(execType+"_"+baseRuleExec.getUuid()+"_"+baseRuleExec.getVersion(), futureTask);
			logger.info(" taskThreadMap while creating baseRule : " + taskThreadMap);
		}
		return baseRuleExec;
	}

}
