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

public class Attribute {
	
	private Integer attributeId;
	private Integer displaySeq;
    private String name;
    private String type;
    private String desc;
    private Expression expression;
    private String key;
    private String partition;
    private String dispName;
	private String active = "Y";
	private Integer length;
	private String attrUnitType;
    private MetaIdentifierHolder domain;
    private String piiFlag = "N";
    private String cdeFlag = "N";
	
	public String getPiiFlag() {
		return piiFlag;
	}

	public void setPiiFlag(String piiFlag) {
		this.piiFlag = piiFlag;
	}

	public String getCdeFlag() {
		return cdeFlag;
	}

	public void setCdeFlag(String cdeFlag) {
		this.cdeFlag = cdeFlag;
	}

	public MetaIdentifierHolder getDomain() {
		return domain;
	}

	public void setDomain(MetaIdentifierHolder domain) {
		this.domain = domain;
	}

	/**
	 *
	 * @Vaibhav
	 *
	 * @return the attrUnitType
	 */
	public String getAttrUnitType() {
		return attrUnitType;
	}

	/**
	 *
	 * @Vaibhav
	 *
	 * @param attrUnitType the attrUnitType to set
	 */
	public void setAttrUnitType(String attrUnitType) {
		this.attrUnitType = attrUnitType;
	}

	/**
	 * @Ganesh
	 *
	 * @return the length
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * @Ganesh
	 *
	 * @param length the length to set
	 */
	public void setLength(Integer length) {
		this.length = length;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getDispName() {
		return dispName;
	}

	public void setDispName(String dispName) {
		this.dispName = dispName;
	}	
	
    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}

	/**
     * 
     * @return
     *     The id
     */
    public Integer getAttributeId() {
        return attributeId;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 
     * @param desc
     *     The desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 
     * @return
     *     The expression
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * 
     * @param expression
     *     The expression
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }

	public Integer getDisplaySeq() {
		return displaySeq;
	}

	public void setDisplaySeq(Integer displaySeq) {
		this.displaySeq = displaySeq;
	}
    
   

    
}
