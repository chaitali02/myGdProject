/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vizpod")
public class Vizpod extends BaseEntity {
	private String title;
	private MetaIdentifierHolder source;
	private String type;
	private int limit;
	private List<AttributeDetails> keys;
	private List<AttributeDetails> dimension;
	private List<AttributeDetails> detailAttr;
	private List<AttributeRefHolder> filterInfo;
//	@Autowired
//	private static Datapod datapod;
	private List<AttributeDetails> groups;
	private List<AttributeDetails> values;
	private List<AttributeDetails> sortBy;
    private String sortOrder;
    private String colorPalette;

	public List<AttributeDetails> getDetailAttr() {
		return detailAttr;
	}

	public void setDetailAttr(List<AttributeDetails> detailAttr) {
		this.detailAttr = detailAttr;
	}

	public List<AttributeDetails> getDimension() {
		return dimension;
	}

	public void setDimension(List<AttributeDetails> dimension) {
		this.dimension = dimension;
	}
	
	public List<AttributeRefHolder> getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(List<AttributeRefHolder> filterInfo) {
		this.filterInfo = filterInfo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public List<AttributeDetails> getGroups() {
		return groups;
	}

	public void setGroups(List<AttributeDetails> groups) {
		this.groups = groups;
	}

	public List<AttributeDetails> getValues() {
		return values;
	}

	public void setValues(List<AttributeDetails> values) {
		this.values = values;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<AttributeDetails> getKeys() {
		return keys;
	}

	public MetaIdentifierHolder getSource() {
		return source;
	}

	public void setSource(MetaIdentifierHolder source) {
		this.source = source;
	}

	public void setKeys(List<AttributeDetails> keys) {
		this.keys = keys;
	}
	
	public static class AttributeDetails {
		private MetaIdentifier ref;
		private Integer attributeId;
		private String function;// not required
		private String value;// if type is "simple"
        private String attributeName;
        
		public MetaIdentifier getRef() {
			return ref;
		}
		public String getAttributeName() {
			return attributeName;
		}
		public void setAttributeName(String attributeName) {
			this.attributeName = attributeName;
		}
		public String getFunction() {
			return function;
		}

		public void setFunction(String function) {
			this.function = function;
		}

		public void setRef(MetaIdentifier ref) {
			this.ref = ref;
		}

		public Integer getAttributeId() {
			return attributeId;
		}

		public void setAttributeId(Integer attributeId) {
			this.attributeId = attributeId;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

//		public String sql(MetadataUtil daoRegister) throws JsonProcessingException {
//			String alias = null;
//			if (getRef().getType() == MetaType.datapod) {
//				datapod = (Datapod) daoRegister.getRefObject(getRef());
//				alias = datapod.getAttribute(getAttributeId()).getName();
//				if (getRef() == null) {
//					return datapod.getAttribute(getAttributeId()).getName().toString().concat(" as ").concat(alias)
//							.concat(" ");
//				}
//			}
//			return datapod.getAttribute(getAttributeId()).getName().toString().concat(" as ").concat(alias).concat(" ");
//		}

	}

	public List<AttributeDetails> getSortBy() {
		return sortBy;
	}

	public void setSortBy(List<AttributeDetails> sortBy) {
		this.sortBy = sortBy;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getColorPalette() {
		return colorPalette;
	}

	public void setColorPalette(String colorPalette) {
		this.colorPalette = colorPalette;
	}
}