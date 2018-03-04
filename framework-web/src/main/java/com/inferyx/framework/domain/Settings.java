package com.inferyx.framework.domain;

import java.util.List;

public class Settings {
	private List<Property> generalSetting;
	private List<Property> metaEngine;
	private List<Property> ruleEngine;
	
	public List<Property> getGeneralSetting() {
		return generalSetting;
	}
	public void setGeneralSetting(List<Property> generalSetting) {
		this.generalSetting = generalSetting;
	}
	public List<Property> getMetaEngine() {
		return metaEngine;
	}
	public void setMetaEngine(List<Property> metaEngine) {
		this.metaEngine = metaEngine;
	}
	public List<Property> getRuleEngine() {
		return ruleEngine;
	}
	public void setRuleEngine(List<Property> ruleEngine) {
		this.ruleEngine = ruleEngine;
	}

}
