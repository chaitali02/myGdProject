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
    private String name;
    private String type;
    private String desc;
    private Expression expression;
    private String key;
    private String partition;
    private String dispName;
	private String active = "Y";
    
	

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
     *     The expr
     */
    public Expression getExpr() {
        return expression;
    }

    /**
     * 
     * @param expression
     *     The expr
     */
    public void setExpr(Expression expression) {
        this.expression = expression;
    }

    
}
