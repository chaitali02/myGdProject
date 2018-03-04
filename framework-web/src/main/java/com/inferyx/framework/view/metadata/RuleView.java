package com.inferyx.framework.view.metadata;

import java.util.List;

import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifierHolder;

public class RuleView extends BaseEntity {
	
	private List<AttributeSource> attributeInfo;	
	private Filter filter;
	private MetaIdentifierHolder source; //It will be a ref of either datapod or dataset
	private String filterChg;
	private MetaIdentifierHolder paramList;
	
	public List<AttributeSource> getAttributeInfo() {
		return attributeInfo;
	}
	public void setAttributeInfo(List<AttributeSource> attributeInfo) {
		this.attributeInfo = attributeInfo;
	}
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	public MetaIdentifierHolder getSource() {
		return source;
	}
	public void setSource(MetaIdentifierHolder source) {
		this.source = source;
	}
	public String getFilterChg() {
		return filterChg;
	}
	public void setFilterChg(String filterChg) {
		this.filterChg = filterChg;
	}
	public MetaIdentifierHolder getParamList() {
		return paramList;
	}
	public void setParamList(MetaIdentifierHolder paramList) {
		this.paramList = paramList;
	}
	
}
