package com.inferyx.framework.domain;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="dq")
public class DataQual extends BaseRule{
	private MetaIdentifierHolder target;	
	private AttributeRefHolder attribute;	
	private MetaIdentifierHolder dependsOn;	
	private String duplicateKeyCheck;
	private String nullCheck;
	private List<String> valueCheck;
	private Map<String, String> rangeCheck;
	private String dataTypeCheck;
	private String dateFormatCheck;
	private String customFormatCheck;
	private Map<String, Long> lengthCheck;
	private AttributeRefHolder refIntegrityCheck;	
	//private String stdDevCheck;
	private List<AttributeRefHolder> filterInfo;
	private List<MetaIdentifierHolder> userInfo;
	
	
	public List<MetaIdentifierHolder> getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(List<MetaIdentifierHolder> userInfo) {
		this.userInfo = userInfo;
	}
	public MetaIdentifierHolder getTarget() {
		return target;
	}
	public void setTarget(MetaIdentifierHolder target) {
		this.target = target;
	}
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
	public List<AttributeRefHolder> getFilterInfo() {
		return filterInfo;
	}
	public void setFilterInfo(List<AttributeRefHolder> filterInfo) {
		this.filterInfo = filterInfo;
	}
	public AttributeRefHolder getRefIntegrityCheck() {
		return refIntegrityCheck;
	}
	public void setRefIntegrityCheck(AttributeRefHolder refIntegrityCheck) {
		this.refIntegrityCheck = refIntegrityCheck;
	}	
	public AttributeRefHolder getAttribute() {
		return attribute;
	}
	public void setAttribute(AttributeRefHolder attribute) {
		this.attribute = attribute;
	}	
	
	public String getDuplicateKeyCheck() {
		return duplicateKeyCheck;
	}
	public void setDuplicateKeyCheck(String duplicateKeyCheck) {
		this.duplicateKeyCheck = duplicateKeyCheck;
	}
	public String getNullCheck() {
		return nullCheck;
	}
	public void setNullCheck(String nullCheck) {
		this.nullCheck = nullCheck;
	}
	public List<String> getValueCheck() {
		return valueCheck;
	}
	public void setValueCheck(List<String> valueCheck) {
		this.valueCheck = valueCheck;
	}
	public Map<String, String> getRangeCheck() {
		return rangeCheck;
	}
	public void setRangeCheck(Map<String, String> rangeCheck) {
		this.rangeCheck = rangeCheck;
	}
	public String getDataTypeCheck() {
		return dataTypeCheck;
	}
	public void setDataTypeCheck(String dataTypeCheck) {
		this.dataTypeCheck = dataTypeCheck;
	}
	public String getDateFormatCheck() {
		return dateFormatCheck;
	}
	public void setDateFormatCheck(String dateFormatCheck) {
		this.dateFormatCheck = dateFormatCheck;
	}
	public String getCustomFormatCheck() {
		return customFormatCheck;
	}
	public void setCustomFormatCheck(String customFormatCheck) {
		this.customFormatCheck = customFormatCheck;
	}
	public Map<String, Long> getLengthCheck() {
		return lengthCheck;
	}
	public void setLengthCheck(Map<String, Long> lengthCheck) {
		this.lengthCheck = lengthCheck;
	}	
	/*public String getStdDevCheck() {
		return stdDevCheck;
	}
	public void setStdDevCheck(String stdDevCheck) {
		this.stdDevCheck = stdDevCheck;
	}*/
		
	
}
