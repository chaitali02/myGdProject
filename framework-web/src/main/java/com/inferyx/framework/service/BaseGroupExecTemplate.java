/**
 * 
 */
package com.inferyx.framework.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.factory.RuleServiceFactory;

/**
 * @author joy
 *
 */
public class BaseGroupExecTemplate {

	@Autowired
	protected CommonServiceImpl<?> commonServiceImpl;
	@Resource(name="taskThreadMap")
	protected ConcurrentHashMap taskThreadMap;
	@Autowired
	private RuleServiceFactory serviceFactory;
	
	static final Logger logger = Logger.getLogger(BaseGroupExecTemplate.class);
	
	/**
	 * 
	 */
	public BaseGroupExecTemplate() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Kill group
	 * @param uuid
	 * @param version
	 */
	public void kill (String uuid, String version, MetaType groupExecType, MetaType ruleExecType) {
		boolean killComplete = false;
		BaseRuleExec baseRuleExec = null;
		BaseRuleExecTemplate baseRuleExecService = serviceFactory.getRuleExecService(ruleExecType);
		BaseRuleGroupExec baseGroupExec = null;
		try {
			baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, groupExecType.toString());
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (baseGroupExec == null) {
			logger.info("BaseGroupExec not found. Exiting...");
			return;
		}
		
		if (!Helper.getLatestStatus(baseGroupExec.getStatusList()).equals(new Status(Status.Stage.InProgress, new Date()))) {
			logger.info("Latest Status is not in InProgress. Exiting...");
			return;			
		}
		try {
			synchronized (baseGroupExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType, Status.Stage.Terminating);
			}
			
			FutureTask futureTask = (FutureTask) taskThreadMap.get(groupExecType+"_"+baseGroupExec.getUuid()+"_"+baseGroupExec.getVersion());
			try {
				if (futureTask != null && !futureTask.isDone() && !futureTask.isCancelled()) {
					futureTask.cancel(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			taskThreadMap.remove(groupExecType+"_"+baseGroupExec.getUuid()+"_"+baseGroupExec.getVersion());
			
			while (!killComplete) {
				killComplete = true;
				for (MetaIdentifierHolder ruleExecHolder : baseGroupExec.getExecList()) {
					baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecHolder.getRef().getUuid(), ruleExecHolder.getRef().getVersion(), ruleExecType.toString());
					if (baseRuleExec == null) {
						continue;
					}
					baseRuleExecService.kill (baseRuleExec.getUuid(), baseRuleExec.getVersion(), ruleExecType);
				}
				logger.info("Check whether all rules were processed ");
				// Check whether kill is complete
				for (MetaIdentifierHolder ruleExecHolder : baseGroupExec.getExecList()) {
					baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecHolder.getRef().getUuid(), ruleExecHolder.getRef().getVersion(), ruleExecType.toString());
					if (baseRuleExec == null) {
						continue;
					}
					List<Status> statusList = baseRuleExec.getStatusList();
					Status latestStatus = Helper.getLatestStatus(statusList);
					if (!latestStatus.getStage().equals(Status.Stage.Completed) 
							&& !latestStatus.getStage().equals(Status.Stage.Killed) 
							&& !latestStatus.getStage().equals(Status.Stage.Failed) 
							&& !latestStatus.getStage().equals(Status.Stage.NotStarted)) {
						killComplete = false;
						Thread.sleep(5000);
						break;
					}
				}
			}	// While Not killComplete
			logger.info("Rules kill completed >>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
			
			baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(baseGroupExec.getUuid(), baseGroupExec.getVersion(), groupExecType.toString());
			Status.Stage latestStatus = Helper.getLatestStatus(baseGroupExec.getStatusList()).getStage();
			Status status=commonServiceImpl.getGroupStatus(baseGroupExec,groupExecType, ruleExecType);
			synchronized (baseGroupExec.getUuid()) {
				if(!Helper.getLatestStatus(baseGroupExec.getStatusList()).equals(status.getStage())){
					baseGroupExec = (BaseRuleGroupExec) commonServiceImpl.setMetaStatus(baseGroupExec, groupExecType,status.getStage());
				}
			}
			/*synchronized (ruleGroupExec.getUuid()) {
				commonServiceImpl.setMetaStatus(ruleGroupExec, MetaType.rulegroupExec, Status.Stage.Killed);
			}*/
		} catch (Exception e) {
			logger.error("Exception while killing rule group >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
			e.printStackTrace();
		}
	}
}
