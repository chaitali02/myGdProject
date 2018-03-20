package com.inferyx.framework.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="application")
public class Application extends BaseEntity{
	private MetaIdentifierHolder dataSource;

	public MetaIdentifierHolder getDataSource() {
		return dataSource;
	}

	public void setDataSource(MetaIdentifierHolder dataSource) {
		this.dataSource = dataSource;
	}
}
