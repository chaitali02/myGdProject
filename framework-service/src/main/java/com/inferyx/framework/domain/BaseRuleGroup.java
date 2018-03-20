/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.List;

/**
 * @author joy
 *
 */
public class BaseRuleGroup extends BaseEntity {
	
	private List<MetaIdentifierHolder> ruleInfo;
	private String inParallel;

	/**
	 * 
	 */
	public BaseRuleGroup() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the ruleInfo
	 */
	public List<MetaIdentifierHolder> getRuleInfo() {
		return ruleInfo;
	}

	/**
	 * @param ruleInfo the ruleInfo to set
	 */
	public void setRuleInfo(List<MetaIdentifierHolder> ruleInfo) {
		this.ruleInfo = ruleInfo;
	}

	/**
	 * @return the inParallel
	 */
	public String getInParallel() {
		return inParallel;
	}

	/**
	 * @param inParallel the inParallel to set
	 */
	public void setInParallel(String inParallel) {
		this.inParallel = inParallel;
	}

}
