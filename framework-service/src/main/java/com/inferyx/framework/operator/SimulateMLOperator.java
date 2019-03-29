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
package com.inferyx.framework.operator;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class SimulateMLOperator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7583225899746066755L;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private FormulaOperator formulaOperator;
	
	static final Logger LOGGER = Logger.getLogger(SimulateMLOperator.class);
	
	/**
	 * 
	 */
	public SimulateMLOperator() {
		// TODO Auto-generated constructor stub
	}
	
	public String generateSql(Simulate simulate, String tableName) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(simulate.getDependsOn().getRef().getUuid(), simulate.getDependsOn().getRef().getVersion(), simulate.getDependsOn().getRef().getType().toString());
		StringBuilder builder = new StringBuilder();
		String aliaseName = "";
		builder.append("SELECT ");
		MetaIdentifierHolder dependsOn = model.getDependsOn();
		if(dependsOn.getRef().getType().equals(MetaType.formula)) {
			Formula formula = (Formula) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
			int i = 0;
			for (Feature feature : model.getFeatures()) {
				builder.append(feature.getName()).append(" AS ").append(feature.getName()).append(", ");
				i++;
			}
			
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			builder.append(formulaOperator.generateSql(formula, null, null, null, datasource)).append(" AS ").append(model.getLabel());
			builder.append(" FROM ");
			builder.append(tableName).append(" ").append(aliaseName);
			
			LOGGER.info("query : "+builder);
			return builder.toString();
		} else if(dependsOn.getRef().getType().equals(MetaType.algorithm)) { //used to generate query for algorithm without distribution
			StringBuilder sb = new StringBuilder();
			for (Feature feature : model.getFeatures()) {
				sb.append("(" + feature.getMinVal() + " + rand()*(" + feature.getMaxVal() + "-" + feature.getMinVal()
						+ ")) AS " + feature.getName() + ", ");
			}
			
			String query = "SELECT id, " + sb.toString().substring(0, sb.toString().length() - 2) + " FROM " + tableName;
			LOGGER.info("query : "+query);
			return query;
		}
		
		return null;
	}
	
}
