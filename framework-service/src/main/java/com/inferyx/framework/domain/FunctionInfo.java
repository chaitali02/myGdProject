package com.inferyx.framework.domain;
import java.util.List;

public  class FunctionInfo {
		private String name;
		private String type;
		private List<ParamInfoHolder> paramInfoHolder;

		public List<ParamInfoHolder> getParamInfoHolder() {
			return paramInfoHolder;
		}

		public void setParamInfoHolder(List<ParamInfoHolder> paramInfoHolder) {
			this.paramInfoHolder = paramInfoHolder;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setType(String type) {
			this.type = type;
		}

		

	}