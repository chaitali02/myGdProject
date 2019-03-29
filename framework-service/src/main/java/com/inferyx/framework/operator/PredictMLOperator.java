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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.FeatureAttrMap;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author joy
 *
 */
@Component
public class PredictMLOperator {
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private FormulaOperator formulaOperator;
	
	static final Logger LOGGER = Logger.getLogger(PredictMLOperator.class);
	
	/**
	 * 
	 */
	public PredictMLOperator() {
		// TODO Auto-generated constructor stub
	}

	
	public String generateSql(Predict predict, String tableName) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
		String aliaseName = "";
		builder.append("SELECT ");
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(predict.getDependsOn().getRef().getUuid(),
				predict.getDependsOn().getRef().getVersion(), predict.getDependsOn().getRef().getType().toString());
		MetaIdentifierHolder dependsOn = model.getDependsOn();
		Object object = commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
		if(object instanceof Formula) {
			
			Formula formula = (Formula) object;
			
			List<Feature> modelFeatures = model.getFeatures();
			
			Formula dumyFormula = formula;
			
			List<SourceAttr> formulaInfo = dumyFormula.getFormulaInfo();
			List<FeatureAttrMap> predictFeatures = predict.getFeatureAttrMap();
			
			for(SourceAttr attr : formulaInfo) {
				if(attr.getRef().getType().equals(MetaType.paramlist)) {
					for(FeatureAttrMap featureAttrMap : predictFeatures) {
						boolean flag = false;
						for(Feature feature : modelFeatures) {
							if(featureAttrMap.getFeatureMapId().equalsIgnoreCase(feature.getFeatureId())) {
								ParamListHolder paramListHolder = feature.getParamListInfo();
								if(paramListHolder.getParamId().equals(attr.getAttributeId().toString())) {
									attr.getRef().setUuid(featureAttrMap.getAttribute().getRef().getUuid());
									attr.getRef().setType(featureAttrMap.getAttribute().getRef().getType());
									attr.setAttributeId(Integer.parseInt(featureAttrMap.getAttribute().getAttrId()));
									
									if(attr.getRef().getType().equals(MetaType.datapod)) {
										Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(attr.getRef().getUuid(), attr.getRef().getVersion(), attr.getRef().getType().toString());
										aliaseName = datapod.getName();
										String attrName = datapod.getAttributes().get(attr.getAttributeId()).getName();
										builder.append(attrName).append(" AS ").append(feature.getName()).append(", ");
									}
									
									flag = true;
									break;
								}
							}
						}
						if(flag)
							break;
					}
				}
			}
			
//			String label = commonServiceImpl.resolveLabel(predict.getLabelInfo());
			Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(predict);
			builder.append(formulaOperator.generateSql(dumyFormula, null, null, null, mapSourceDS)).append(" AS ").append("label");
			builder.append(" FROM ");
			builder.append(tableName).append(" ").append(aliaseName);

			LOGGER.info("query : "+builder);
		}
		return builder.toString();
	}
}
