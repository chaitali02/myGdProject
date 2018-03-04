/**
 * 
 */
package com.inferyx.framework.factory;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.service.RunBaseGroupService;
import com.inferyx.framework.service.RunBaseRuleService;
import com.inferyx.framework.service.RunDataQualGroupServiceImpl;
import com.inferyx.framework.service.RunDataQualServiceImpl;
import com.inferyx.framework.service.RunProfileGroupServiceImpl;
import com.inferyx.framework.service.RunProfileServiceImpl;
import com.inferyx.framework.service.RunRuleGroupServiceImpl;
import com.inferyx.framework.service.RunRuleServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class RunRuleFactory {
	
	static final Logger logger = Logger.getLogger(RunRuleFactory.class);

	/**
	 * 
	 */
	public RunRuleFactory() {
	}
	
	public static RunBaseRuleService getRuleService(MetaType type) {
		if (type == null) {
			logger.info("Returning RunBaseRuleService");
			return new RunBaseRuleService();
		} 
		if (type == MetaType.rule) {
			logger.info("Returning RunRuleServiceImpl");
			return new RunRuleServiceImpl();
		}
		if (type == MetaType.dq) {
			logger.info("Returning RunDataQualServiceImpl");
			return new RunDataQualServiceImpl();
		}
		if (type == MetaType.profile) {
			logger.info("Returning RunProfileServiceImpl");
			return new RunProfileServiceImpl();
		}
		return new RunBaseRuleService();
	}

	public static RunBaseGroupService getGroupService(MetaType type) {
		if (type == null) {
			logger.info("Returning RunBaseGroupService");
			return new RunBaseGroupService();
		} 
		if (type == MetaType.rulegroup) {
			logger.info("Returning RunRuleGroupServiceImpl");
			return new RunRuleGroupServiceImpl();
		}
		if (type == MetaType.dqgroup) {
			logger.info("Returning RunDataQualGroupServiceImpl");
			return new RunDataQualGroupServiceImpl();
		}
		if (type == MetaType.profilegroup) {
			logger.info("Returning RunProfileGroupServiceImpl");
			return new RunProfileGroupServiceImpl();
		}
		logger.info("Returning RunBaseGroupService");
		return new RunBaseGroupService();
	}

}
