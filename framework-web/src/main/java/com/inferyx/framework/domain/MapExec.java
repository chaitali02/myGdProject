package com.inferyx.framework.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mapexec")
public class MapExec extends BaseEntity {

		private List<Status> statusList;
		MetaIdentifierHolder dependsOn;		
		private ExecParams execParams;	
		private List<MetaIdentifier> refKeyList;
		private String exec;
		private MetaIdentifierHolder result;	//dataStore info
		
		public MapExec() {
			refKeyList = new ArrayList<>();
			statusList = new ArrayList<>();
		}
		
		public MetaIdentifierHolder getDependsOn() {
			return dependsOn;
		}
		public void setDependsOn(MetaIdentifierHolder dependsOn) {
			this.dependsOn = dependsOn;
		}		
		/**
		 * @return the statusList
		 */
		public List<Status> getStatusList() {
			return statusList;
		}
		/**
		 * @param statusList the statusList to set
		 */
		public void setStatusList(List<Status> statusList) {
			this.statusList = statusList;
		}
		public ExecParams getExecParams() {
			return execParams;
		}
		public void setExecParams(ExecParams execParams) {
			this.execParams = execParams;
		}
		public String getExec() {
			return exec;
		}
		public void setExec(String exec) {
			this.exec = exec;
		}
		public MetaIdentifierHolder getResult() {
			return result;
		}
		public void setResult(MetaIdentifierHolder result) {
			this.result = result;
		}
		public List<MetaIdentifier> getRefKeyList() {
			return refKeyList;
		}
		public void setRefKeyList(List<MetaIdentifier> refKeyList) {
			this.refKeyList = refKeyList;
		}
			
}
