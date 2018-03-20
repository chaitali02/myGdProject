/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.view.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.domain.Condition;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.Vizpod;
import com.inferyx.framework.service.ConditionServiceImpl;
import com.inferyx.framework.service.DagServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.ExpressionServiceImpl;
import com.inferyx.framework.service.FilterServiceImpl;
import com.inferyx.framework.service.FormulaServiceImpl;
//import com.inferyx.framework.service.GroupServiceImpl;
import com.inferyx.framework.service.MapServiceImpl;
import com.inferyx.framework.service.RelationServiceImpl;
import com.inferyx.framework.service.SessionServiceImpl;
import com.inferyx.framework.service.VizpodServiceImpl;
@Service
public class RefParser {
	
	@Autowired
    private ExpressionServiceImpl expressionServiceImpl;
	@Autowired
  /*  private GroupServiceImpl groupServiceImpl;
	@Autowired*/
    private RelationServiceImpl relationServiceImpl;
	@Autowired
    private FormulaServiceImpl formulaServiceImpl;
    @Autowired
    private FilterServiceImpl filterServiceImpl;
    @Autowired
    private DagServiceImpl dagServiceImpl;
    @Autowired
    private VizpodServiceImpl vizpodServiceImpl;
    @Autowired
    private DataStoreServiceImpl datastoreServiceImpl;
    @Autowired
    private ConditionServiceImpl conditionServiceImpl;
    @Autowired
    private SessionServiceImpl sessionServiceImpl;
    @Autowired
    private MapServiceImpl mapServiceImpl;
	/*public Object resolveName(Object document, String type) {
	
		Object obj = null;
		if(type != null && !type.isEmpty()){
			 ObjectMapper mapper = new ObjectMapper();  
          
		    if(type.equalsIgnoreCase(MetaType.relation.toString())){
		    	 Relation relation = mapper.convertValue(document, Relation.class);
		    	 obj = relationServiceImpl.resolveName(relation);
	        }
            if(type.equalsIgnoreCase(MetaType.filter.toString())){
            	Filter filter = mapper.convertValue(document, Filter.class);
            	obj = filterServiceImpl.resolveName(filter);
            }
            if(type.equalsIgnoreCase(MetaType.expression.toString())){
            	Expression exp = mapper.convertValue(document, Expression.class);
            	obj = expressionServiceImpl.resolveName(exp);
            	
            }
            if(type.equalsIgnoreCase("group")){
            	Group group = mapper.convertValue(document, Group.class);
            	obj = groupServiceImpl.resolveName(group);
            }
            if(type.equalsIgnoreCase(MetaType.formula.toString())){
            	Formula formula = mapper.convertValue(document, Formula.class);
            	obj = formulaServiceImpl.resolveName(formula);
            }
            if(type.equalsIgnoreCase(MetaType.dag.toString())){
            	Dag dag = mapper.convertValue(document, Dag.class);
            	obj = dagServiceImpl.resolveName(dag);
            }
            if(type.equalsIgnoreCase(MetaType.vizpod.toString())){
            	Vizpod vizpod = mapper.convertValue(document, Vizpod.class);
            	obj = vizpodServiceImpl.resolveName(vizpod);
            }
            if(type.equalsIgnoreCase(MetaType.datastore.toString())){
            	DataStore datastore = mapper.convertValue(document, DataStore.class);
            	obj = datastoreServiceImpl.resolveName(datastore);
            }
            if(type.equalsIgnoreCase(MetaType.condition.toString())){
            	Condition condition = mapper.convertValue(document, Condition.class);
            	obj = conditionServiceImpl.resolveName(condition);
            }
            if(type.equalsIgnoreCase(MetaType.map.toString())){
            	Map map = mapper.convertValue(document, Map.class);
            	obj = mapServiceImpl.resolveName(map);
            }
            if(type.equalsIgnoreCase(MetaType.session.toString())){
            	Session session = mapper.convertValue(document, Session.class);
            	obj = sessionServiceImpl.resolveName(session);
            }
        }
		return obj;
   }*/
}