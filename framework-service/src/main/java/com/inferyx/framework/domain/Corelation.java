package com.inferyx.framework.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.cloudera.org.codehaus.jackson.annotate.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "corelation")
public class Corelation extends BaseEntity {

	private MetaIdentifierHolder source;
	private List<AttributeRefHolder> listAttributes;

	public MetaIdentifierHolder getSource() {
		return source;
	}

	public void setSource(MetaIdentifierHolder source) {
		this.source = source;
	}

	public List<AttributeRefHolder> getListAttributes() {
		return listAttributes;
	}

	public void setListAttributes(List<AttributeRefHolder> listAttributes) {
		this.listAttributes = listAttributes;
	}

	

}
