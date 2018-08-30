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
package com.inferyx.framework.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.dao.IFunctionDao;
import com.inferyx.framework.domain.Activity;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BatchExec;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DagStatusHolder;
import com.inferyx.framework.domain.Dashboard;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroup;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.DownloadExec;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.GraphExec;
import com.inferyx.framework.domain.Group;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaStatsHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamSet;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.Privilege;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroup;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.Registry;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.domain.ReportView;
import com.inferyx.framework.domain.Role;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.RuleGroup;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.SimulateExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.domain.UploadExec;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.domain.VizExec;
import com.inferyx.framework.domain.Vizpod;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.register.CSVRegister;
import com.inferyx.framework.register.HiveRegister;
import com.inferyx.framework.register.MySqlRegister;
import com.inferyx.framework.register.OracleRegister;
import com.inferyx.framework.register.PostGresRegister;
import com.inferyx.framework.view.metadata.DQView;
import com.inferyx.framework.view.metadata.DashboardView;
import com.inferyx.framework.view.metadata.DatasetView;
import com.inferyx.framework.view.metadata.ReconView;
import com.inferyx.framework.view.metadata.RuleView;

@Service
public class RegisterService {

	static final Logger logger = Logger.getLogger(RegisterService.class);
	@Autowired
	private ModelExecServiceImpl modelExecServiceImpl;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	@Autowired
	private RelationServiceImpl relationServiceImpl;
	@Autowired
	private FormulaServiceImpl formulaServiceImpl;
	@Autowired
	private DagServiceImpl dagServiceImpl;
	@Autowired
	private ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	private DagExecServiceImpl dagExecServiceImpl;
	@Autowired
	private VizpodServiceImpl vizpodServiceImpl;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	/*
	 * @Autowired private GroupServiceImpl groupServiceImpl;
	 */
	@Autowired
	private UserServiceImpl userServiceImpl;
	@Autowired
	private SessionServiceImpl sessionServiceImpl;
	@Autowired
	private ActivityServiceImpl activityServiceImpl;
	@Autowired
	private DatasetServiceImpl datasetServiceImpl;
	@Autowired
	private DatasetViewServiceImpl datasetViewServiceImpl;
	@Autowired
	private DashboardViewServiceImpl dashboardViewServiceImpl;
	@Autowired
	private DataQualServiceImpl dataQualServiceImpl;
	@Autowired
	private DataQualExecServiceImpl dataQualExecServiceImpl;
	@Autowired
	private DataQualGroupExecServiceImpl dataQualGroupExecServiceImpl;
	@Autowired
	private DQViewServiceImpl dqViewServiceImpl;
	@Autowired
	private RuleServiceImpl ruleServiceImpl;
	@Autowired
	private RuleViewServiceImpl ruleViewServiceImpl;
	@Autowired
	private RuleExecServiceImpl ruleExecServiceImpl;
	@Autowired
	private RuleGroupExecServiceImpl ruleGroupExecServiceImpl;
	@Autowired
	private SecurityServiceImpl securityServiceImpl;
	@Autowired
	private CSVRegister csvRegister;
	@Autowired
	private HiveRegister hiveRegister;
	@Autowired
	private MySqlRegister mysqlRegister;
	@Autowired
	private OracleRegister oracleRegister;
	@Autowired
	private PostGresRegister postGresRegister;
	@Autowired
	private LoadExecServiceImpl loadExecServiceImpl;
	@Autowired
	private FunctionServiceImpl functionServiceImpl;
	@Autowired
	private IFunctionDao iFunctionDao;
	@Autowired
	private MapExecServiceImpl mapExecServiceImpl;
	@Autowired
	private ProfileExecServiceImpl profileExecServiceImpl;
	@Autowired
	private ProfileGroupExecServiceImpl profileGroupExecServiceImpl;
	@Autowired
	private ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	ConnectionFactory connectionFactory;
	@Autowired
	protected ExecutorFactory execFactory;
    @Autowired
	private CommonServiceImpl<?> commonServiceImpl;
    @Autowired
    private MongoGraphServiceImpl mongoGraphServiceImpl;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private ReconViewServiceImpl reconViewServiceImpl;
	@Autowired
	private ReconServiceImpl reconServiceImpl;
	@Autowired
	private ReconGroupServiceImpl reconGroupServiceImpl;
	@Autowired
	private ReconExecServiceImpl reconExecServiceImpl;
	@Autowired
	private ReportViewServiceImpl reportViewServiceImpl;

	List<String> createDet = new ArrayList<String>();
	List<String> datapodResult = new ArrayList<String>();

	/********************** UNUSED **********************/
	/*public String getAllLatest(String type, String inputFlag) throws JsonProcessingException {
		logger.debug("getAllLatest() method start point ");
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {

			type = type.toLowerCase();
			switch (type) {
			case "datapod":
				List<Datapod> datapodList = datapodServiceImpl.findAllLatest();
				result = ow.writeValueAsString(datapodServiceImpl.resolveName(datapodList));
				break;
			case "relation":
				List<Relation> relationList = relationServiceImpl.findAllLatest();
				result = ow.writeValueAsString(relationServiceImpl.resolveName(relationList));
				break;
			case "dag":
				List<Dag> dagList = dagServiceImpl.findAllLatest();
				result = ow.writeValueAsString(dagServiceImpl.resolveName(dagList));
				break;
			case "filter":
				List<Filter> filterList = filterServiceImpl.findAllLatest();
				result = ow.writeValueAsString(filterServiceImpl.resolveName(filterList));
				break;
			case "expression":
				List<Expression> expressionList = expressionServiceImpl.findAllLatest();
				result = ow.writeValueAsString(expressionServiceImpl.resolveName(expressionList));
				break;
			case "formula":
				List<Formula> formulaList = formulaServiceImpl.findAllLatest();
				result = ow.writeValueAsString(formulaServiceImpl.resolveName(formulaList));
				break;
			case "map":
				List<com.inferyx.framework.domain.Map> mapList = mapServiceImpl.findAllLatest();
				result = ow.writeValueAsString(mapServiceImpl.resolveName(mapList));
				break;
			case "condition":
				List<Condition> conditionList = conditionServiceImpl.findAllLatest();
				result = ow.writeValueAsString(conditionServiceImpl.resolveName(conditionList));
				break;
			case "dagexec":
				List<DagExec> dagExecList = dagExecServiceImpl.findAllLatest();
				result = ow.writeValueAsString(dagExecServiceImpl.resolveName(dagExecList));
				break;
			case "meta":
				result = ow.writeValueAsString(metadataServiceImpl.findAllLatest());
				break;
			case "vizpod":
				List<Vizpod> vizpodList = vizpodServiceImpl.findAllLatest();
				result = ow.writeValueAsString(vizpodServiceImpl.resolveName(vizpodList));
				break;
			case "datastore":
				List<DataStore> datastoreList = dataStoreServiceImpl.findAllLatest();
				result = ow.writeValueAsString(dataStoreServiceImpl.resolveName(datastoreList));
				break;
			
			 * case "group": List<Group> groupList = groupServiceImpl.findAllLatest();
			 * result = ow.writeValueAsString(groupServiceImpl.resolveName(groupList));
			 * break;
			 
			case "load":
				List<Load> loadList = loadServiceImpl.findAllLatest();
				result = ow.writeValueAsString(loadServiceImpl.resolveName(loadList));
				break;
			case "user":
				List<User> userList = userServiceImpl.findAllLatest();
				result = ow.writeValueAsString(userServiceImpl.resolveName(userList));
				break;
			case "session":
				List<Session> sessionList = sessionServiceImpl.findAllLatest();
				result = ow.writeValueAsString(sessionServiceImpl.resolveName(sessionList));
				break;
			case "activity":
				List<Activity> activityList = activityServiceImpl.findAllLatest();
				result = ow.writeValueAsString(activityServiceImpl.resolveName(activityList));
				break;
			case "role":
				List<Role> roleList = roleServiceImpl.findAllLatest();
				result = ow.writeValueAsString(roleServiceImpl.resolveName(roleList));
				break;
			case "group":
				List<Group> groupList = groupServiceImpl.findAllLatest();
				result = ow.writeValueAsString(groupServiceImpl.resolveName(groupList));
				break;
			case "privilege":
				List<Privilege> privilegeList = privilegeServiceImpl.findAllLatest();
				result = ow.writeValueAsString(privilegeServiceImpl.resolveName(privilegeList));
				break;
			case "dimension":
				List<Dimension> dimensionList = dimensionServiceImpl.findAllLatest();
				result = ow.writeValueAsString(dimensionServiceImpl.resolveName(dimensionList));
				break;
			case "measure":
				List<Measure> measureList = measureServiceImpl.findAllLatest();
				result = ow.writeValueAsString(measureServiceImpl.resolveName(measureList));
				break;
			case "vizexec":
				List<VizExec> vizExecList = vizExecServiceImpl.findAllLatest();
				result = ow.writeValueAsString(vizExecServiceImpl.resolveName(vizExecList));
				break;
			case "dataset":
				List<Dataset> datasetList = datasetServiceImpl.findAllLatest();
				result = ow.writeValueAsString(datasetServiceImpl.resolveName(datasetList));
				break;
			case "application":
				List<Application> applicationList = applicationServiceImpl.findAllLatest();
				result = ow.writeValueAsString(applicationServiceImpl.resolveName(applicationList));
				break;
			case "datasource":
				List<Datasource> datasourceList = datasourceServiceImpl.findAllLatest();
				result = ow.writeValueAsString(datasourceServiceImpl.resolveName(datasourceList));
				break;
			case "dq":
				List<DataQual> dataQualList = dataQualServiceImpl.findAllLatest();
				result = ow.writeValueAsString(dataQualServiceImpl.resolveName(dataQualList));
				break;
			case "dqgroup":
				List<DataQualGroup> dataQualGroupList = dataQualGroupServiceImpl.findAllLatest();
				result = ow.writeValueAsString(dataQualGroupServiceImpl.resolveName(dataQualGroupList));
				break;
			case "dqExec":
				List<DataQualExec> dataQualExecList = dataQualExecServiceImpl.findAllLatest();
				result = ow.writeValueAsString(dataQualExecServiceImpl.resolveName(dataQualExecList));
				break;
			case "dqgroupexec":
				List<DataQualGroupExec> dataQualGroupExecList = dataQualGroupExecServiceImpl.findAllLatest();
				result = ow.writeValueAsString(dataQualGroupExecServiceImpl.resolveNameGroup(dataQualGroupExecList));
				break;
			case "rule":
				List<Rule> ruleList = ruleServiceImpl.findAllLatest();
				result = ow.writeValueAsString(ruleServiceImpl.resolveName(ruleList));
				break;
			case "ruleexec":
				List<RuleExec> ruleExecList = ruleExecServiceImpl.findAllLatest();
				result = ow.writeValueAsString(ruleExecServiceImpl.resolveName(ruleExecList));
				break;
			case "rulegroup":
				List<RuleGroup> ruleGroupList = ruleGroupServiceImpl.findAllLatest();
				result = ow.writeValueAsString(ruleGroupServiceImpl.resolveName(ruleGroupList));
				break;
			case "rulegroupexec":
				List<RuleGroupExec> ruleGroupExecList = ruleGroupExecServiceImpl.findAllLatest();
				result = ow.writeValueAsString(ruleGroupExecServiceImpl.resolveName(ruleGroupExecList));
				break;
			case "loadexec":
				List<LoadExec> loadExecList = loadExecServiceImpl.findAllLatest();
				result = ow.writeValueAsString(loadExecServiceImpl.resolveName(loadExecList));
				break;
			case "function":
				List<Function> functionList = functionServiceImpl.findAllLatest(inputFlag);
				result = ow.writeValueAsString(functionServiceImpl.resolveName(functionList));
				break;
			case "mapexec":
				List<MapExec> mapexecList = mapExecServiceImpl.findAllLatest();
				result = ow.writeValueAsString(mapExecServiceImpl.resolveName(mapexecList));
				break;
			case "profile":
				List<Profile> profileList = profileServiceImpl.findAllLatest();
				result = ow.writeValueAsString(profileServiceImpl.resolveName(profileList));
				break;
			case "profileexec":
				List<ProfileExec> profileExecList = profileExecServiceImpl.findAllLatest();
				result = ow.writeValueAsString(profileExecServiceImpl.resolveName(profileExecList));
				break;
			case "profilegroup":
				List<ProfileGroup> profileGroupList = profileGroupServiceImpl.findAllLatest();
				result = ow.writeValueAsString(profileGroupServiceImpl.resolveName(profileGroupList));
				break;
			case "profilegroupexec":
				List<ProfileGroupExec> profileGroupExecList = profileGroupExecServiceImpl.findAllLatest();
				result = ow.writeValueAsString(profileGroupExecServiceImpl.resolveName(profileGroupExecList));
				break;
			case "dashboard":
				List<Dashboard> dashboardList = dashboardServiceImpl.findAllLatest();
				result = ow.writeValueAsString(dashboardServiceImpl.resolveName(dashboardList));
				break;
			case "algorithm":
				List<Algorithm> algorithmList = algorithmServiceImpl.findAllLatest();
				result = ow.writeValueAsString(algorithmServiceImpl.resolveName(algorithmList));
				break;
			case "model":
				List<Model> modelList = modelServiceImpl.findAllLatest();
				result = ow.writeValueAsString(modelServiceImpl.resolveName(modelList));
				break;
			case "modelexec":
				List<ModelExec> modelExecList = modelExecServiceImpl.findAllLatest();
				result = ow.writeValueAsString(modelExecServiceImpl.resolveName(modelExecList));
				break;
			case "paramlist":
				List<ParamList> paramList = paramListServiceImpl.findAllLatest();
				result = ow.writeValueAsString(paramListServiceImpl.resolveName(paramList));
				break;
			case "paramset":
				List<ParamSet> paramSet = paramSetServiceImpl.findAllLatest();
				result = ow.writeValueAsString(paramSetServiceImpl.resolveName(paramSet));
				break;
			}
		}
		return result;
	}
*/

	/********************** UNUSED **********************/
	/*public String getAllLatestActive(String type) throws JsonProcessingException {
		logger.debug("getAllLatest() method start point ");
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			case "datapod":
				result = ow.writeValueAsString(datapodServiceImpl.findAllLatestActive());
				break;
			case "relation":
				result = ow.writeValueAsString(relationServiceImpl.findAllLatestActive());
				break;
			case "dag":
				result = ow.writeValueAsString(dagServiceImpl.findAllLatestActive());
				break;
			case "filter":
				result = ow.writeValueAsString(filterServiceImpl.findAllLatestActive());
				break;
			case "expression":
				result = ow.writeValueAsString(expressionServiceImpl.findAllLatestActive());
				break;
			case "formulae":
				result = ow.writeValueAsString(formulaServiceImpl.findAllLatestActive());
				break;
			case "map":
				result = ow.writeValueAsString(mapServiceImpl.findAllLatestActive());
				break;
			case "condition":
				result = ow.writeValueAsString(conditionServiceImpl.findAllLatestActive());
				break;
			case "dagexec":
				result = ow.writeValueAsString(dagExecServiceImpl.findAllLatestActive());
				break;
			case "meta":
				result = ow.writeValueAsString(metadataServiceImpl.findAllLatestActive());
				break;
			case "vizpod":
				result = ow.writeValueAsString(vizpodServiceImpl.findAllLatestActive());
				break;
			case "datastore":
				result = ow.writeValueAsString(dataStoreServiceImpl.findAllLatestActive());
				break;
			
			 * case "group": result =
			 * ow.writeValueAsString(groupServiceImpl.findAllLatestActive()); break;
			 
			case "user":
				result = ow.writeValueAsString(userServiceImpl.findAllLatestActive());
				break;
			case "session":
				result = ow.writeValueAsString(sessionServiceImpl.findAllLatestActive());
				break;
			case "activity":
				result = ow.writeValueAsString(activityServiceImpl.findAllLatestActive());
				break;
			case "role":
				result = ow.writeValueAsString(roleServiceImpl.findAllLatestActive());
				break;
			case "group":
				result = ow.writeValueAsString(groupServiceImpl.findAllLatestActive());
				break;
			case "privilege":
				result = ow.writeValueAsString(privilegeServiceImpl.findAllLatestActive());
				break;
			case "dimension":
				result = ow.writeValueAsString(dimensionServiceImpl.findAllLatestActive());
				break;
			case "measure":
				result = ow.writeValueAsString(measureServiceImpl.findAllLatestActive());
				break;
			case "dataset":
				result = ow.writeValueAsString(datasetServiceImpl.findAllLatestActive());
				break;
			case "application":
				result = ow.writeValueAsString(applicationServiceImpl.findAllLatestActive());
				break;
			case "datasource":
				result = ow.writeValueAsString(datasourceServiceImpl.findAllLatestActive());
				break;
			case "dq":
				result = ow.writeValueAsString(dataQualServiceImpl.findAllLatestActive());
				break;
			case "dqgroup":
				result = ow.writeValueAsString(dataQualGroupServiceImpl.findAllLatestActive());
				break;
			case "load":
				result = ow.writeValueAsString(loadServiceImpl.findAllLatestActive());
				break;
			case "dqExec":
				result = ow.writeValueAsString(dataQualExecServiceImpl.findAllLatestActive());
				break;
			case "dqgroupexec":
				result = ow.writeValueAsString(dataQualGroupExecServiceImpl.findAllLatestActive());
				break;
			case "rule":
				result = ow.writeValueAsString(ruleServiceImpl.findAllLatestActive());
				break;
			case "ruleexec":
				result = ow.writeValueAsString(ruleExecServiceImpl.findAllLatestActive());
				break;
			case "rulegroup":
				result = ow.writeValueAsString(ruleGroupServiceImpl.findAllLatestActive());
				break;
			case "rulegroupexec":
				result = ow.writeValueAsString(ruleGroupExecServiceImpl.findAllLatestActive());
				break;
			case "loadexec":
				result = ow.writeValueAsString(loadExecServiceImpl.findAllLatestActive());
				break;
			case "function":
				result = ow.writeValueAsString(functionServiceImpl.findAllLatestActive());
				break;
			case "profile":
				result = ow.writeValueAsString(profileServiceImpl.findAllLatestActive());
				break;
			case "profileexec":
				result = ow.writeValueAsString(profileExecServiceImpl.findAllLatestActive());
				break;
			case "profilegroup":
				result = ow.writeValueAsString(profileGroupServiceImpl.findAllLatestActive());
				break;
			case "profilegroupexec":
				result = ow.writeValueAsString(profileGroupExecServiceImpl.findAllLatestActive());
				break;
			case "algorithm":
				result = ow.writeValueAsString(algorithmServiceImpl.findAllLatestActive());
				break;
			case "model":
				result = ow.writeValueAsString(modelServiceImpl.findAllLatestActive());
				break;
			case "modelexec":
				result = ow.writeValueAsString(modelExecServiceImpl.findAllLatestActive());
				break;
			case "paramlist":
				result = ow.writeValueAsString(paramListServiceImpl.findAllLatestActive());
				break;
			case "paramset":
				result = ow.writeValueAsString(paramSetServiceImpl.findAllLatestActive());
				break;

			}
		}
		return result;
	}
*/

	/********************** UNUSED **********************/
	/*public String getMapByDatapod(String datapodUUID, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase("map")) {
			result = ow.writeValueAsString(mapServiceImpl.findMapByDatapod(datapodUUID));
		}

		return result;
	}*/

	public String validateUser(String userName, String password) throws IOException, JSONException, ParseException,
			ServletException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(sessionServiceImpl.validateUser(userName, password));
		return result;
	}

	/**********************UNUSED**********************/
	/*public String createActivity(String UserUUID) throws IOException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(activityServiceImpl.createActivity(UserUUID));
		return result;
	}
*/
	public String getActivityByUser(String UserUUID) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

		result = ow.writeValueAsString(activityServiceImpl.findActivityByUser(UserUUID));
		return result;
	}

	public String getUserByName(String userName) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(userServiceImpl.findUserByName(userName));
		return result;
	}

	public String getLatestByUsername(String userName) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

		result = ow.writeValueAsString(userServiceImpl.findLatestByUsername(userName));
		return result;
	}
	/*
	 * public String loginUser(String userId) throws JsonProcessingException {
	 * String result = null; ObjectWriter ow = new
	 * ObjectMapper().writer().withDefaultPrettyPrinter();
	 *
	 * result = ow.writeValueAsString(sessionServiceImpl.loginUser(userId));
	 *
	 *
	 * return result; }
	 */

	public String createUserSession(String userName)
			throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

		result = ow.writeValueAsString(sessionServiceImpl.createUserSession(userName));

		return result;
	}

	public String logoutSession(String sessionId)
			throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(sessionServiceImpl.logoutSession(sessionId));
		return result;
	}

	public String getRelationByDatapod(String datapodUUID, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase("relation")) {
			result = ow.writeValueAsString(relationServiceImpl.findRelationByDatapod(datapodUUID));
		}
		return result;
	}

	/********************** UNUSED **********************/
	/*public String getFilterByDatapod(String datapodUUID, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase("filter")) {
			result = ow.writeValueAsString(filterServiceImpl.findFilterByDatapod(datapodUUID));
		}

		return result;
	}*/

	/********************** UNUSED **********************/
	/*public String getFilterByRelation(String relationUUID, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase("filter")) {
			result = ow.writeValueAsString(filterServiceImpl.findFilterByRelation(relationUUID));
		}

		return result;
	}*/

	public String getDagExecByDatapod(String datapodUUID, String type) throws JsonProcessingException {

		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase("dagexec")) {
			result = ow.writeValueAsString(dagExecServiceImpl.findDagExecByDatapod(datapodUUID));
		}
		return result;
	}

	/********************** UNUSED **********************/
	/*public String getConditionByRelation(String relationUUID, String type) throws JsonProcessingException {

		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase("condition")) {
			result = ow.writeValueAsString(conditionServiceImpl.findConditionByRelation(relationUUID));
		}
		return result;
	}*/

	public String getDagExecByDag(String dagUUID, String type) throws JsonProcessingException {

		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase("dagexec")) {
			result = ow.writeValueAsString(dagExecServiceImpl.findDagExecByDag(dagUUID));
		}
		return result;
	}

	public String getVizpodByDatapod(String datapodUUID, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase(MetaType.vizpod.toString())) {
			result = ow.writeValueAsString(vizpodServiceImpl.findVizpodByDatapod(datapodUUID));
		}

		return result;
	}

	/********************** UNUSED **********************/
	/*public String getVizpodById(String uuid, String version, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (StringUtils.isNotBlank(type) && type.equalsIgnoreCase(MetaType.vizpod.toString())) {
			if (StringUtils.isNotBlank(version)) {
				result = ow.writeValueAsString(vizpodServiceImpl.findOneByUuidAndVersion(uuid, version));
			} else {
				result = ow.writeValueAsString(vizpodServiceImpl.findLatestByUuid(uuid));
			}
		}

		return result;
	}*/

	public String getDatapodByRelation(String relationUuid, String version, String type)
			throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase(MetaType.datapod.toString())) {
			try {
				result = ow.writeValueAsString(relationServiceImpl.findDatapodByRelation(relationUuid, version));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public String getDataStoreByDatapod(String datapodUUID, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase(MetaType.datastore.toString())) {
			result = ow.writeValueAsString(dataStoreServiceImpl.findDataStoreByDatapod(datapodUUID));
		}
		return result;
	}

	public BaseEntity save(Map<String, Object> operator, String type) throws Exception {
		// Session session=sessionServiceImpl.findOneById(sessionId);
		// String uuid=session.getUserInfo().getRef().getUuid();

//		String Id = "";
		BaseEntity baseEntity=null;
		if (type != null && !type.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			type = type.toLowerCase();
			switch (type) {
			/*case "relation":
				Relation optr = mapper.convertValue(operator, Relation.class);
				Id = relationServiceImpl.save(optr).getId();
				break;*/
			/*case "datapod":
				Datapod datapod = mapper.convertValue(operator, Datapod.class);
				try {
					Id = datapodServiceImpl.save(datapod).getId();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;*/
			/*case "datadag":
				Dag dag = mapper.convertValue(operator, Dag.class);
				Id = dagServiceImpl.Save(dag).getId();
				break;*/
			/*case "filter":
				Filter filterOptr = mapper.convertValue(operator, Filter.class);
				Filter filter = filterServiceImpl.save(filterOptr);
				Id = filter.getId();
				break;*/
			/*case "expression":
				Expression expressionOptr = mapper.convertValue(operator, Expression.class);
				Expression expression = expressionServiceImpl.save(expressionOptr);
				Id = expression.getId();
				break;*/
			/*case "formula":
				Formula formulaOptr = mapper.convertValue(operator, Formula.class);
				Formula formula = formulaServiceImpl.save(formulaOptr);
				Id = formula.getId();
				break;*/
			/*case "map":
				com.inferyx.framework.domain.Map map = mapper.convertValue(operator,
						com.inferyx.framework.domain.Map.class);
				Id = mapServiceImpl.save(map).getId();
				break;*/
			/*case "condition":
				Condition condition = mapper.convertValue(operator, Condition.class);
				;
				Id = conditionServiceImpl.save(condition).getId();
				break;*/
			/*case "dagExec":
				DagExec dagExec = mapper.convertValue(operator, DagExec.class);
				;
				logger.info(dagExec);
				logger.info("*************" + operator);
				Id = dagExecServiceImpl.save(dagExec).getId();
				break;*/
			/*case "meta":
				Meta metadata = mapper.convertValue(operator, Meta.class);
				;
				Id = metadataServiceImpl.Save(metadata).getId();
				break;*/
			/*case "vizpod":
				Vizpod vizpod = mapper.convertValue(operator, Vizpod.class);
				;
				Id = vizpodServiceImpl.save(vizpod).getId();
				break;*/
			/*case "datastore":
				DataStore datastore = mapper.convertValue(operator, DataStore.class);
				;
				Id = dataStoreServiceImpl.save(datastore).getId();
				break;*/
			/*
			 * case "group": Group grpoupOptr = mapper.convertValue(operator, Group.class);
			 * Group group = groupServiceImpl.Save(grpoupOptr); Id = group.getId(); break;
			 */
			/*case "user":
				User user = mapper.convertValue(operator, User.class);
				;
				Id = userServiceImpl.save(user).getId();
				break;*/
			/*case "session":
				Session session = mapper.convertValue(operator, Session.class);
				Id = sessionServiceImpl.save(session).getId();
				break;*/
			/*case "activity":
				Activity activity = mapper.convertValue(operator, Activity.class);
				;
				Id = activityServiceImpl.save(activity).getId();
				break;*/
			/*case "role":
				Role roleOptr = mapper.convertValue(operator, Role.class);
				Id = roleServiceImpl.save(roleOptr).getId();
				break;*/
			/*case "group":
				Group groupOptr = mapper.convertValue(operator, Group.class);
				Id = groupServiceImpl.save(groupOptr).getId();
				break;*/
			/*case "privilege":
				Privilege privilegeOptr = mapper.convertValue(operator, Privilege.class);
				Id = privilegeServiceImpl.save(privilegeOptr).getId();
				break;*/
			/*case "dimension":
				Dimension dimensionOptr = mapper.convertValue(operator, Dimension.class);
				Id = dimensionServiceImpl.save(dimensionOptr).getId();
				break;*/
			/*case "measure":
				Measure measureOptr = mapper.convertValue(operator, Measure.class);
				Id = measureServiceImpl.save(measureOptr).getId();
				break;*/
			/*case "vizexec":
				VizExec vizExecOptr = mapper.convertValue(operator, VizExec.class);
				Id = vizExecServiceImpl.save(vizExecOptr).getId();
				break;*/
			/*case "dataset":
				Dataset datasetOptr = mapper.convertValue(operator, Dataset.class);
				Id = datasetServiceImpl.save(datasetOptr).getId();
				break;*/
			case "datasetview":
				DatasetView datasetViewOptr = mapper.convertValue(operator, DatasetView.class);
				baseEntity = datasetServiceImpl.save(datasetViewOptr);
				break;
			/*case "rulegroup":
				RuleGroup ruleGroupOptr = mapper.convertValue(operator, RuleGroup.class);
				Id = ruleGroupServiceImpl.save(ruleGroupOptr).getId();
				break;*/
			/*case "application":
				Application applicationOptr = mapper.convertValue(operator, Application.class);
				Id = applicationServiceImpl.save(applicationOptr).getId();
				break;*/
			/*case "datasource":
				Datasource datasourceOptr = mapper.convertValue(operator, Datasource.class);
				Id = datasourceServiceImpl.save(datasourceOptr).getId();
				break;*/
			/*case "dq":
				DataQual dataQualOptr = mapper.convertValue(operator, DataQual.class);
				Id = dataQualServiceImpl.save(dataQualOptr).getId();
				break;*/
			/*case "dqgroup":
				DataQualGroup dataQualGroupOptr = mapper.convertValue(operator, DataQualGroup.class);
				Id = dataQualGroupServiceImpl.save(dataQualGroupOptr).getId();
				break;*/
			/*case "dqExec":
				DataQualExec dataQualExecOptr = mapper.convertValue(operator, DataQualExec.class);
				Id = dataQualExecServiceImpl.save(dataQualExecOptr).getId();
				break;*/
			/*case "dqgroupexec":
				DataQualGroupExec dataQualExecGroupOptr = mapper.convertValue(operator, DataQualGroupExec.class);
				Id = dataQualGroupExecServiceImpl.save(dataQualExecGroupOptr).getId();
				break;*/
			case "dqview":
				DQView dqViewOptr = mapper.convertValue(operator, DQView.class);
				baseEntity = dataQualServiceImpl.save(dqViewOptr);
				break;
			case "ruleview":
				RuleView ruleViewOptr = mapper.convertValue(operator, RuleView.class);
				baseEntity =  ruleServiceImpl.save(ruleViewOptr);
				break;
			/*case "rule":
				Rule ruleOptr = mapper.convertValue(operator, Rule.class);
				Id = ruleServiceImpl.save(ruleOptr).getId();
				break;*/
			/*case "rulegroupexec":
				RuleGroupExec ruleGroupExecOptr = mapper.convertValue(operator, RuleGroupExec.class);
				Id = ruleGroupExecServiceImpl.save(ruleGroupExecOptr).getId();
				break;*/
			/*case "loadexec":
				LoadExec loadExecOptr = mapper.convertValue(operator, LoadExec.class);
				Id = loadExecServiceImpl.save(loadExecOptr).getId();
				break;*/
			/*case "function":
				Function functionOptr = mapper.convertValue(operator, Function.class);
				Id = functionServiceImpl.save(functionOptr).getId();
				break;*/
			/*case "load":
				Load loadOptr = mapper.convertValue(operator, Load.class);
				Id = loadServiceImpl.save(loadOptr).getId();
				break;*/
			/*case "profile":
				Profile profileOptr = mapper.convertValue(operator, Profile.class);
				Id = profileServiceImpl.save(profileOptr).getId();
				break;*/
			/*case "profileexec":
				ProfileExec profileExecOptr = mapper.convertValue(operator, ProfileExec.class);
				Id = profileExecServiceImpl.save(profileExecOptr).getId();
				break;*/
			/*case "profilegroup":
				ProfileGroup profileGroupOptr = mapper.convertValue(operator, ProfileGroup.class);
				Id = profileGroupServiceImpl.save(profileGroupOptr).getId();
				break;*/
			/*case "profilegroupexec":
				ProfileGroupExec profileGroupExecOptr = mapper.convertValue(operator, ProfileGroupExec.class);
				Id = profileGroupExecServiceImpl.save(profileGroupExecOptr).getId();
				break;*/
			/*case "dashboard":
				Dashboard dashboard = mapper.convertValue(operator, Dashboard.class);
				Id = dashboardServiceImpl.save(dashboard).getId();
				break;*/
			case "dashboardview":
				DashboardView dashboardView = mapper.convertValue(operator, DashboardView.class);
				baseEntity =  dashboardViewServiceImpl.save(dashboardView);
				break;
			/*case "algorithm":
				Algorithm algorithm = mapper.convertValue(operator, Algorithm.class);
				Id = algorithmServiceImpl.save(algorithm).getId();
				break;*/
			/*case "model":
				Model model = mapper.convertValue(operator, Model.class);
				Id = modelServiceImpl.save(model).getId();
				break;*/
			/*case "modelexec":
				ModelExec modelExec = mapper.convertValue(operator, ModelExec.class);
				Id = modelExecServiceImpl.save(modelExec).getId();
				break;*/
			/*case "paramlist":
				ParamList paramList = mapper.convertValue(operator, ParamList.class);
				Id = paramListServiceImpl.save(paramList).getId();
				break;*/
			/*case "paramset":
				ParamSet paramSet = mapper.convertValue(operator, ParamSet.class);
				Id = paramSetServiceImpl.save(paramSet).getId();
				break;*/
			case "reconview":
				ReconView reconViewOptr = mapper.convertValue(operator, ReconView.class);
				baseEntity = reconViewServiceImpl.save(reconViewOptr);
				break;
			case "reportview" :
				ReportView reportView = mapper.convertValue(operator, ReportView.class);
				baseEntity = reportViewServiceImpl.save(reportView);
				break;
			}
		}
		return baseEntity;
	}

	/********************** UNUSED **********************/
	/*public void delete(String id, String type) {

		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			case "relation":
				relationServiceImpl.delete(id);
				break;
			case "datapod":
				datapodServiceImpl.delete(id);
				break;
			case "datadag":
				dagServiceImpl.delete(id);
				break;
			case "filter":
				filterServiceImpl.delete(id);
				break;
			case "expression":
				expressionServiceImpl.delete(id);
				break;
			case "formula":
				formulaServiceImpl.delete(id);
				break;
			case "map":
				mapServiceImpl.delete(id);
				break;
			case "condition":
				conditionServiceImpl.delete(id);
				break;
			case "dagexec":
				dagExecServiceImpl.delete(id);
				break;
			case "meta":
				metadataServiceImpl.delete(id);
				break;
			case "vizpod":
				vizpodServiceImpl.delete(id);
				break;
			case "datastore":
				dataStoreServiceImpl.delete(id);
				break;
			
			 * case "group": groupServiceImpl.delete(id); break;
			 
			case "user":
				userServiceImpl.delete(id);
				break;
			case "session":
				sessionServiceImpl.delete(id);
				break;
			case "activity":
				activityServiceImpl.delete(id);
				break;
			case "role":
				roleServiceImpl.delete(id);
				break;
			case "group":
				groupServiceImpl.delete(id);
				break;
			case "privilege":
				privilegeServiceImpl.delete(id);
				break;
			case "dimension":
				dimensionServiceImpl.delete(id);
				break;
			case "measure":
				measureServiceImpl.delete(id);
				break;
			case "vizexec":
				vizExecServiceImpl.delete(id);
				break;
			case "dataset":
				datasetServiceImpl.delete(id);
				break;
			case "application":
				applicationServiceImpl.delete(id);
				break;
			case "datasource":
				datasourceServiceImpl.delete(id);
				break;
			case "dq":
				dataQualServiceImpl.delete(id);
				break;
			case "dqgroup":
				dataQualGroupServiceImpl.delete(id);
				break;
			case "dqExec":
				dataQualExecServiceImpl.delete(id);
				break;
			case "dqgroupexec":
				dataQualGroupExecServiceImpl.delete(id);
				break;
			case "rule":
				ruleServiceImpl.delete(id);
				break;
			case "rulegroup":
				ruleGroupServiceImpl.delete(id);
				break;
			case "loadexec":
				loadExecServiceImpl.delete(id);
				break;
			case "function":
				functionServiceImpl.delete(id);
				break;
			case "profile":
				profileServiceImpl.delete(id);
				break;
			case "profileexec":
				profileExecServiceImpl.delete(id);
				break;
			case "profilegroup":
				profileGroupServiceImpl.delete(id);
				break;
			case "profilegroupexec":
				profileGroupExecServiceImpl.delete(id);
				break;
			case "algorithm":
				algorithmServiceImpl.delete(id);
				break;
			case "model":
				modelServiceImpl.delete(id);
				break;
			case "modelexec":
				modelExecServiceImpl.delete(id);
				break;
			case "paramlist":
				paramListServiceImpl.delete(id);
				break;
			case "paramset":
				paramSetServiceImpl.delete(id);
				break;
			}
		}

	}
*/
	/********************** UNUSED 
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException **********************/
	/*public String getMetaDataList(String type, HttpServletRequest request) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			case "relation":
				
				 * result = ow.writeValueAsString(relationServiceImpl.findAll()); break;
				 
				List<Relation> relation = relationServiceImpl.findAll();
				result = ow.writeValueAsString(relationServiceImpl.resolveName(relation));
				break;
			case "dashboard":
				List<Dashboard> dashboard = dashboardServiceImpl.findAll();
				result = ow.writeValueAsString(dashboardServiceImpl.resolveName(dashboard));
				break;
			case "datapod":
				
				 * result = ow.writeValueAsString(datapodServiceImpl.findAll()); break;
				 
				List<Datapod> datapod = datapodServiceImpl.findAll();
				result = ow.writeValueAsString(datapodServiceImpl.resolveName(datapod));
				// System.out.println("\n\n"+"RegisterService - datapod: result=
				// "+result+"\n\n");
				break;
			case "dag":
				
				 * result = ow.writeValueAsString(dagServiceImpl.findAll()); break;
				 
				List<Dag> dag = dagServiceImpl.findAll();
				result = ow.writeValueAsString(dagServiceImpl.resolveName(dag));
				// System.out.println("\n\n"+"RegisterService - dag: result= "+result+"\n\n");
				break;
			case "filter":
				
				 * result = ow.writeValueAsString(filterServiceImpl.findAll()); break;
				 
				List<Filter> filter = filterServiceImpl.findAll();
				result = ow.writeValueAsString(filterServiceImpl.resolveName(filter));
				break;
			case "expression":
				
				 * result = ow.writeValueAsString(expressionServiceImpl.findAll()); break;
				 
				List<Expression> expression = expressionServiceImpl.findAll();
				result = ow.writeValueAsString(expressionServiceImpl.resolveName(expression));
				break;
			case "formula":
				
				 * result = ow.writeValueAsString(formulaServiceImpl.findAll()); break;
				 
				List<Formula> formula = formulaServiceImpl.findAll();
				result = ow.writeValueAsString(formulaServiceImpl.resolveName(formula));
				break;
			case "map":
				
				 * result = ow.writeValueAsString(mapServiceImpl.findAll()); break;
				 
				List<com.inferyx.framework.domain.Map> map = mapServiceImpl.findAll();
				result = ow.writeValueAsString(mapServiceImpl.resolveName(map));
				// System.out.println("\n\n"+"RegisterService - map: result= "+result+"\n\n");
				break;
			case "condition":
				
				 * result = ow.writeValueAsString(conditionServiceImpl.findAll()); break;
				 
				List<Condition> condition = conditionServiceImpl.findAll();
				result = ow.writeValueAsString(conditionServiceImpl.resolveName(condition));
				break;
			case "dagexec":
				
				 * result = ow.writeValueAsString(dagExecServiceImpl.findAll()); break;
				 
				List<DagExec> dagExec = dagExecServiceImpl.findAll();
				result = ow.writeValueAsString(dagExecServiceImpl.resolveName(dagExec));
				break;
			case "meta":
				result = ow.writeValueAsString(metadataServiceImpl.findAll());
				break;
			case "vizpod":
				
				 * result = ow.writeValueAsString(vizpodServiceImpl.findAll()); break;
				 
				List<Vizpod> vizpod = vizpodServiceImpl.findAll();
				result = ow.writeValueAsString(vizpodServiceImpl.resolveName(vizpod));
				break;
			case "datastore":
				
				 * result = ow.writeValueAsString(dataStoreServiceImpl.findAll()); break;
				 
				List<DataStore> datastore = dataStoreServiceImpl.findAll();
				result = ow.writeValueAsString(dataStoreServiceImpl.resolveName(datastore));
				break;
			
			 * case "group":
			 * 
			 * result = ow.writeValueAsString(groupServiceImpl.findAll()); break;
			 * 
			 * List<Group> group = groupServiceImpl.findAll(); result =
			 * ow.writeValueAsString(groupServiceImpl.resolveName(group)); break;
			 
			case "session":
				
				 * result = ow.writeValueAsString(sessionServiceImpl.findAll()); break;
				 
				List<Session> session = sessionServiceImpl.findAll();
				result = ow.writeValueAsString(sessionServiceImpl.resolveName(session));
				break;
			case "user":
				
				 * result = ow.writeValueAsString(userServiceImpl.findAll()); break;
				 
				List<User> user = userServiceImpl.findAll();
				result = ow.writeValueAsString(userServiceImpl.resolveName(user));
				break;
			case "activity":
				
				 * result = ow.writeValueAsString(activityServiceImpl.findAll()); break;
				 
				List<Activity> activity = activityServiceImpl.findAll();
				result = ow.writeValueAsString(activityServiceImpl.resolveName(activity));
				break;
			case "role":
				
				 * result = ow.writeValueAsString(roleServiceImpl.findAll()); break;
				 
				List<Role> role = roleServiceImpl.findAll();
				result = ow.writeValueAsString(roleServiceImpl.resolveName(role));
				break;
			case "group":
				
				 * result = ow.writeValueAsString(userGroupServiceImpl.findAll()); break;
				 
				List<Group> group = groupServiceImpl.findAll();
				result = ow.writeValueAsString(groupServiceImpl.resolveName(group));
				break;
			case "privilege":
				
				 * result = ow.writeValueAsString(privilegeServiceImpl.findAll()); break;
				 
				List<Privilege> privilege = privilegeServiceImpl.findAll();
				result = ow.writeValueAsString(privilegeServiceImpl.resolveName(privilege));
				break;
			case "dimension":
				
				 * result = ow.writeValueAsString(dimensionServiceImpl.findAll()); break;
				 
				List<Dimension> dimension = dimensionServiceImpl.findAll();
				result = ow.writeValueAsString(dimensionServiceImpl.resolveName(dimension));
				break;
			case "measure":
				
				 * result = ow.writeValueAsString(measureServiceImpl.findAll()); break;
				 
				List<Measure> measure = measureServiceImpl.findAll();
				result = ow.writeValueAsString(measureServiceImpl.resolveName(measure));
				break;
			case "vizexec":
				List<VizExec> vizExec = vizExecServiceImpl.findAll();
				result = ow.writeValueAsString(vizExecServiceImpl.resolveName(vizExec));
				break;
			case "dataset":
				List<Dataset> dataset = datasetServiceImpl.findAll();
				result = ow.writeValueAsString(datasetServiceImpl.resolveName(dataset));
				break;
			case "application":
				List<Application> application = applicationServiceImpl.findAll();
				result = ow.writeValueAsString(applicationServiceImpl.resolveName(application));
				break;
			case "datasource":
				List<Datasource> datasource = datasourceServiceImpl.findAll();
				result = ow.writeValueAsString(datasourceServiceImpl.resolveName(datasource));
				break;
			case "load":
				List<Load> load = loadServiceImpl.findAll();
				result = ow.writeValueAsString(loadServiceImpl.resolveName(load));
				System.out.println("\n\n" + "RegisterService - load: result= " + result + "\n\n");
				break;
			case "dq":
				List<DataQual> dq = dataQualServiceImpl.findAll();
				result = ow.writeValueAsString(dataQualServiceImpl.resolveName(dq));
				break;
			case "dqgroup":
				List<DataQualGroup> dqGroup = dataQualGroupServiceImpl.findAll();
				result = ow.writeValueAsString(dataQualGroupServiceImpl.resolveName(dqGroup));
				break;
			case "dqExec":
				List<DataQualExec> dqExec = dataQualExecServiceImpl.findAll();
				result = ow.writeValueAsString(dataQualExecServiceImpl.resolveName(dqExec));
				break;
			case "dqgroupexec":
				List<DataQualGroupExec> dqExecGroup = dataQualGroupExecServiceImpl.findAll();
				result = ow.writeValueAsString(dataQualGroupExecServiceImpl.resolveNameGroup(dqExecGroup));
				break;
			case "rule":
				List<Rule> rule = ruleServiceImpl.findAll();
				result = ow.writeValueAsString(ruleServiceImpl.resolveName(rule));
				System.out.println("\n\n" + "RegisterService - rule: result= " + result + "\n\n");
				break;
			case "ruleexec":
				List<RuleExec> ruleExec = ruleExecServiceImpl.findAll();
				result = ow.writeValueAsString(ruleExecServiceImpl.resolveName(ruleExec));
				break;
			case "rulegroup":
				List<RuleGroup> ruleGroup = ruleGroupServiceImpl.findAll();
				result = ow.writeValueAsString(ruleGroupServiceImpl.resolveName(ruleGroup));
				break;
			case "rulegroupexec":
				List<RuleGroupExec> ruleGroupExec = ruleGroupExecServiceImpl.findAll();
				result = ow.writeValueAsString(ruleGroupExecServiceImpl.resolveName(ruleGroupExec));
				break;
			case "loadexec":
				List<LoadExec> loadExec = loadExecServiceImpl.findAll();
				result = ow.writeValueAsString(loadExecServiceImpl.resolveName(loadExec));
				break;
			case "function":
				List<Function> function = functionServiceImpl.findAll();
				result = ow.writeValueAsString(functionServiceImpl.resolveName(function));
				break;
			case "profile":
				List<Profile> profile = profileServiceImpl.findAll();
				result = ow.writeValueAsString(profileServiceImpl.resolveName(profile));
				break;
			case "profileexec":
				List<ProfileExec> profileExec = profileExecServiceImpl.findAll();
				result = ow.writeValueAsString(profileExecServiceImpl.resolveName(profileExec));
				break;
			case "profilegroup":
				List<ProfileGroup> profileGroup = profileGroupServiceImpl.findAll();
				result = ow.writeValueAsString(profileGroupServiceImpl.resolveName(profileGroup));
				break;
			case "profilegroupexec":
				List<ProfileGroupExec> profileGroupExec = profileGroupExecServiceImpl.findAll();
				result = ow.writeValueAsString(profileGroupExecServiceImpl.resolveName(profileGroupExec));
				break;
			case "algorithm":
				List<Algorithm> algorithm = algorithmServiceImpl.findAll();
				result = ow.writeValueAsString(algorithmServiceImpl.resolveName(algorithm));
				break;
			case "model":
				List<Model> model = modelServiceImpl.findAll();
				result = ow.writeValueAsString(modelServiceImpl.resolveName(model));
				break;
			case "modelexec":
				List<ModelExec> modelExec = modelExecServiceImpl.findAll();
				result = ow.writeValueAsString(modelExecServiceImpl.resolveName(modelExec));
				break;
			case "paramlist":
				List<ParamList> paramList = paramListServiceImpl.findAll();
				result = ow.writeValueAsString(paramListServiceImpl.resolveName(paramList));
				break;
			case "paramset":
				List<ParamSet> paramSet = paramSetServiceImpl.findAll();
				result = ow.writeValueAsString(paramSetServiceImpl.resolveName(paramSet));
				break;
			}

		}
		return result;
	}*/

	public String getOneById(String id, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			/*case "relation":
				Relation relation = relationServiceImpl.findOneById(id);
				result = ow.writeValueAsString(relationServiceImpl.resolveName(relation));
				break;*/
			/*case "datapod":
				Datapod datapod = datapodServiceImpl.findOneById(id);
				result = ow.writeValueAsString(datapodServiceImpl.resolveName(datapod));
				break;*/
			/*case "dag":
				Dag dag = dagServiceImpl.findOneById(id);
				result = ow.writeValueAsString(dagServiceImpl.resolveName(dag));
				break;*/
			/*case "filter":
				Filter filter = filterServiceImpl.findOneById(id);
				result = ow.writeValueAsString(filterServiceImpl.resolveName(filter));
				break;*/
			/*case "expression":
				Expression expression = expressionServiceImpl.findOneById(id);
				result = ow.writeValueAsString(expressionServiceImpl.resolveName(expression));
				break;*/
			/*case "formula":
				Formula formula = formulaServiceImpl.findOneById(id);
				result = ow.writeValueAsString(formulaServiceImpl.resolveName(formula));
				break;*/
			/*case "map":
				com.inferyx.framework.domain.Map map = mapServiceImpl.findOneById(id);
				result = ow.writeValueAsString(mapServiceImpl.resolveName(map));
				break;*/
			/*case "dagexec":
				DagExec dagExec = dagExecServiceImpl.findOneById(id);
				result = ow.writeValueAsString(dagExecServiceImpl.resolveName(dagExec));
				break;*/
			/*case "condition":
				Condition condition = conditionServiceImpl.findOneById(id);
				result = ow.writeValueAsString(conditionServiceImpl.resolveName(condition));
				break;*/
			/*case "meta":
				result = ow.writeValueAsString(metadataServiceImpl.findOneById(id));
				break;*/
			/*case "vizpod":
				Vizpod vizpod = vizpodServiceImpl.findOneById(id);
				result = ow.writeValueAsString(vizpodServiceImpl.resolveName(vizpod));
				break;*/
			/*case "datastore":
				DataStore datastore = dataStoreServiceImpl.findOneById(id);
				result = ow.writeValueAsString(dataStoreServiceImpl.resolveName(datastore));
				break;*/
			/*
			 * case "group": Group group = groupServiceImpl.findOneById(id); result =
			 * ow.writeValueAsString(groupServiceImpl.resolveName(group)); break;
			 */
			/*case "user":
				User user = userServiceImpl.findOneById(id);
				result = ow.writeValueAsString(userServiceImpl.resolveName(user));
				break;*/
			/*case "session":
				Session session = sessionServiceImpl.findOneById(id);
				result = ow.writeValueAsString(sessionServiceImpl.resolveName(session));
				break;*/
			/*case "failuer":
				result = ow.writeValueAsString(userServiceImpl.findOneById(id));
				break;*/
			/*case "activity":
				Activity activity = activityServiceImpl.findOneById(id);
				result = ow.writeValueAsString(activityServiceImpl.resolveName(activity));
				break;*/
			/*case "role":
				Role role = roleServiceImpl.findOneById(id);
				result = ow.writeValueAsString(roleServiceImpl.resolveName(role));
				break;*/
			/*case "group":
				Group group = groupServiceImpl.findOneById(id);
				result = ow.writeValueAsString(groupServiceImpl.resolveName(group));
				break;*/
			/*case "privilege":
				Privilege privilege = privilegeServiceImpl.findOneById(id);
				result = ow.writeValueAsString(privilegeServiceImpl.resolveName(privilege));
				break;*/
			/*case "dimension":
				Dimension dimension = dimensionServiceImpl.findOneById(id);
				result = ow.writeValueAsString(dimensionServiceImpl.resolveName(dimension));
				break;*/
			/*case "measure":
				Measure measure = measureServiceImpl.findOneById(id);
				result = ow.writeValueAsString(measureServiceImpl.resolveName(measure));
				break;*/
			/*case "vizexec":
				VizExec vizExec = vizExecServiceImpl.findOneById(id);
				result = ow.writeValueAsString(vizExecServiceImpl.resolveName(vizExec));
				break;*/
			/*case "dataset":
				Dataset dataset = datasetServiceImpl.findOneById(id);
				result = ow.writeValueAsString(datasetServiceImpl.resolveName(dataset));
				break;*/
			case "datasetview":
				result = ow.writeValueAsString(datasetViewServiceImpl.findOneById(id));
				break;
			/*case "application":
				Application application = applicationServiceImpl.findOneById(id);
				result = ow.writeValueAsString(applicationServiceImpl.resolveName(application));
				break;*/
			/*case "datasource":
				Datasource datasource = datasourceServiceImpl.findOneById(id);
				result = ow.writeValueAsString(datasourceServiceImpl.resolveName(datasource));
				break;*/
			/*case "dq":
				DataQual dq = dataQualServiceImpl.findOneById(id);
				result = ow.writeValueAsString(dataQualServiceImpl.resolveName(dq));
				break;*/
			/*case "dqgroup":
				DataQualGroup dqgroup = dataQualGroupServiceImpl.findOneById(id);
				result = ow.writeValueAsString(dataQualGroupServiceImpl.resolveName(dqgroup));
				break;*/
			/*case "dqExec":
				DataQualExec dqExec = dataQualExecServiceImpl.findOneById(id);
				result = ow.writeValueAsString(dataQualExecServiceImpl.resolveName(dqExec));
				break;*/
			/*case "dqgroupexec":
				DataQualGroupExec dqgroupExec = dataQualGroupExecServiceImpl.findOneById(id);
				result = ow.writeValueAsString(dataQualGroupExecServiceImpl.resolveName(dqgroupExec));
				break;*/
			case "dqview":
				DQView dqView = dqViewServiceImpl.findOneById(id);
				result = ow.writeValueAsString(dqViewServiceImpl.resolveName(dqView));
				break;
			/*case "rule":
				Rule rule = ruleServiceImpl.findOneById(id);
				result = ow.writeValueAsString(ruleServiceImpl.resolveName(rule));
				break;*/
			/*case "ruleexec":
				RuleExec ruleexec = ruleExecServiceImpl.findOneById(id);
				result = ow.writeValueAsString(ruleExecServiceImpl.resolveName(ruleexec));
				break;*/
			/*case "rulegroup":
				RuleGroup ruleGroup = ruleGroupServiceImpl.findOneById(id);
				result = ow.writeValueAsString(ruleGroupServiceImpl.resolveName(ruleGroup));
				break;*/
			/*case "loadexec":
				LoadExec loadExec = loadExecServiceImpl.findOneById(id);
				result = ow.writeValueAsString(loadExecServiceImpl.resolveName(loadExec));
				break;*/
			/*case "function":
				Function function = functionServiceImpl.findOneById(id);
				result = ow.writeValueAsString(functionServiceImpl.resolveName(function));
				break;
*/			/*case "profile":
				Profile profile = profileServiceImpl.findOneById(id);
				result = ow.writeValueAsString(profileServiceImpl.resolveName(profile));
				break;*/
			/*case "profileexec":
				ProfileExec profileExec = profileExecServiceImpl.findOneById(id);
				result = ow.writeValueAsString(profileExecServiceImpl.resolveName(profileExec));
				break;*/
			/*case "profilegroup":
				ProfileGroup profileGroup = profileGroupServiceImpl.findOneById(id);
				result = ow.writeValueAsString(profileGroupServiceImpl.resolveName(profileGroup));
				break;*/
			/*case "profilegroupexec":
				ProfileGroupExec profileGroupExec = profileGroupExecServiceImpl.findOneById(id);
				result = ow.writeValueAsString(profileGroupExecServiceImpl.resolveName(profileGroupExec));
				break;*/
			/*case "algorithm":
				Algorithm algorithm = algorithmServiceImpl.findOneById(id);
				result = ow.writeValueAsString(algorithmServiceImpl.resolveName(algorithm));
				break;*/
			/*case "model":
				Model model = modelServiceImpl.findOneById(id);
				result = ow.writeValueAsString(modelServiceImpl.resolveName(model));
				break;*/
			/*case "modelexec":
				ModelExec modelExec = modelExecServiceImpl.findOneById(id);
				result = ow.writeValueAsString(modelExecServiceImpl.resolveName(modelExec));
				break;*/
			/*case "paramlist":
				ParamList paramList = paramListServiceImpl.findOneById(id);
				result = ow.writeValueAsString(paramListServiceImpl.resolveName(paramList));
				break;*/
			/*case "paramset":
				ParamSet paramSet = paramSetServiceImpl.findOneById(id);
				result = ow.writeValueAsString(paramSetServiceImpl.resolveName(paramSet));
				break;*/
			case "reportview":
				ReportView reportView = reportViewServiceImpl.findOneById(id);
				result = ow.writeValueAsString(commonServiceImpl.resolveName(reportView, MetaType.reportview));
				break;
			}
		}

		return result;
	}

	public String getOneByUuidAndVersion(String uuid, String version, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			/*case "relation":
				Relation relation = relationServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(relationServiceImpl.resolveName(relation));
				break;*/
			/*case "datapod":
				Datapod datapod = datapodServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(datapodServiceImpl.resolveName(datapod));
				break;*/
			/*case "dag":
				Dag dag = dagServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dagServiceImpl.resolveName(dag));
				break;*/
			/*case "filter":
				Filter filter = filterServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(filterServiceImpl.resolveName(filter));
				break;*/
			/*case "expression":
				Expression expression = expressionServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(expressionServiceImpl.resolveName(expression));
				break;*/
			/*case "formula":
				Formula formula = formulaServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(formulaServiceImpl.resolveName(formula));
				break;*/
			/*case "map":
				com.inferyx.framework.domain.Map map = mapServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(mapServiceImpl.resolveName(map));
				break;*/
			/*case "dagexec":
				DagExec dagExec = dagExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dagExecServiceImpl.resolveName(dagExec));
				break;*/
			/*case "condition":
				Condition condition = conditionServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(conditionServiceImpl.resolveName(condition));
				break;*/
			/*case "meta":
				result = ow.writeValueAsString(metadataServiceImpl.findOneByUuidAndVersion(uuid, version));
				break;*/
			/*case "vizpod":
				Vizpod vizpod = vizpodServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(vizpodServiceImpl.resolveName(vizpod));
				break;*/
			/*case "datastore":
				DataStore datastore = dataStoreServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dataStoreServiceImpl.resolveName(datastore));
				break;*/
			/*case "user":
				User user = userServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(userServiceImpl.resolveName(user));
				break;*/
			/*case "session":
				Session session = sessionServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(sessionServiceImpl.resolveName(session));
				break;*/
			/*case "activity":
				Activity activity = activityServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(activityServiceImpl.resolveName(activity));
				break;*/
			/*case "role":
				Role role = roleServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(roleServiceImpl.resolveName(role));
				break;*/
			/*case "group":
				Group group = groupServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(groupServiceImpl.resolveName(group));
				break;*/
			/*case "privilege":
				Privilege privilege = privilegeServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(privilegeServiceImpl.resolveName(privilege));
				break;*/
			/*case "dimension":
				Dimension dimension = dimensionServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dimensionServiceImpl.resolveName(dimension));
				break;*/
			/*case "measure":
				Measure measure = measureServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(measureServiceImpl.resolveName(measure));
				break;*/
			/*case "vizexec":
				VizExec vizExec = vizExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(vizExecServiceImpl.resolveName(vizExec));
				break;*/
			/*case "dataset":
				Dataset dataset = datasetServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(datasetServiceImpl.resolveName(dataset));
				break;*/
			case "datasetview":
				result = ow.writeValueAsString(datasetViewServiceImpl.findOneByUuidAndVersion(uuid, version));
				break;
			case "dashboardview":
				result = ow.writeValueAsString(dashboardViewServiceImpl.findOneByUuidAndVersion(uuid, version));
				break;
			/*case "application":
				Application application = applicationServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(applicationServiceImpl.resolveName(application));
				break;*/
			/*case "load":
				Load load = loadServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(loadServiceImpl.resolveName(load));
				break;*/
			/*case "datasource":
				Datasource datasource = datasourceServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(datasourceServiceImpl.resolveName(datasource));
				break;*/
			/*case "dq":
				DataQual dq = dataQualServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dataQualServiceImpl.resolveName(dq));
				break;*/
			/*case "dqgroup":
				DataQualGroup dqgroup = dataQualGroupServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dataQualGroupServiceImpl.resolveName(dqgroup));
				break;*/
			/*case "dqExec":
				DataQualExec dqExec = dataQualExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dataQualExecServiceImpl.resolveName(dqExec));
				break;*/
			/*case "dqgroupexec":
				DataQualGroupExec dqgroupExec = dataQualGroupExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dataQualGroupExecServiceImpl.resolveName(dqgroupExec));
				break;*/
			case "dqview":
				DQView dqView = dqViewServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dqViewServiceImpl.resolveName(dqView));
				break;
			case "ruleview":
				result = ow.writeValueAsString(ruleViewServiceImpl.findOneByUuidAndVersion(uuid, version));
				break;
			case "reconview":
				result = ow.writeValueAsString(reconViewServiceImpl.findOneByUuidAndVersion(uuid, version));
				break;
			/*case "rule":
				Rule rule = ruleServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(ruleServiceImpl.resolveName(rule));
				break;*/
			/*case "ruleexec":
				RuleExec ruleExec = ruleExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(ruleExecServiceImpl.resolveName(ruleExec));
				break;*/
			/*case "rulegroup":
				RuleGroup ruleGroup = ruleGroupServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(ruleGroupServiceImpl.resolveName(ruleGroup));
				break;*/
			/*case "rulegroupexec":
				RuleGroupExec ruleGroupExec = ruleGroupExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(ruleGroupExecServiceImpl.resolveName(ruleGroupExec));
				break;*/
			/*case "loadexec":
				LoadExec loadExec = loadExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(loadExecServiceImpl.resolveName(loadExec));
				break;*/
			/*case "function":
				Function function = functionServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(functionServiceImpl.resolveName(function));
				break;*/
			/*case "profile":
				Profile profile = profileServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(profileServiceImpl.resolveName(profile));
				break;*/
			/*case "profileexec":
				ProfileExec profileExec = profileExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(profileExecServiceImpl.resolveName(profileExec));
				break;*/
			/*case "profilegroup":
				ProfileGroup profileGroup = profileGroupServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(profileGroupServiceImpl.resolveName(profileGroup));
				break;*/
			/*case "profilegroupexec":
				ProfileGroupExec profileGroupExec = profileGroupExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(profileGroupExecServiceImpl.resolveName(profileGroupExec));
				break;*/
			/*case "dashboard":
				Dashboard dashboard = dashboardServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dashboardServiceImpl.resolveName(dashboard));
				break;*/
			/*case "algorithm":
				Algorithm algorithm = algorithmServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(algorithmServiceImpl.resolveName(algorithm));
				break;*/
			/*case "model":
				Model model = modelServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(modelServiceImpl.resolveName(model));
				break;*/
			/*case "modelexec":
				ModelExec modelExec = modelExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(modelExecServiceImpl.resolveName(modelExec));
				break;*/
			/*case "paramlist":
				ParamList paramlist = paramListServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(paramListServiceImpl.resolveName(paramlist));
				break;*/
			/*case "paramset":
				ParamSet paramset = paramSetServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(paramSetServiceImpl.resolveName(paramset));
				break;*/
			case "reportview":
				result = ow.writeValueAsString(reportViewServiceImpl.findOneByUuidAndVersion(uuid, version));
				break;
			}
		}
		return result;
	}

	public String getLatestByUuid(String uuid, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			/*case "relation":
				Relation relation = relationServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(relationServiceImpl.resolveName(relation));
				break;*/
			/*case "datapod":
				Datapod datapod = datapodServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(datapodServiceImpl.resolveName(datapod));
				break;*/
			/*
			 * result = ow.writeValueAsString(datapodServiceImpl.findLatestByUuid(uuid));
			 * break;
			 */
			/*case "dag":
				Dag dag = dagServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(dagServiceImpl.resolveName(dag));
				break;*/
			/*case "filter":
				Filter filter = filterServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(filterServiceImpl.resolveName(filter));
				break;*/
			/*case "expression":
				Expression expression = expressionServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(expressionServiceImpl.resolveName(expression));
				break;*/
			/*case "formula":
				Formula formula = formulaServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(formulaServiceImpl.resolveName(formula));
				break;*/
			/*case "map":
				com.inferyx.framework.domain.Map map = mapServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(mapServiceImpl.resolveName(map));
				break;*/
			/*case "dagexec":
				DagExec dagExec = dagExecServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(dagExecServiceImpl.resolveName(dagExec));
				break;*/
			/*case "condition":
				Condition condition = conditionServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(conditionServiceImpl.resolveName(condition));
				break;*/
			/*case "meta":
				result = ow.writeValueAsString(metadataServiceImpl.findLatestByUuid(uuid));
				break;*/
			/*case "vizpod":
				Vizpod vizpod = vizpodServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(vizpodServiceImpl.resolveName(vizpod));
				break;*/
			/*case "datastore":
				DataStore datastore = dataStoreServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(dataStoreServiceImpl.resolveName(datastore));
				break;*/
			/*
			 * case "group": Group group = groupServiceImpl.findLatestByUuid(uuid); result =
			 * ow.writeValueAsString(groupServiceImpl.resolveName(group)); break;
			 */
			/*case "user":
				User user = userServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(userServiceImpl.resolveName(user));
				break;*/
			/*case "session":
				Session session = sessionServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(sessionServiceImpl.resolveName(session));
				break;*/
			/*case "activity":
				Activity activity = activityServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(activityServiceImpl.resolveName(activity));
				break;*/
			/*case "role":
				Role role = roleServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(roleServiceImpl.resolveName(role));
				break;*/
			/*case "group":
				Group group = groupServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(groupServiceImpl.resolveName(group));
				break;*/
			/*case "privilege":
				Privilege privilege = privilegeServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(privilegeServiceImpl.resolveName(privilege));
				break;*/
			/*case "dimension":
				Dimension dimension = dimensionServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(dimensionServiceImpl.resolveName(dimension));
				break;*/
			/*case "measure":
				Measure measure = measureServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(measureServiceImpl.resolveName(measure));
				break;*/
			/*case "vizexec":
				VizExec vizExec = vizExecServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(vizExecServiceImpl.resolveName(vizExec));
				break;*/
			/*case "dataset":
				Dataset dataset = datasetServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(datasetServiceImpl.resolveName(dataset));
				break;*/
			case "datasetview":
				result = ow.writeValueAsString(datasetViewServiceImpl.findLatestByUuid(uuid));
				break;
			case "dashboardview":
				result = ow.writeValueAsString(dashboardViewServiceImpl.findLatestByUuid(uuid));
				break;
			/*case "application":
				Application application = applicationServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(applicationServiceImpl.resolveName(application));
				break;*/
			/*case "datasource":
				Datasource datasource = datasourceServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(datasourceServiceImpl.resolveName(datasource));
				break;*/
			/*case "dq":
				DataQual dq = dataQualServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(dataQualServiceImpl.resolveName(dq));
				break;*/
			/*case "dqgroup":
				DataQualGroup dqGroup = dataQualGroupServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(dataQualGroupServiceImpl.resolveName(dqGroup));
				break;*/
			/*case "load":
				Load load = loadServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(loadServiceImpl.resolveName(load));
				break;*/
			/*case "dqExec":
				DataQualExec dqexec = dataQualExecServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(dataQualExecServiceImpl.resolveName(dqexec));
				break;*/
			/*case "dqgroupexec":
				DataQualGroupExec dqgroupExec = dataQualGroupExecServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(dataQualGroupExecServiceImpl.resolveName(dqgroupExec));
				break;*/
			case "dqview":
				DQView dqView = dqViewServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(dqViewServiceImpl.resolveName(dqView));
				break;
			case "ruleview":
				RuleView ruleview = ruleViewServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(ruleview);
				break;
			/*case "rule":
				Rule rule = ruleServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(ruleServiceImpl.resolveName(rule));
				break;*/
			/*case "ruleexec":
				RuleExec ruleExec = ruleExecServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(ruleExecServiceImpl.resolveName(ruleExec));
				break;*/
			/*case "rulegroup":
				RuleGroup ruleGroup = ruleGroupServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(ruleGroupServiceImpl.resolveName(ruleGroup));
				break;*/
			/*case "rulegroupexec":
				RuleGroupExec ruleGroupExec = ruleGroupExecServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(ruleGroupExecServiceImpl.resolveName(ruleGroupExec));
				break;*/
			/*case "loadexec":
				LoadExec loadExec = loadExecServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(loadExecServiceImpl.resolveName(loadExec));
				break;*/
			/*case "function":
				Function function = functionServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(functionServiceImpl.resolveName(function));
				break;*/
			/*case "profile":
				Profile profile = profileServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(profileServiceImpl.resolveName(profile));
				break;*/
			/*case "profileexec":
				ProfileExec profileExec = profileExecServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(profileExecServiceImpl.resolveName(profileExec));
				break;*/
			/*case "profilegroup":
				ProfileGroup profileGroup = profileGroupServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(profileGroupServiceImpl.resolveName(profileGroup));
				break;*/
			/*case "profilegroupexec":
				ProfileGroupExec profileGroupExec = profileGroupExecServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(profileGroupExecServiceImpl.resolveName(profileGroupExec));
				break;*/

			/*case "dashboard":
				Dashboard dashboard = dashboardServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(dashboardServiceImpl.resolveName(dashboard));
				break;*/
			/*case "algorithm":
				Algorithm algorithm = algorithmServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(algorithmServiceImpl.resolveName(algorithm));
				break;*/
			/*case "model":
				Model model = modelServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(modelServiceImpl.resolveName(model));
				break;*/
			/*case "modelexec":
				ModelExec modelExec = modelExecServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(modelExecServiceImpl.resolveName(modelExec));
				break;*/
			/*case "paramlist":
				ParamList paralist = paramListServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(paramListServiceImpl.resolveName(paralist));
				break;*/
			/*case "paramset":
				ParamSet paramset = paramSetServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(paramSetServiceImpl.resolveName(paramset));
				break;*/
			/*case "mapexec":
				MapExec mapExec = mapExecServiceImpl.findLatestByUuid(uuid);
				result = ow.writeValueAsString(mapExecServiceImpl.resolveName(mapExec));
				break;*/
			case "reportview":
				result = ow.writeValueAsString(reportViewServiceImpl.findLatestByUuid(uuid));
				break;
			}

		}

		return result;
	}

	/********************** UNUSED **********************/
	/*public String getAllByUuid(String uuid, String type) throws JsonProcessingException {

		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			case "relation":
				result = ow.writeValueAsString(relationServiceImpl.findAllByUuid(uuid));
				break;
			case "datapod":
				result = ow.writeValueAsString(datapodServiceImpl.findAllByUuid(uuid));
				break;
			case "dag":
				result = ow.writeValueAsString(dagServiceImpl.findAllByUuid(uuid));
				break;
			case "filter":
				result = ow.writeValueAsString(filterServiceImpl.findAllByUuid(uuid));
				break;
			case "expression":
				result = ow.writeValueAsString(expressionServiceImpl.findAllByUuid(uuid));
				break;
			case "formula":
				result = ow.writeValueAsString(formulaServiceImpl.findAllByUuid(uuid));
				break;
			case "map":
				result = ow.writeValueAsString(mapServiceImpl.findAllByUuid(uuid));
				break;
			case "dagExec":
				result = ow.writeValueAsString(dagExecServiceImpl.findAllByUuid(uuid));
				break;
			case "condition":
				result = ow.writeValueAsString(conditionServiceImpl.findAllByUuid(uuid));
				break;
			case "meta":
				result = ow.writeValueAsString(metadataServiceImpl.findAllByUuid(uuid));
				break;
			case "vizpod":
				result = ow.writeValueAsString(vizpodServiceImpl.findAllByUuid(uuid));
				break;
			case "datastore":
				result = ow.writeValueAsString(dataStoreServiceImpl.findAllByUuid(uuid));
				break;
			
			 * case "group": result =
			 * ow.writeValueAsString(groupServiceImpl.findAllByUuid(uuid)); break;
			 
			case "user":
				result = ow.writeValueAsString(userServiceImpl.findAllByUuid(uuid));
				break;
			case "session":
				result = ow.writeValueAsString(sessionServiceImpl.findAllByUuid(uuid));
				break;
			case "activity":
				result = ow.writeValueAsString(activityServiceImpl.findAllByUuid(uuid));
				break;
			case "role":
				result = ow.writeValueAsString(roleServiceImpl.findAllByUuid(uuid));
				break;
			case "group":
				result = ow.writeValueAsString(groupServiceImpl.findAllByUuid(uuid));
				break;
			case "privilege":
				result = ow.writeValueAsString(privilegeServiceImpl.findAllByUuid(uuid));
				break;
			case "dimension":
				result = ow.writeValueAsString(dimensionServiceImpl.findAllByUuid(uuid));
				break;
			case "measure":
				result = ow.writeValueAsString(measureServiceImpl.findAllByUuid(uuid));
				break;
			case "dataset":
				result = ow.writeValueAsString(datasetServiceImpl.findAllByUuid(uuid));
				break;
			case "application":
				result = ow.writeValueAsString(applicationServiceImpl.findAllByUuid(uuid));
				break;
			case "datasource":
				result = ow.writeValueAsString(datasourceServiceImpl.findAllByUuid(uuid));
				break;
			case "dq":
				result = ow.writeValueAsString(dataQualServiceImpl.findAllByUuid(uuid));
				break;
			case "dqgroup":
				result = ow.writeValueAsString(dataQualGroupServiceImpl.findAllByUuid(uuid));
				break;
			case "dqExec":
				result = ow.writeValueAsString(dataQualExecServiceImpl.findAllByUuid(uuid));
				break;
			case "dqgroupexec":
				result = ow.writeValueAsString(dataQualGroupExecServiceImpl.findAllByUuid(uuid));
				break;
			case "rule":
				result = ow.writeValueAsString(ruleServiceImpl.findAllByUuid(uuid));
				break;
			case "rulegroup":
				result = ow.writeValueAsString(ruleGroupServiceImpl.findAllByUuid(uuid));
				break;
			case "loadexec":
				result = ow.writeValueAsString(loadExecServiceImpl.findAllByUuid(uuid));
				break;
			case "function":
				result = ow.writeValueAsString(functionServiceImpl.findAllByUuid(uuid));
				break;
			case "profile":
				result = ow.writeValueAsString(profileServiceImpl.findAllByUuid(uuid));
				break;
			case "profileexec":
				result = ow.writeValueAsString(profileExecServiceImpl.findAllByUuid(uuid));
				break;
			case "profilegroup":
				result = ow.writeValueAsString(profileGroupServiceImpl.findAllByUuid(uuid));
				break;
			case "profilegroupexec":
				result = ow.writeValueAsString(profileGroupExecServiceImpl.findAllByUuid(uuid));
				break;

			case "dashboard":
				result = ow.writeValueAsString(dashboardServiceImpl.findAllByUuid(uuid));
				break;
			case "algorithm":
				result = ow.writeValueAsString(algorithmServiceImpl.findAllByUuid(uuid));
				break;
			case "model":
				result = ow.writeValueAsString(modelServiceImpl.findAllByUuid(uuid));
				break;
			case "modelexec":
				result = ow.writeValueAsString(modelServiceImpl.findAllByUuid(uuid));
				break;
			case "paramlist":
				result = ow.writeValueAsString(paramListServiceImpl.findAllByUuid(uuid));
				break;
			case "paramset":
				result = ow.writeValueAsString(paramSetServiceImpl.findAllByUuid(uuid));
				break;
			}
		}
		return result;
	}
*/

	/********************** UNUSED **********************/
	/*public String test(String param1, String param2, String type) throws JsonProcessingException {

		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			case "relation":
				result = ow.writeValueAsString(relationServiceImpl.test(param1));
				break;
			case "datapod":
				result = ow.writeValueAsString(datapodServiceImpl.test(param1));
				break;
			case "dag":
				result = ow.writeValueAsString(dagServiceImpl.test(param1));
				break;
			case "filter":
				result = ow.writeValueAsString(filterServiceImpl.test(param1));
				break;
			case "expression":
				result = ow.writeValueAsString(expressionServiceImpl.test(param1));
				break;
			case "formula":
				result = ow.writeValueAsString(formulaServiceImpl.test(param1));
				break;
			case "map":
				result = ow.writeValueAsString(mapServiceImpl.test(param1));
				break;
			case "dagExec":
				result = ow.writeValueAsString(dagExecServiceImpl.test(param1, param2));
				break;
			case "condition":
				result = ow.writeValueAsString(conditionServiceImpl.test(param1));
				break;
			case "meta":
				result = ow.writeValueAsString(metadataServiceImpl.test(param1));
				break;
			case "vizpod":
				result = ow.writeValueAsString(vizpodServiceImpl.test(param1));
				break;
			case "datastore":
				result = ow.writeValueAsString(dataStoreServiceImpl.test(param1));
				break;
			
			 * case "group": result = ow.writeValueAsString(groupServiceImpl.test(param1));
			 * break;
			 
			case "user":
				result = ow.writeValueAsString(userServiceImpl.test(param1));
				break;
			case "session":
				result = ow.writeValueAsString(sessionServiceImpl.test(param1));
				break;
			case "activity":
				result = ow.writeValueAsString(activityServiceImpl.test(param1));
				break;
			case "dataset":
				result = ow.writeValueAsString(datasetServiceImpl.test(param1));
				break;
			}
		}

		return result;
	}
*/
	public String getDagExecStatus(String uuid, String version) throws JsonProcessingException {

		String result = null;
		String statusValue = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(dagExecServiceImpl.findDagExecStatus(uuid, version));
		String modifiedList = result.substring(1, result.length() - 1);

		try {
			JSONObject jsonObj = new JSONObject(modifiedList);
			JSONArray jsonArray = (JSONArray) jsonObj.get("status");
			for (int i = 0; i < jsonArray.length(); i++) {
				if (i > 0) {
					if (jsonArray.getJSONObject(i).getLong("createdOn") > jsonArray.getJSONObject(i - 1)
							.getLong("createdOn")) {
						statusValue = jsonArray.getJSONObject(i).getString("stage");

					} else if (jsonArray.getJSONObject(i).getLong("createdOn") < jsonArray.getJSONObject(i - 1)
							.getLong("createdOn")) {
						statusValue = jsonArray.getJSONObject(i - 1).getString("stage");
					}
				} else {
					statusValue = jsonArray.getJSONObject(i).getString("stage");
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return statusValue;
	}

	public String getDagExecStageStatus(String uuid, String version, String stageId) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(dagExecServiceImpl.findDagExecStageStatus(uuid, version, stageId));
		return statusForStageID(result, stageId);

	}

	public String getDagExecTaskStatus(String uuid, String version, String taskId) throws JsonProcessingException {
		String list = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		list = ow.writeValueAsString(dagExecServiceImpl.findDagExecTaskStatus(uuid, version, taskId));
		return statusFortaskID(list, taskId);
	}

	private String statusFortaskID(String list, String taskId) {
		String statusValue = null;
		String modifiedList = list.substring(1, list.length() - 1);
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(modifiedList);
			JSONArray jsonArray = (JSONArray) jsonObj.get("status");
			for (int i = 0; i < jsonArray.length(); i++) {
				statusValue = jsonArray.getJSONObject(i).getString("stage");
				// .concat(jsonArray.getJSONObject(i).getString("createdOn"));
				if (i > 0) {
					if (jsonArray.getJSONObject(i).getLong("createdOn") > jsonArray.getJSONObject(i - 1)
							.getLong("createdOn")) {
						statusValue = jsonArray.getJSONObject(i).getString("stage");
					} else if (jsonArray.getJSONObject(i).getLong("createdOn") < jsonArray.getJSONObject(i - 1)
							.getLong("createdOn")) {
						statusValue = jsonArray.getJSONObject(i - 1).getString("stage");
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();

		}
		return statusValue;
	}

	private String statusForStageID(String result, String stageId) {
		String statusValue = null;
		String modifiedList = result.substring(1, result.length() - 1);
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(modifiedList);

			JSONArray jsonArray = (JSONArray) jsonObj.get("status");
			for (int j = 0; j < jsonArray.length(); j++) {
				statusValue = jsonArray.getJSONObject(j).getString("stage");
				if (j > 0) {
					if (jsonArray.getJSONObject(j).getLong("createdOn") > jsonArray.getJSONObject(j - 1)
							.getLong("createdOn")) {
						statusValue = jsonArray.getJSONObject(j).getString("stage");
					} else if (jsonArray.getJSONObject(j).getLong("createdOn") < jsonArray.getJSONObject(j - 1)
							.getLong("createdOn")) {
						statusValue = jsonArray.getJSONObject(j - 1).getString("stage");
					}
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return statusValue;
	}

	public List<AttributeRefHolder> getAttributesByRelation(String relationUuid, String type)
			throws JSONException, JsonProcessingException {
		List<AttributeRefHolder> attrRefDetails = null;
		List<String> objectUuidList = new ArrayList<String>();
		Relation relationDetails = (Relation) commonServiceImpl.getLatestByUuid(relationUuid,
				MetaType.relation.toString());
		if (relationDetails.getRelationInfo().size() > 0) {
			for (int i = 0; i < relationDetails.getRelationInfo().size(); i++) {
				String joinInfoUUId = relationDetails.getRelationInfo().get(i).getJoin().getRef().getUuid();
				objectUuidList.add(joinInfoUUId);
			}
		}
		attrRefDetails = new ArrayList<AttributeRefHolder>();
		logger.info("datapoduuid : " + objectUuidList);
		if (type.equalsIgnoreCase("relation")) {
			
			if (relationDetails.getRelationInfo().size() > 0) {
				for (int i = 0; i < relationDetails.getRelationInfo().size(); i++) {
					for (int j = 0; j < relationDetails.getRelationInfo().get(i).getJoinKey().size(); j++) {
						for (int k = 0; k < relationDetails.getRelationInfo().get(i).getJoinKey().get(j).getOperand()
								.size(); k++) {
							String refUUID = relationDetails.getRelationInfo().get(i).getJoinKey().get(j).getOperand()
									.get(k).getRef().getUuid();
							String refType = relationDetails.getRelationInfo().get(i).getJoinKey().get(j).getOperand()
									.get(k).getRef().getType().toString();
							if (refType.equalsIgnoreCase(MetaType.datapod.toString())) {
								Datapod datapodDet = (Datapod) commonServiceImpl.getLatestByUuid(refUUID, refType);
								MetaIdentifier ref = new MetaIdentifier();
								ref.setName(datapodDet.getName());
								ref.setUuid(datapodDet.getUuid());
								ref.setVersion(datapodDet.getVersion());
								ref.setType(MetaType.datapod);
								List<Attribute> listAttributes = datapodDet.getAttributes();
								for (Attribute attr : listAttributes) {
									AttributeRefHolder attributeRefTemp = new AttributeRefHolder();
									attributeRefTemp.setAttrId(Integer.toString((attr.getAttributeId())));
									attributeRefTemp
											.setAttrType(datapodDet.getAttribute(attr.getAttributeId()).getType());
									if (datapodDet.getAttribute(attr.getAttributeId()).getDispName() != null) {
										attributeRefTemp.setAttrName(
												datapodDet.getAttribute(attr.getAttributeId()).getDispName());
									} else
										attributeRefTemp
												.setAttrName(datapodDet.getAttribute(attr.getAttributeId()).getName());
									attributeRefTemp.setRef(ref);
									attrRefDetails.add(attributeRefTemp);
								}
							} else if (refType.equalsIgnoreCase(MetaType.dataset.toString())) {
								DataSet datasetDet = (DataSet) commonServiceImpl.getLatestByUuid(refUUID, refType);
								MetaIdentifier ref = new MetaIdentifier();
								ref.setName(datasetDet.getName());
								ref.setUuid(datasetDet.getUuid());
								ref.setVersion(datasetDet.getVersion());
								ref.setType(MetaType.dataset);
								List<AttributeSource> listAttributes = datasetDet.getAttributeInfo();
								for (AttributeSource attr : listAttributes) {
									AttributeRefHolder attributeRef = new AttributeRefHolder();
									attributeRef.setAttrId((attr.getAttrSourceId()));
									attributeRef.setAttrType(attr.getSourceAttr().getAttrType());
									attributeRef.setAttrName(attr.getAttrSourceName());
									attributeRef.setRef(ref);
									attrRefDetails.add(attributeRef);
								}

							}

						}
					}
				}
			}
		}else {
			if (relationDetails.getRelationInfo().size() > 0) {
				for (int i = 0; i < relationDetails.getRelationInfo().size(); i++) {
					for (int j = 0; j < relationDetails.getRelationInfo().get(i).getJoinKey().size(); j++) {
						for (int k = 0; k < relationDetails.getRelationInfo().get(i).getJoinKey().get(j).getOperand()
								.size(); k++) {

							AttributeRefHolder attributeRef = new AttributeRefHolder();
							MetaIdentifier ref = new MetaIdentifier();
							String refUUID = relationDetails.getRelationInfo().get(i).getJoinKey().get(j).getOperand()
									.get(k).getRef().getUuid();
							String refType = relationDetails.getRelationInfo().get(i).getJoinKey().get(j).getOperand()
									.get(k).getRef().getType().toString();
							if (refType.equalsIgnoreCase(MetaType.datapod.toString())) {
								Datapod datapodDet = (Datapod) commonServiceImpl.getLatestByUuid(refUUID, refType);
								Integer AttrId = relationDetails.getRelationInfo().get(i).getJoinKey().get(j)
										.getOperand().get(k).getAttributeId();
								// if (objectUuidList.contains(refUUID)) {
								ref.setType(MetaType.datapod);
								ref.setUuid(datapodDet.getUuid());
								ref.setVersion(datapodDet.getVersion());
								ref.setName(datapodDet.getName());
								Attribute attr = datapodDet.getAttribute(AttrId);
								if (attr != null) {
									attributeRef.setAttrId(Integer.toString((AttrId)));
									attributeRef.setAttrType(datapodDet.getAttribute(AttrId).getType());
									if (datapodDet.getAttribute(AttrId).getDispName() != null) {
										attributeRef.setAttrName(datapodDet.getAttribute(AttrId).getDispName());
									} else
										attributeRef.setAttrName(datapodDet.getAttribute(AttrId).getName());
									attributeRef.setRef(ref);
									attrRefDetails.add(attributeRef);
								}

								// }
							} else if (refType.equalsIgnoreCase(MetaType.dataset.toString())) {
								DataSet datasetDet = (DataSet) commonServiceImpl.getLatestByUuid(refUUID, refType);
								Integer AttrId = relationDetails.getRelationInfo().get(i).getJoinKey().get(j)
										.getOperand().get(k).getAttributeId();
								// if (objectUuidList.contains(refUUID)) {
								ref.setType(MetaType.dataset);
								ref.setUuid(datasetDet.getUuid());
								ref.setVersion(datasetDet.getVersion());
								ref.setName(datasetDet.getName());

								AttributeSource attrSource = datasetDet.getAttributeInfo().get(AttrId);
								MetaIdentifier attr = attrSource.getSourceAttr().getRef();

								if (attr != null) {
									attributeRef.setAttrId(Integer.toString((AttrId)));
									attributeRef.setAttrType(
											datasetDet.getAttributeInfo().get(AttrId).getSourceAttr().getAttrType());
									attributeRef.setAttrName(attrSource.getAttrSourceName());

									attributeRef.setRef(ref);
									attrRefDetails.add(attributeRef);
								}

								// }

							}
						}
					}
				}
			}
		}
		logger.info("finalMap is " + attrRefDetails);
		return attrRefDetails;
	}

	/********************** UNUSED **********************/
	// Find Formula by given relation uuid
	/*public String getFormulaByRelation(String relationUuid, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase("formula")) {
			result = ow.writeValueAsString(formulaServiceImpl.findFormulaByRelation(relationUuid));
		}

		return result;
	}*/

	/********************** UNUSED **********************/
	// Find expression by given relation uuid
	/*public String getExpressionByRelation(String relationUuid, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase("expression")) {
			result = ow.writeValueAsString(expressionServiceImpl.findExpressionByRelation(relationUuid));
		}
		return result;
	}*/

	// Find vizpod by given relation uuid
	public String getVizpodByRelation(String relationUUID, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase(MetaType.vizpod.toString())) {
			result = ow.writeValueAsString(vizpodServiceImpl.findVizpodByRelation(relationUUID));
		}

		return result;
	}

	// public List<Map<String, Object>> createGraph(Datapod document, String
	// type, String action){
	/*
	 * public String submit(Map<String, Object> document, String type) { String Id =
	 * ""; Datapod datapodDet = null;
	 * 
	 * if (type != null && !type.isEmpty()) { ObjectMapper mapper = new
	 * ObjectMapper(); type = type.toLowerCase(); // Create graph start//
	 * 
	 * if (type.equalsIgnoreCase("datapod")) { Datapod datapod =
	 * mapper.convertValue(document, Datapod.class); Id = datapodServiceImpl.Save
	 * case "datapod": result =
	 * ow.writeValueAsString(datapodServiceImpl.findAllByVersion(uuid)); break; case
	 * "relation": result =
	 * ow.writeValueAsString(relationServiceImpl.findAllByVersion(uuid)); break;
	 * case "activity": result =
	 * ow.writeValueAsString(activityServiceImpl.findAllByVersion(uuid)); break;
	 * case "application": result =
	 * ow.writeValueAsString(applicationServiceImpl.findAllByVersion(uuid)); break;
	 * case "condition": result =
	 * ow.writeValueAsString(conditionServiceImpl.findAllByVersion(uuid)); break;
	 * case "dagExec": result =
	 * ow.writeValueAsString(dagExecServiceImpl.findAllByVersion(uuid)); break; case
	 * "dag": result = ow.writeValueAsString(dagServiceImpl.findAllByVersion(uuid));
	 * break; case "dashboard": result =
	 * ow.writeValueAsString(dashboardSeviceImpl.findAllByVersion(uuid)); break;
	 * case "dataset": result =
	 * ow.writeValueAsString(datasetServiceImpl.findAllByVersion(uuid)); break; case
	 * "datasource": result =
	 * ow.writeValueAsString(datasourceServiceImpl.findAllByVersion(uuid)); break;
	 * case "datastore": result =
	 * ow.writeValueAsString(dataStoreServiceImpl.findAllByVersion(uuid)); break;
	 * case "dimension": result =
	 * ow.writeValueAsString(dimensionServiceImpl.findAllByVersion(uuid)); break;
	 * case "expression": result =
	 * ow.writeValueAsString(expressionServiceImpl.findAllByVersion(uuid)); break;
	 * case "filter": result =
	 * ow.writeValueAsString(filterServiceImpl.findAllByVersion(uuid)); break; case
	 * "formula": result =
	 * ow.writeValueAsString(formulaServiceImpl.findAllByVersion(uuid)); break; case
	 * "group": result =
	 * ow.writeValueAsString(groupServiceImpl.findAllByVersion(uuid)); break; case
	 * "load": result =
	 * ow.writeValueAsString(loadServiceImpl.findAllByVersion(uuid)); break; case
	 * "map": result = ow.writeValueAsString(mapServiceImpl.findAllByVersion(uuid));
	 * break; case "measure": result =
	 * ow.writeValueAsString(measureServiceImpl.findAllByVersion(uuid)); break; case
	 * "metadata": result =
	 * ow.writeValueAsString(metadataServiceImpl.findAllByVersion(uuid)); break;
	 * case "privilege": result =
	 * ow.writeValueAsString(privilegeServiceImpl.findAllByVersion(uuid)); break;
	 * case "role": result =
	 * ow.writeValueAsString(roleServiceImpl.findAllByVersion(uuid)); break; case
	 * "ruleExec": result =
	 * ow.writeValueAsString(ruleExecServiceImpl.findAllByVersion(uuid)); break;
	 * case "ruleGroup": result =
	 * ow.writeValueAsString(ruleGroupServiceImpl.findAllByVersion(uuid)); break;
	 * case "rule": result =
	 * ow.writeValueAsString(ruleServiceImpl.findAllByVersion(uuid)); break; case
	 * "session": result =
	 * ow.writeValueAsString(sessionServiceImpl.findAllByVersion(uuid)); break; case
	 * "userGroup": result =
	 * ow.writeValueAsString(userGroupServiceImpl.findAllByVersion(uuid)); break;
	 * case "user": result =
	 * ow.writeValueAsString(userServiceImpl.findAllByVersion(uuid)); break; case
	 * "vizExec": result =
	 * ow.writeValueAsString(vizExecServiceImpl.findAllByVersion(uuid)); break; case
	 * "vizpod": result =
	 * ow.writeValueAsString(vizpodServiceImpl.findAllByVersion(uuid)); break;
	 * (datapod).getId(); datapodDet = datapodServiceImpl.Save(datapod); createDet =
	 * graphServiceImpl.submit(datapodDet);
	 * 
	 * }
	 * 
	 * } return Id; }
	 */

	/*public String getGraphResults(String uuid,String version, String degree) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return graphServiceImpl.getGraphJson(uuid,version,degree);
	}*/
	
	public String getGraphResults(String uuid,String version, String degree) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		//return graphServiceImpl.getGraphJson(uuid,version,degree);
		return mongoGraphServiceImpl.getGraphJson(uuid,version,degree);

	}
	public String getTreeGraphResults(String uuid,String version, String degree) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		//return graphServiceImpl.getGraphJson(uuid,version,degree);
		return mongoGraphServiceImpl.getTreeGraphJson(uuid,version,degree);

	}
	
	/* public String getGraphJson() { 
		 return mongoGraphServiceImpl.getGraphJson();
	 
	 
	 }
*/
//	public String getOneByUuid(String uuid, String type) {
//		return graphServiceImpl.getOneByUuid(uuid, type);
//
//	}

	// public String getOneByUuid(String uuid, String type) {
	// return graphServiceImpl.getOneByUuid(uuid, type);
	//
	// }

	/********************** UNUSED **********************/
	/*public String getDagByDatapod(String datapodUUID) throws JsonProcessingException {
		// String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		return ow.writeValueAsString(dagServiceImpl.findDagbyDatapod(datapodUUID));
	}*/

	/*
	 * public void getAllForGraph(String type) { List<Datapod> getAllForDatapod =
	 * new ArrayList<>(); String result = null; if(type.equalsIgnoreCase("datapod"))
	 * { //case "datapod": getAllForDatapod = datapodServiceImpl.findAll(); for(int
	 * i =0;i<getAllForDatapod.size(); i++){ saveGraph(getAllForDatapod.get(i));
	 * datapodResult.add(result); }
	 * 
	 * }
	 * 
	 * logger.info("print result :: " + result);
	 * 
	 * }
	 */

	/*
	 * public String saveGraph(Datapod datapod) { String Id = ""; Datapod datapodDet
	 * = null; // Create graph start//
	 * 
	 * Id = datapodServiceImpl.Save(datapod).getId(); datapodDet =
	 * datapodServiceImpl.Save(datapod); createDet =
	 * graphServiceImpl.submit(datapodDet);
	 * 
	 * return Id; }
	 */

	/********************** UNUSED **********************/
	/*public String getDimByType(String typeUUID, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty() && type.equalsIgnoreCase("dimension")) {
			result = ow.writeValueAsString(dimensionServiceImpl.findDimByType(typeUUID));
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public String getDimInfoByRelation(String relationUUID) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(dimensionServiceImpl.findDimInfoByRelation(relationUUID));

		return result;
	}*/

	/********************** UNUSED **********************/
	/*public String getMeasureInfoByRelation(String relationUUID) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(measureServiceImpl.findMeasureInfoByRelation(relationUUID));
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifier getCreatedUserName(MetaIdentifier metaIdentifier) {
		String userUUID = metaIdentifier.getUuid();
		User userDO = userServiceImpl.findLatestByUuid(userUUID);
		String userName = userDO.getName();
		metaIdentifier.setName(userName);
		return metaIdentifier;
	}*/

	/********************** UNUSED **********************/
	/*public String getAppBySession(String userUuid) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(userServiceImpl.findAppBySession(userUuid));
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public String getRoleBySession(String userUuid) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(userServiceImpl.findRoleBySession(userUuid));
		return result;
	}*/

	public String getAppByUser(String userName)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		/*
		 * User user = userServiceImpl.findUserByName(userName); if(user!= null) {
		 * List<MetaIdentifierHolder> groupInfoList = user.getGroupInfo();
		 * if(groupInfoList != null && !groupInfoList.isEmpty()) {
		 * List<MetaIdentifierHolder> roleIdList = new ArrayList<>();
		 * List<MetaIdentifierHolder> appIdList = new ArrayList<>();
		 * for(MetaIdentifierHolder groupInfo : groupInfoList) { String groupUuid =
		 * groupInfo.getRef().getUuid(); String groupVersion =
		 * groupInfo.getRef().getUuid(); MetaType type = groupInfo.getRef().getType();
		 * Group group = (Group)
		 * commonServiceImpl.getLatestByUuidWithoutAppUuid(groupUuid, type.toString());
		 * roleIdList.add(group.getRoleId()); appIdList.add(group.getAppId()); }
		 * logger.info("AppRole ->--->> "+ow.writeValueAsString(new AppRole(roleIdList,
		 * appIdList))); return ow.writeValueAsString(new AppRole(roleIdList,
		 * appIdList)); }else
		 * logger.info("No group informaion available, groupInfo is empty/null.");
		 * return "No group informaion available, groupInfo is empty/null."; }else
		 * logger.info("User object null."); return "User object null.";
		 */
		userServiceImpl.findAppByUser(userName);
		result = ow.writeValueAsString(userServiceImpl.findAppByUser(userName));
		return result;
	}

	/********************** UNUSED **********************/
	/*public String getRoleByUser(String userName) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(userServiceImpl.findRoleByUser(userName));
		return result;
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public String getAllVersion(String uuid, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			case "datapod":
				result = ow.writeValueAsString(datapodServiceImpl.findAllByVersion(uuid));
				break;
			case "relation":
				result = ow.writeValueAsString(relationServiceImpl.findAllByVersion(uuid));
				break;
			case "activity":
				result = ow.writeValueAsString(activityServiceImpl.findAllByVersion(uuid));
				break;
			case "application":
				result = ow.writeValueAsString(applicationServiceImpl.findAllByVersion(uuid));
				break;
			case "condition":
				result = ow.writeValueAsString(conditionServiceImpl.findAllByVersion(uuid));
				break;
			case "dagExec":
				result = ow.writeValueAsString(dagExecServiceImpl.findAllByVersion(uuid));
				break;
			case "dag":
				result = ow.writeValueAsString(dagServiceImpl.findAllByVersion(uuid));
				break;
			case "dashboard":
				result = ow.writeValueAsString(dashboardServiceImpl.findAllByVersion(uuid));
				break;
			case "dataset":
				result = ow.writeValueAsString(datasetServiceImpl.findAllByVersion(uuid));
				break;
			case "datasource":
				result = ow.writeValueAsString(datasourceServiceImpl.findAllByVersion(uuid));
				break;
			case "datastore":
				result = ow.writeValueAsString(dataStoreServiceImpl.findAllByVersion(uuid));
				break;
			case "dimension":
				result = ow.writeValueAsString(dimensionServiceImpl.findAllByVersion(uuid));
				break;
			case "expression":
				result = ow.writeValueAsString(expressionServiceImpl.findAllByVersion(uuid));
				break;
			case "filter":
				result = ow.writeValueAsString(filterServiceImpl.findAllByVersion(uuid));
				break;
			case "formula":
				result = ow.writeValueAsString(formulaServiceImpl.findAllByVersion(uuid));
				break;
			
			 * case "group": result =
			 * ow.writeValueAsString(groupServiceImpl.findAllByVersion(uuid)); break;
			 
			case "load":
				result = ow.writeValueAsString(loadServiceImpl.findAllByVersion(uuid));
				break;
			case "map":
				result = ow.writeValueAsString(mapServiceImpl.findAllByVersion(uuid));
				break;
			case "measure":
				result = ow.writeValueAsString(measureServiceImpl.findAllByVersion(uuid));
				break;
			case "metadata":
				result = ow.writeValueAsString(metadataServiceImpl.findAllByVersion(uuid));
				break;
			case "privilege":
				result = ow.writeValueAsString(privilegeServiceImpl.findAllByVersion(uuid));
				break;
			case "role":
				result = ow.writeValueAsString(roleServiceImpl.findAllByVersion(uuid));
				break;
			case "session":
				result = ow.writeValueAsString(sessionServiceImpl.findAllByVersion(uuid));
				break;
			case "group":
				result = ow.writeValueAsString(groupServiceImpl.findAllByVersion(uuid));
				break;
			case "user":
				result = ow.writeValueAsString(userServiceImpl.findAllByVersion(uuid));
				break;
			case "vizexec":
				result = ow.writeValueAsString(vizExecServiceImpl.findAllByVersion(uuid));
				break;
			case "vizpod":
				result = ow.writeValueAsString(vizpodServiceImpl.findAllByVersion(uuid));
				break;
			case "dq":
				result = ow.writeValueAsString(dataQualServiceImpl.findAllByVersion(uuid));
				break;
			case "dqgroup":
				result = ow.writeValueAsString(dataQualGroupServiceImpl.findAllByVersion(uuid));
				break;

			case "rulegroup":
				result = ow.writeValueAsString(ruleGroupServiceImpl.findAllByVersion(uuid));
				break;

			case "dqExec":
				result = ow.writeValueAsString(dataQualExecServiceImpl.findAllByVersion(uuid));
				break;
			case "dqgroupexec":
				result = ow.writeValueAsString(dataQualGroupExecServiceImpl.findAllByVersion(uuid));
				break;
			case "rule":
				result = ow.writeValueAsString(ruleServiceImpl.findAllByVersion(uuid));
				break;
			case "ruleexec":
				result = ow.writeValueAsString(ruleExecServiceImpl.findAllByVersion(uuid));
				break;
			case "rulegroupexec":
				result = ow.writeValueAsString(ruleGroupExecServiceImpl.findAllByVersion(uuid));
				break;
			case "loadexec":
				result = ow.writeValueAsString(loadExecServiceImpl.findAllByVersion(uuid));
				break;
			case "function":
				result = ow.writeValueAsString(functionServiceImpl.findAllByVersion(uuid));
				break;
			case "profile":
				result = ow.writeValueAsString(profileServiceImpl.findAllByVersion(uuid));
				break;
			case "profileexec":
				result = ow.writeValueAsString(profileExecServiceImpl.findAllByVersion(uuid));
				break;
			case "profilegroup":
				result = ow.writeValueAsString(profileGroupServiceImpl.findAllByVersion(uuid));
				break;
			case "profilegroupexec":
				result = ow.writeValueAsString(profileGroupExecServiceImpl.findAllByVersion(uuid));
				break;
			case "algorithm":
				result = ow.writeValueAsString(algorithmServiceImpl.findAllByVersion(uuid));
				break;
			case "model":
				result = ow.writeValueAsString(modelServiceImpl.findAllByVersion(uuid));
				break;
			case "modelexec":
				result = ow.writeValueAsString(modelExecServiceImpl.findAllByVersion(uuid));
				break;
			case "paramlist":
				result = ow.writeValueAsString(paramListServiceImpl.findAllByVersion(uuid));
				break;
			case "paramset":
				result = ow.writeValueAsString(paramSetServiceImpl.findAllByVersion(uuid));
				break;

			}

		}
		return result;
	}
*/
	// public List<AttributeRef> getAttributesByDataset(String uuid) {
	// List<AttributeRef> attrRefDetails = new ArrayList<AttributeRef>();
	// Datapod datapod = null;
	// MetaIdentifier finalDataRef = new MetaIdentifier();
	// Dataset dataset = datasetServiceImpl.findLatestByUuid(uuid);
	// List<SourceAttribute> sourceAttributes = dataset.getSourceAttributes();
	// for (int i = 0; i < sourceAttributes.size(); i++) {
	// AttributeRef attributeRef = new AttributeRef();
	// if(sourceAttributes.get(i).getSourceAttr().getRef().getType() ==
	// MetaType.datapod)
	// {
	// finalDataRef.setType(MetaType.datapod);
	// datapod =
	// datapodServiceImpl.findLatestByUuid(sourceAttributes.get(i).getSourceAttr().getRef().getUuid());
	// finalDataRef.setUuid(datapod.getUuid());
	// finalDataRef.setVersion(datapod.getVersion());
	// finalDataRef.setName(datapod.getName());
	// attributeRef.setAttributeId(Integer.parseInt(sourceAttributes.get(i).getAttrSourceId()));
	// attributeRef.setAttributeName(datapod.getAttributes().get(i).getName());
	// attributeRef.setAttributeDesc(sourceAttributes.get(i).getName());
	// attributeRef.setDatapodRef(finalDataRef);
	// attrRefDetails.add(attributeRef);
	// }
	// }
	// return attrRefDetails;
	// }

	public List<AttributeRefHolder> getAttributesByDataset(String uuid) throws JsonProcessingException {
		List<AttributeRefHolder> attrRefDetails = new ArrayList<AttributeRefHolder>();
		MetaIdentifier finalDataRef = new MetaIdentifier();
		//Dataset dataset = datasetServiceImpl.findLatestByUuid(uuid);
		DataSet dataset = (DataSet) commonServiceImpl.getLatestByUuid(uuid,  MetaType.dataset.toString());
		List<AttributeSource> sourceAttributes = dataset.getAttributeInfo();
		for (int i = 0; i < sourceAttributes.size(); i++) {
			AttributeRefHolder attributeRef = new AttributeRefHolder();
			finalDataRef.setType(MetaType.dataset);
			finalDataRef.setUuid(dataset.getUuid());
			finalDataRef.setVersion(dataset.getVersion());
			finalDataRef.setName(dataset.getName());
			attributeRef.setAttrId(sourceAttributes.get(i).getAttrSourceId());
			attributeRef.setAttrName(dataset.getAttributeInfo().get(i).getAttrSourceName());
			if(sourceAttributes.get(i).getSourceAttr().getAttrType()!=null) {
				attributeRef.setAttrType(sourceAttributes.get(i).getSourceAttr().getAttrType());
			}else {
				attributeRef.setAttrType("string");
			}
		
			// attributeRef.setAttrDesc(sourceAttributes.get(i).getName());
			attributeRef.setRef(finalDataRef);
			attrRefDetails.add(attributeRef);
		}
		return attrRefDetails;
	}

	public List<AttributeRefHolder> getAttributesByRule(String uuid) throws JsonProcessingException {
		List<AttributeRefHolder> attrRefDetails = new ArrayList<AttributeRefHolder>();
		MetaIdentifier finalDataRef = new MetaIdentifier();
		//Rule rule = ruleServiceImpl.findLatestByUuid(uuid);
		Rule rule = (Rule) commonServiceImpl.getLatestByUuid(uuid,  MetaType.rule.toString());
		List<AttributeSource> sourceAttributes = rule.getAttributeInfo();
		for (int i = 0; i < sourceAttributes.size(); i++) {
			AttributeRefHolder attributeRef = new AttributeRefHolder();
			finalDataRef.setType(MetaType.rule);
			finalDataRef.setUuid(rule.getUuid());
			finalDataRef.setVersion(rule.getVersion());
			finalDataRef.setName(rule.getName());
			attributeRef.setAttrId(sourceAttributes.get(i).getAttrSourceId());
			attributeRef.setAttrName(rule.getAttributeInfo().get(i).getAttrSourceName());
			//attributeRef.setAttrType(rule.getAttributeInfo().get(i).getSourceAttr().getAttrType());
			//attributeRef.setAttrName(sourceAttributes.get(i).getSourceAttr().getAttrType());
			// attributeRef.setAttrDesc(sourceAttributes.get(i).getName());
			attributeRef.setRef(finalDataRef);
			attrRefDetails.add(attributeRef);
		}
		return attrRefDetails;
	}

	public List<AttributeRefHolder> getAttributesByDatapod(String uuid) throws JsonProcessingException {
		List<AttributeRefHolder> attrRefDetails = new ArrayList<AttributeRefHolder>();
		Datapod datapod = null;
		// AttributeRef attributeRef = new AttributeRef();
		MetaIdentifier finalDataRef = new MetaIdentifier();
		//datapod = datapodServiceImpl.findLatestByUuid(uuid);
		datapod = (Datapod) commonServiceImpl.getLatestByUuid(uuid, MetaType.datapod.toString());
		for (int i = 0; i < datapod.getAttributes().size(); i++) {
			AttributeRefHolder attributeRef = new AttributeRefHolder();
			finalDataRef.setType(MetaType.datapod);
			finalDataRef.setUuid(datapod.getUuid());
			finalDataRef.setVersion(datapod.getVersion());
			finalDataRef.setName(datapod.getName());
			attributeRef.setAttrId(Integer.toString(datapod.getAttributes().get(i).getAttributeId()));
			attributeRef.setAttrType(datapod.getAttributes().get(i).getType());
			if (datapod.getAttributes().get(i).getDispName() != null) {
				attributeRef.setAttrName(datapod.getAttributes().get(i).getDispName());
			} else
				attributeRef.setAttrName(datapod.getAttributes().get(i).getName());
			// attributeRef.setAttrDesc(datapod.getAttributes().get(i).getDesc());
			attributeRef.setRef(finalDataRef);
			attrRefDetails.add(attributeRef);
		}
		return attrRefDetails;
	}

	public String getRuleExecByRule(String ruleUuid) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(ruleExecServiceImpl.findRuleExecByRule(ruleUuid));
		return result;
	}

	public String getRuleExecByRGExec(String ruleGroupExecUuid, String ruleGroupExecVersion)
			throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(
				ruleExecServiceImpl.findRuleExecByRuleGroupExec(ruleGroupExecUuid, ruleGroupExecVersion));
		return result;
	}

	public String getFormulaByType(String uuid) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(formulaServiceImpl.findFormulaByType(uuid));

		return result;
	}

	public String getVizpodByType(String uuid) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(vizpodServiceImpl.findVizpodByType(uuid));

		return result;
	}

	/********************** UNUSED **********************/
/*	public String getAsOf(String uuid, String asOf, String type) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			case "relation":
				Relation relation = relationServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(relationServiceImpl.resolveName(relation));
				break;
			case "datapod":
				Datapod datapod = datapodServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(datapodServiceImpl.resolveName(datapod));
				break;
			case "dag":
				Dag dag = dagServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(dagServiceImpl.resolveName(dag));
				break;
			case "filter":
				Filter filter = filterServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(filterServiceImpl.resolveName(filter));
				break;
			case "expression":
				Expression expression = expressionServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(expressionServiceImpl.resolveName(expression));
				break;
			case "formula":
				Formula formula = formulaServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(formulaServiceImpl.resolveName(formula));
				break;
			case "map":
				com.inferyx.framework.domain.Map map = mapServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(mapServiceImpl.resolveName(map));
				break;
			case "dagexec":
				DagExec dagExec = dagExecServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(dagExecServiceImpl.resolveName(dagExec));
				break;
			case "condition":
				Condition condition = conditionServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(conditionServiceImpl.resolveName(condition));
				break;
			case "meta":
				result = ow.writeValueAsString(metadataServiceImpl.getAsOf(uuid, asOf));
				break;
			case "vizpod":
				Vizpod vizpod = vizpodServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(vizpodServiceImpl.resolveName(vizpod));
				break;
			case "datastore":
				DataStore datastore = dataStoreServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(dataStoreServiceImpl.resolveName(datastore));
				break;
			case "user":
				User user = userServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(userServiceImpl.resolveName(user));
				break;
			case "session":
				Session session = sessionServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(sessionServiceImpl.resolveName(session));
				break;
			case "activity":
				Activity activity = activityServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(activityServiceImpl.resolveName(activity));
				break;
			case "role":
				Role role = roleServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(roleServiceImpl.resolveName(role));
				break;
			case "group":
				Group group = groupServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(groupServiceImpl.resolveName(group));
				break;
			case "privilege":
				Privilege privilege = privilegeServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(privilegeServiceImpl.resolveName(privilege));
				break;
			case "dimension":
				Dimension dimension = dimensionServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(dimensionServiceImpl.resolveName(dimension));
				break;
			case "measure":
				Measure measure = measureServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(measureServiceImpl.resolveName(measure));
				break;
			case "vizexec":
				VizExec vizExec = vizExecServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(vizExecServiceImpl.resolveName(vizExec));
				break;
			case "dataset":
				Dataset dataset = datasetServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(datasetServiceImpl.resolveName(dataset));
				break;
			case "application":
				Application application = applicationServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(applicationServiceImpl.resolveName(application));
				break;
			case "datasource":
				Datasource datasource = datasourceServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(datasourceServiceImpl.resolveName(datasource));
				break;
			case "dq":
				DataQual dq = dataQualServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(dataQualServiceImpl.resolveName(dq));
				break;
			case "dqgroup":
				DataQualGroup dqgroup = dataQualGroupServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(dataQualGroupServiceImpl.resolveName(dqgroup));
				break;
			case "dqExec":
				DataQualExec dqExec = dataQualExecServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(dataQualExecServiceImpl.resolveName(dqExec));
				break;
			case "dqgroupexec":
				DataQualGroupExec dqgroupExec = dataQualGroupExecServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(dataQualGroupExecServiceImpl.resolveName(dqgroupExec));
				break;
			case "profile":
				Profile profile = profileServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(profileServiceImpl.resolveName(profile));
				break;
			case "profileexec":
				ProfileExec profileExec = profileExecServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(profileExecServiceImpl.resolveName(profileExec));
				break;
			case "profilegroup":
				ProfileGroup profileGroup = profileGroupServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(profileGroupServiceImpl.resolveName(profileGroup));
				break;
			case "profilegroupexec":
				ProfileGroupExec profilegroupExec = profileGroupExecServiceImpl.getAsOf(uuid, asOf);
				result = ow.writeValueAsString(profileGroupExecServiceImpl.resolveName(profilegroupExec));
				break;
			}
		}
		return result;
	}
*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> getList(String type, HttpServletRequest request) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			case "datapod":
				List<Datapod> datapodList = datapodServiceImpl.findAll();
				baseEntityList = datapodServiceImpl.findList(datapodList);
				break;
			case "relation":
				List<Relation> relationList = relationServiceImpl.findAll();
				baseEntityList = relationServiceImpl.findList(relationList);
				break;
			case "dag":
				List<Dag> dagList = dagServiceImpl.findAll();
				baseEntityList = dagServiceImpl.findList(dagList);
				break;
			case "filter":
				List<Filter> filterList = filterServiceImpl.findAll();
				baseEntityList = filterServiceImpl.findList(filterList);
				break;
			case "expression":
				List<Expression> expressionList = expressionServiceImpl.findAll();
				baseEntityList = expressionServiceImpl.findList(expressionList);
				break;
			case "formula":
				List<Formula> formulaList = formulaServiceImpl.findAll();
				baseEntityList = formulaServiceImpl.findList(formulaList);
				break;
			case "map":
				List<com.inferyx.framework.domain.Map> mapList = mapServiceImpl.findAll();
				baseEntityList = formulaServiceImpl.findList(mapList);
				break;
			case "dagexec":
				List<DagExec> dagExecList = dagExecServiceImpl.findAll();
				baseEntityList = dagExecServiceImpl.findList(dagExecList);
				break;
			case "condition":
				List<Condition> conditionList = conditionServiceImpl.findAll();
				baseEntityList = conditionServiceImpl.findList(conditionList);
				break;
			case "vizpod":
				List<Vizpod> vizpodList = vizpodServiceImpl.findAll();
				baseEntityList = vizpodServiceImpl.findList(vizpodList);
				break;
			case "datastore":
				List<DataStore> datastoreList = dataStoreServiceImpl.findAll();
				baseEntityList = dataStoreServiceImpl.findList(datastoreList);
				break;
			case "user":
				List<User> userList = userServiceImpl.findAll();
				baseEntityList = userServiceImpl.findList(userList);
				break;
			case "session":
				List<Session> sessionList = sessionServiceImpl.findAll();
				baseEntityList = sessionServiceImpl.findList(sessionList);
				break;
			case "activity":
				List<Activity> activityList = activityServiceImpl.findAll();
				baseEntityList = activityServiceImpl.findList(activityList);
				break;
			case "role":
				List<Role> roleList = roleServiceImpl.findAll();
				baseEntityList = roleServiceImpl.findList(roleList);
				break;
			case "group":
				List<Group> groupList = groupServiceImpl.findAll();
				baseEntityList = groupServiceImpl.findList(groupList);
				break;
			case "privilege":
				List<Privilege> privilegeList = privilegeServiceImpl.findAll();
				baseEntityList = privilegeServiceImpl.findList(privilegeList);
				break;
			case "dimension":
				List<Dimension> dimensionList = dimensionServiceImpl.findAll();
				baseEntityList = dimensionServiceImpl.findList(dimensionList);
				break;
			case "measure":
				List<Measure> measureList = measureServiceImpl.findAll();
				baseEntityList = measureServiceImpl.findList(measureList);
				break;
			case "vizexec":
				List<VizExec> vizExecList = vizExecServiceImpl.findAll();
				baseEntityList = vizExecServiceImpl.findList(vizExecList);
				break;
			case "dataset":
				List<Dataset> datasetList = datasetServiceImpl.findAll();
				baseEntityList = datasetServiceImpl.findList(datasetList);
				break;
			case "application":
				List<Application> applicationList = applicationServiceImpl.findAll();
				baseEntityList = applicationServiceImpl.findList(applicationList);
				break;
			case "datasource":
				List<Datasource> datasourceList = datasourceServiceImpl.findAll();
				baseEntityList = datasourceServiceImpl.findList(datasourceList);
				break;
			case "dq":
				List<DataQual> dqList = dataQualServiceImpl.findAll();
				baseEntityList = dataQualServiceImpl.findList(dqList);
				break;
			case "dqgroup":
				List<DataQualGroup> dqgroupList = dataQualGroupServiceImpl.findAll();
				baseEntityList = dataQualGroupServiceImpl.findList(dqgroupList);
				break;
			case "dqExec":
				List<DataQualExec> dqExecList = dataQualExecServiceImpl.findAll();
				baseEntityList = dataQualExecServiceImpl.findList(dqExecList);
				break;
			case "dqgroupexec":
				List<DataQualGroupExec> dqgroupExecList = dataQualGroupExecServiceImpl.findAll();
				baseEntityList = dataQualGroupExecServiceImpl.findList(dqgroupExecList);
				break;
			case "profile":
				List<Profile> profileList = profileServiceImpl.findAll();
				baseEntityList = profileServiceImpl.findList(profileList);
				break;
			case "profileexec":
				List<ProfileExec> profileExecList = profileExecServiceImpl.findAll();
				baseEntityList = profileExecServiceImpl.findList(profileExecList);
				break;
			case "profilegroup":
				List<ProfileGroup> profilegroupList = profileGroupServiceImpl.findAll();
				baseEntityList = profileGroupServiceImpl.findList(profilegroupList);
				break;
			case "profilegroupexec":
				List<ProfileGroupExec> profileGroupExecList = profileGroupExecServiceImpl.findAll();
				baseEntityList = profileGroupExecServiceImpl.findList(profileGroupExecList);
				break;
			case "algorithm":
				List<Algorithm> algoList = algorithmServiceImpl.findAll();
				baseEntityList = algorithmServiceImpl.findList(algoList);
				break;
			case "model":
				List<Model> modelList = modelServiceImpl.findAll();
				baseEntityList = modelServiceImpl.findList(modelList);
				break;
			case "modelexec":
				List<ModelExec> modelExecList = modelExecServiceImpl.findAll();
				baseEntityList = modelExecServiceImpl.findList(modelExecList);
				break;
			case "paramlist":
				List<ParamList> paramListList = paramListServiceImpl.findAll();
				baseEntityList = paramListServiceImpl.findList(paramListList);
				break;
			case "paramset":
				List<ParamSet> paramSetList = paramSetServiceImpl.findAll();
				baseEntityList = paramSetServiceImpl.findList(paramSetList);
				break;
			}
		}
		return baseEntityList;
	}
*/
	public String getExpressionByType(String uuid) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(expressionServiceImpl.findExpressionByType(uuid));
		return result;
	}

	public String getRuleGroupExecByRuleGroup(String ruleGroupUuid, String ruleGroupVersion)
			throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(
				ruleGroupExecServiceImpl.findRuleGroupExecByRuleGroup(ruleGroupUuid, ruleGroupVersion));
		return result;
	}

	public List<Registry> getRegistryByDatasource(String datasourceUuid,String status) throws IOException, SQLException {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		//Datasource datasource = datasourceServiceImpl.findLatestByUuid(datasourceUuid);
		Datasource datasource = (Datasource) commonServiceImpl.getLatestByUuid(datasourceUuid, MetaType.datasource.toString());
		List<Registry> registryList = new ArrayList<Registry>();
		List<Registry> registerList = new ArrayList<Registry>();
		List<Registry> unRegisterList = new ArrayList<Registry>();

		if (datasource.getType().equalsIgnoreCase(ExecContext.HIVE.toString()) 
				|| datasource.getType().equalsIgnoreCase(ExecContext.IMPALA.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) {
//			List<String> tables = new ArrayList<>();
			Map<String, String> tablesWithPath = new Hashtable<>();
			try {				
				IConnector connector = connectionFactory.getConnector(datasource.getType());
				ConnectionHolder connectionHolder = connector.getConnection();
				Connection con = ((Statement) connectionHolder.getStmtObject()).getConnection();
				DatabaseMetaData dbMetaData = con.getMetaData();
				ResultSet rs = dbMetaData.getTables(null, null, "%", null);
				while(rs.next()) {
					tablesWithPath.put(rs.getString(3), (datasource.getDbname()+"."+rs.getString(3)));
//					tables.add(rs.getString(3));
				}
				logger.info("Tables are :: " + tablesWithPath);				
				List<Registry> datapodList = createDatapodList(tablesWithPath, datasourceUuid, appUuid);
				registryList.addAll(datapodList);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//For FILE
		else if(datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
			File folder = new File(datasource.getPath());
			File[] listOfFiles = folder.listFiles();
//			List<String> fileList = new ArrayList<String>();
			Map<String, String> tablesWithPath = new Hashtable<>();
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					logger.info("File " + listOfFiles[i].getName());
					String fileName = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().indexOf("."));
					logger.info(fileName);
//					fileList.add(fileName);
					String path = datasource.getPath() + listOfFiles[i].getName();
					tablesWithPath.put(fileName, path);
				} else if (listOfFiles[i].isDirectory()) {
					logger.info("Directory " + listOfFiles[i].getName());
				}
			}
			List<Registry> datapodList = createDatapodList(tablesWithPath, datasourceUuid, appUuid);
			registryList.addAll(datapodList);
		}
//		//For Mysql
//		else if (datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString())) {
//			List<String> tables = new ArrayList<String>();
//			try {
//				IConnector connector = connectionFactory.getConnector(ExecContext.MYSQL.toString());
//				ConnectionHolder connectionHolder = connector.getConnection();
//				Connection con = ((Statement) connectionHolder.getStmtObject()).getConnection();
//				DatabaseMetaData dbMetaData = con.getMetaData();
//				ResultSet rs = dbMetaData.getTables(null, null, "%", null);
//				
//				while (rs.next()) 
//					tables.add(rs.getString(3));
//				logger.info("Mysql Tables :  " + tables);
//				rs.close();				
//
//				List<Registry> datapodList = createDatapodList(tables, datasourceUuid, appUuid);
//				registryList.addAll(datapodList);
//			} catch (IllegalArgumentException | SecurityException | NullPointerException | SQLException e1) {
//				e1.printStackTrace();
//			}
//		}
//		// For Oracle
//		else if (datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
//			List<String> tables = new ArrayList<String>();
//			try {			
//				IConnector connector = connectionFactory.getConnector(ExecContext.ORACLE.toString());
//				ConnectionHolder connectionHolder = connector.getConnection();
//				Connection con = ((Statement) connectionHolder.getStmtObject()).getConnection();
//				DatabaseMetaData dbMetaData = con.getMetaData();
//				ResultSet rs = dbMetaData.getTables(null, null, "%", null);
//
//				while (rs.next()) 
//					tables.add(rs.getString(3));				
//				logger.info("Oracle Tables :  " + tables);
//				rs.close();
//
//				List<Registry> datapodList = createDatapodList(tables, datasourceUuid, appUuid);
//				registryList.addAll(datapodList);
//				
//			} catch (IllegalArgumentException | SecurityException | NullPointerException | SQLException e1) {
//				e1.printStackTrace();
//			}
//		} 
//		//for Postgres
//		else if (datasource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) {
//			List<String> tables = new ArrayList<String>();
//			try {
//				IConnector connector = connectionFactory.getConnector(ExecContext.MYSQL.toString());
//				ConnectionHolder connectionHolder = connector.getConnection();
//				Connection con = ((Statement) connectionHolder.getStmtObject()).getConnection();
//				DatabaseMetaData dbMetaData = con.getMetaData();
//				ResultSet rs = dbMetaData.getTables(null, null, "%", null);
//				
//				while (rs.next()) 
//					tables.add(rs.getString(3));
//				logger.info("PostGres Tables :  " + tables);				
//				rs.close();		
//				
//				List<Registry> datapodList = createDatapodList(tables, datasourceUuid, appUuid);
//				registryList.addAll(datapodList);
//			} catch (IllegalArgumentException | SecurityException | NullPointerException | SQLException e1) {
//				e1.printStackTrace();
//			}
//		}
//		
		if (status.equalsIgnoreCase("UnRegistered")) {
			int count = 1;
			for (Registry unRegiser : registryList) {				
				if (unRegiser.getStatus().equalsIgnoreCase("UnRegistered")) {		
					unRegiser.setId(Integer.toString(count));
					unRegisterList.add(unRegiser);
					count++;
				}
			}
			return unRegisterList;
		} else if (status.equals("Registered")) {
			int count = 1;
			for (Registry register : registryList) {				
				if (register.getStatus().equals("Registered")) {					
					register.setId(Integer.toString(count));
					registerList.add(register);
					count++;
				}
			}
			return registerList;
		} else {
			return registryList;
		}

	}

	private List<Registry> createDatapodList(Map<String, String> tablesWithPath, String datasourceUuid, String appUuid) throws JsonProcessingException {
		List<Registry> registryList = new ArrayList<Registry>();
		List<Datapod> datapodList = null;
		int i = 1;
		String compareStatus = null;
		for (Entry<String, String> tableWithPath : tablesWithPath.entrySet()) {
			datapodList = datapodServiceImpl.searchDatapodByName(tableWithPath.getKey(), datasourceUuid);
			if (datapodList.size() > 0){
				for (Datapod datapod : datapodList) {
					for (int j = 0; j < datapod.getAppInfo().size(); j++) {
						if (datapod.getAppInfo().get(j).getRef().getUuid().equals(appUuid)) {
							try {
								 compareStatus=datapodServiceImpl.getMetaStatsByDatapodName(datapod.getUuid(), datapod.getVersion(), RunMode.BATCH);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Registry registry = new Registry();
							registry.setId(Integer.toString(i));
							registry.setName(tableWithPath.getKey());
							registry.setPath(tableWithPath.getValue());
							registry.setRegisteredOn(datapod.getCreatedOn());
							registry.setDesc(datapod.getDesc());
							registry.setRegisteredOn(datapod.getCreatedOn());
							registry.setStatus("Registered");
							registry.setCompareStatus(compareStatus);
							registryList.add(registry);
							break;
						} else {
							Registry registry = new Registry();
							registry.setId(Integer.toString(i));
							registry.setName(tableWithPath.getKey());
							registry.setPath(tableWithPath.getValue());
							registry.setRegisteredOn(null);
							registry.setDesc(null);
							registry.setRegisteredOn(null);
							registry.setStatus("UnRegistered");
							registryList.add(registry);
						}
					}
				}
			} else {
				Registry registry = new Registry();
				registry.setId(Integer.toString(i));
				registry.setName(tableWithPath.getKey());
				registry.setPath(tableWithPath.getValue());
				registry.setRegisteredOn(null);
				registry.setDesc(null);
				registry.setRegisteredOn(null);
				registry.setStatus("UnRegistered");
				registryList.add(registry);
			}
			i++;
		}
		return registryList;
	}

	public List<Registry> register(String uuid, String version, String type, List<Registry> registryList, RunMode runMode)
			throws Exception {
		if (type.equalsIgnoreCase(ExecContext.FILE.toString())) {
			return csvRegister.register(uuid, version, registryList, runMode);
		} else if (type.equalsIgnoreCase(ExecContext.HIVE.toString()) | type.equalsIgnoreCase(ExecContext.IMPALA.toString())) {
			return hiveRegister.registerDB(uuid, version, registryList, runMode);
		} /*else if (type.equalsIgnoreCase("impala")) {
			return impalaRegister.registerDB(uuid, version, registryList);
		} */else if (type.equalsIgnoreCase(ExecContext.MYSQL.toString())) {
			return mysqlRegister.registerDB(uuid, version, registryList, runMode);
		} else if (type.equalsIgnoreCase(ExecContext.ORACLE.toString())) {
			return oracleRegister.registerDB(uuid, version, registryList, runMode);
		} else if (type.equalsIgnoreCase(ExecContext.POSTGRES.toString())) {
				return postGresRegister.registerDB(uuid, version, registryList, runMode);
		} else {
			return null;
		}

	}

	/********************** UNUSED 
	 * @throws JsonProcessingException 
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException **********************/
	/*public String saveAs(String uuid, String version, String type) throws Exception {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			case "relation":
				Relation relation = relationServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(relationServiceImpl.saveAs(relation));
				break;
			case "datapod":
				Datapod datapod = datapodServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(datapodServiceImpl.saveAs(datapod));
				break;
			case "dag":
				Dag dag = dagServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dagServiceImpl.saveAs(dag));
				break;
			case "filter":
				Filter filter = filterServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(filterServiceImpl.saveAs(filter));
				break;
			case "expression":
				Expression expression = expressionServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(expressionServiceImpl.saveAs(expression));
				break;
			case "formula":
				Formula formula = formulaServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(formulaServiceImpl.saveAs(formula));
				break;
			case "map":
				com.inferyx.framework.domain.Map map = mapServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(mapServiceImpl.saveAs(map));
				break;
			case "dagexec":
				DagExec dagExec = dagExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dagExecServiceImpl.saveAs(dagExec));
				break;
			case "condition":
				Condition condition = conditionServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(conditionServiceImpl.saveAs(condition));
				break;
			case "vizpod":
				Vizpod vizpod = vizpodServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(vizpodServiceImpl.saveAs(vizpod));
				break;
			case "datastore":
				DataStore datastore = dataStoreServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dataStoreServiceImpl.saveAs(datastore));
				break;
			case "user":
				User user = userServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(userServiceImpl.saveAs(user));
				break;
			
			 * case "session": Session session =
			 * sessionServiceImpl.findOneByUuidAndVersion(uuid, version); result =
			 * ow.writeValueAsString(sessionServiceImpl.saveAs(session)); break;
			 
			
			 * case "activity": Activity activity =
			 * activityServiceImpl.findOneByUuidAndVersion(uuid, version); result =
			 * ow.writeValueAsString(activityServiceImpl.saveAs(activity)); break;
			 
			case "role":
				Role role = roleServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(roleServiceImpl.saveAs(role));
				break;
			case "group":
				Group group = groupServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(groupServiceImpl.saveAs(group));
				break;
			case "privilege":
				Privilege privilege = privilegeServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(privilegeServiceImpl.saveAs(privilege));
				break;
			case "dimension":
				Dimension dimension = dimensionServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dimensionServiceImpl.saveAs(dimension));
				break;
			case "measure":
				Measure measure = measureServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(measureServiceImpl.saveAs(measure));
				break;
			case "vizexec":
				VizExec vizExec = vizExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(vizExecServiceImpl.saveAs(vizExec));
				break;
			case "dataset":
				Dataset dataset = datasetServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(datasetServiceImpl.saveAs(dataset));
				break;
			case "application":
				Application application = applicationServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(applicationServiceImpl.saveAs(application));
				break;
			case "load":
				Load load = loadServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(loadServiceImpl.saveAs(load));
				break;
			case "datasource":
				Datasource datasource = datasourceServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(datasourceServiceImpl.saveAs(datasource));
				break;
			case "dq":
				DataQual dq = dataQualServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dataQualServiceImpl.saveAs(dq));
				break;
			case "dqgroup":
				DataQualGroup dqgroup = dataQualGroupServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dataQualGroupServiceImpl.saveAs(dqgroup));
				break;
			case "dqExec":
				DataQualExec dqExec = dataQualExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dataQualExecServiceImpl.saveAs(dqExec));
				break;
			case "dqgroupexec":
				DataQualGroupExec dqgroupExec = dataQualGroupExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dataQualGroupExecServiceImpl.saveAs(dqgroupExec));
				break;
			case "rule":
				Rule rule = ruleServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(ruleServiceImpl.saveAs(rule));
				break;
			case "rulegroup":
				RuleGroup ruleGroup = ruleGroupServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(ruleGroupServiceImpl.saveAs(ruleGroup));
				break;
			case "function":
				Function function = functionServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(functionServiceImpl.saveAs(function));
				break;
			case "profile":
				Profile profile = profileServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(profileServiceImpl.saveAs(profile));
				break;
			case "profileexec":
				ProfileExec profileExec = profileExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(profileExecServiceImpl.saveAs(profileExec));
				break;
			case "profilegroup":
				ProfileGroup profileGroup = profileGroupServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(profileGroupServiceImpl.saveAs(profileGroup));
				break;
			case "profilegroupexec":
				ProfileGroupExec profileGroupExec = profileGroupExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(profileGroupExecServiceImpl.saveAs(profileGroupExec));
				break;
			case "dashboard":
				Dashboard dashboard = dashboardServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(dashboardServiceImpl.saveAs(dashboard));
				break;
			case "algorithm":
				Algorithm algorithm = algorithmServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(algorithmServiceImpl.saveAs(algorithm));
				break;
			case "model":
				Model model = modelServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(modelServiceImpl.saveAs(model));
				break;
			case "modelexec":
				ModelExec modelExec = modelExecServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(modelExecServiceImpl.saveAs(modelExec));
				break;
			case "paramlist":
				ParamList paramList = paramListServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(paramListServiceImpl.saveAs(paramList));
				break;
			case "paramset":
				ParamSet paramSet = paramSetServiceImpl.findOneByUuidAndVersion(uuid, version);
				result = ow.writeValueAsString(paramSetServiceImpl.saveAs(paramSet));
				break;
			}
		}
		return result;
	}
*/
	public List<Function> getFunctionByFunctionInfo(String functionInfo) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<Function> functionList = iFunctionDao.findFunctionByFunctionInfo(functionInfo);
		List<Function> resolvedList = functionServiceImpl.resolveName(functionList);
		return resolvedList;
	}

	public String getFormulaByType2(String uuid,String[] formulaType) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(formulaServiceImpl.findFormulaByType2(uuid,formulaType));

		return result;
	}

	public String getExpressionByType2(String uuid) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(expressionServiceImpl.findExpressionByType2(uuid));

		return result;

	}

	public String getDagByDagExec(String dagExecUuid) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(dagServiceImpl.findDagByDagExec(dagExecUuid));
		return result;
	}

	public DagStatusHolder getStatusByDagExec(String dagExecUuid) throws JsonProcessingException {
		return dagExecServiceImpl.getStatusByDagExec(dagExecUuid);
	}

	/********************** UNUSED **********************/
	/*public String getParamSetByModel(String modelUuid, String modelVersion) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(paramSetServiceImpl.getParamSetByModel(modelUuid, modelVersion));
		return result;
	}*/
	
	public String getParamSetByTrain(String trainUuid, String trainVersion) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(paramSetServiceImpl.getParamSetByTrain(trainUuid, trainVersion));
		return result;
	}

	public String getParamSetByRule(String ruleUuid, String ruleVersion) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(paramSetServiceImpl.getParamSetByRule(ruleUuid, ruleVersion));
		return result;
	}

	public String getParamSetByParamList(String paramListUuid, String paramListVersion) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(paramSetServiceImpl.getParamSetByParamList(paramListUuid, paramListVersion));
		return result;
	}

	public String getParamSetByAlogrithm(String algorithmUUID, String algorithmVersion, String isHyperParam) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(paramSetServiceImpl.getParamSetByAlgorithm(algorithmUUID, algorithmVersion, isHyperParam));
		return result;
	}

	public String getMetaIdByExecId(String execUuid, String execVersion, String type) throws Exception {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			case "loadexec":
				result = ow.writeValueAsString(loadExecServiceImpl.getMetaIdByExecId(execUuid, execVersion));
				break;
			case "mapexec":
				result = ow.writeValueAsString(mapExecServiceImpl.getMetaIdByExecId(execUuid, execVersion));
				break;
			case "ruleexec":
				result = ow.writeValueAsString(ruleExecServiceImpl.getMetaIdByExecId(execUuid, execVersion));
				break;
			case "rulegroupexec":
				result = ow.writeValueAsString(ruleGroupExecServiceImpl.getMetaIdByExecId(execUuid, execVersion));
				break;
			case "dqexec":
				result = ow.writeValueAsString(dataQualExecServiceImpl.getMetaIdByExecId(execUuid, execVersion));
				break;
			case "dqgroupexec":
				result = ow.writeValueAsString(dataQualGroupExecServiceImpl.getMetaIdByExecId(execUuid, execVersion));
				break;
			case "profileexec":
				result = ow.writeValueAsString(profileExecServiceImpl.getMetaIdByExecId(execUuid, execVersion));
				break;
			case "profilegroupexec":
				result = ow.writeValueAsString(profileGroupExecServiceImpl.getMetaIdByExecId(execUuid, execVersion));
				break;
			case "trainexec":
				result = ow.writeValueAsString(modelExecServiceImpl.getMetaIdByExecId(execUuid, execVersion, type));
				break;
			case "predictexec":
				result = ow.writeValueAsString(modelExecServiceImpl.getMetaIdByExecId(execUuid, execVersion, type));
				break;
			case "simulateexec":
				result = ow.writeValueAsString(modelExecServiceImpl.getMetaIdByExecId(execUuid, execVersion, type));
				break;
			case "reconexec":
				result = ow.writeValueAsString(reconServiceImpl.getMetaIdByExecId(execUuid, execVersion));
				break;
			case "recongroupexec":
				result = ow.writeValueAsString(reconGroupServiceImpl.getMetaIdByExecId(execUuid, execVersion));
				break;	
			case "operatorexec" : 
				result = ow.writeValueAsString(modelExecServiceImpl.getMetaIdByExecId(execUuid, execVersion, type));
				break;
			}
		}
		return result;
	}

	private MetaStatsHolder addToCount(String type, int count, String lastUpdatedBy, String lastUpdatedOn) {
		MetaStatsHolder holder = new MetaStatsHolder(type, Long.toString(count), lastUpdatedBy, lastUpdatedOn);
		return holder;
	}

	public List<MetaStatsHolder> getExecStats() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonProcessingException {
		List<MetaStatsHolder> countHolder = new ArrayList<>();

		int loadExecCount = commonServiceImpl.findAllLatest(MetaType.loadExec).size();
		LoadExec loadExec = (LoadExec) commonServiceImpl.getLatest(MetaType.loadExec.toString());
		if (loadExec != null) {
			countHolder.add(addToCount(MetaType.loadExec.toString(), loadExecCount,
					loadExec.getCreatedBy().getRef().getName(), loadExec.getCreatedOn()));
		}

		int mapExecCount = commonServiceImpl.findAllLatest(MetaType.mapExec).size();
		MapExec mapExec = (MapExec) commonServiceImpl.getLatest(MetaType.mapExec.toString());
		if (mapExec != null) {
			countHolder.add(addToCount(MetaType.mapExec.toString(), mapExecCount,
					mapExec.getCreatedBy().getRef().getName(), mapExec.getCreatedOn()));

		}

		int ruleExecCount = commonServiceImpl.findAllLatest(MetaType.ruleExec).size();
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getLatest(MetaType.ruleExec.toString());
		if (ruleExec != null) {
			countHolder.add(addToCount(MetaType.ruleExec.toString(), ruleExecCount,
					ruleExec.getCreatedBy().getRef().getName(), ruleExec.getCreatedOn()));
		}

		int ruleGroupExecCount = commonServiceImpl.findAllLatest(MetaType.rulegroupExec).size();
		RuleGroupExec ruleGroupExec = (RuleGroupExec) commonServiceImpl.getLatest(MetaType.rulegroupExec.toString());
		if (ruleGroupExec != null) {
			countHolder.add(addToCount(MetaType.rulegroupExec.toString(), ruleGroupExecCount,
					ruleGroupExec.getCreatedBy().getRef().getName(), ruleGroupExec.getCreatedOn()));
		}

		int profileExecCount = commonServiceImpl.findAllLatest(MetaType.profileExec).size();
		ProfileExec profileExec =(ProfileExec) commonServiceImpl.getLatest(MetaType.profileExec.toString());
		if (profileExec != null) {
			countHolder.add(addToCount(MetaType.profileExec.toString(), profileExecCount,
					profileExec.getCreatedBy().getRef().getName(), profileExec.getCreatedOn()));
		}

		int profileGroupExecCount = commonServiceImpl.findAllLatest(MetaType.profilegroupExec).size();
		ProfileGroupExec profileGroupExec = (ProfileGroupExec) commonServiceImpl.getLatest(MetaType.profilegroupExec.toString());
		if (profileGroupExec != null) {
			countHolder.add(addToCount(MetaType.profilegroupExec.toString(), profileGroupExecCount,
					profileGroupExec.getCreatedBy().getRef().getName(), profileGroupExec.getCreatedOn()));
		}

		int dagExecCount = commonServiceImpl.findAllLatest(MetaType.dagExec).size();
		DagExec dagExec = (DagExec) commonServiceImpl.getLatest(MetaType.dagExec.toString());
		if (dagExec != null) {
			countHolder.add(addToCount(MetaType.dagExec.toString(), dagExecCount,
					dagExec.getCreatedBy().getRef().getName(), dagExec.getCreatedOn()));
		}

		int vizExecCount = commonServiceImpl.findAllLatest(MetaType.vizExec).size();
		VizExec vizExec = (VizExec) commonServiceImpl.getLatest(MetaType.vizExec.toString());
		if (vizExec != null) {
			countHolder.add(addToCount(MetaType.vizExec.toString(), vizExecCount,
					vizExec.getCreatedBy().getRef().getName(), vizExec.getCreatedOn()));
		}

		int dataQualExecCount = commonServiceImpl.findAllLatest(MetaType.dqExec).size();
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getLatest(MetaType.dqExec.toString());
		if (dataQualExec != null) {
			countHolder.add(addToCount(MetaType.dqExec.toString(), dataQualExecCount,
					dataQualExec.getCreatedBy().getRef().getName(), dataQualExec.getCreatedOn()));
		}

		int dataQualGroupExecCount = commonServiceImpl.findAllLatest(MetaType.dqgroupExec).size();
		DataQualGroupExec dataQualGroupExec = (DataQualGroupExec) commonServiceImpl.getLatest(MetaType.dqgroupExec.toString());
		if (dataQualGroupExec != null) {
			countHolder.add(addToCount(MetaType.dqgroupExec.toString(), dataQualGroupExecCount,
					dataQualGroupExec.getCreatedBy().getRef().getName(), dataQualGroupExec.getCreatedOn()));
		}

		/*int modelExecCount = commonServiceImpl.findAllLatest(MetaType.modelExec).size();
		ModelExec modelExec = (ModelExec) commonServiceImpl.getLatest(MetaType.modelExec.toString());
		if (modelExec != null) {
			countHolder.add(addToCount(MetaType.modelExec.toString(), modelExecCount,
					modelExec.getCreatedBy().getRef().getName(), modelExec.getCreatedOn()));
		}*/
		
		int uploadExecCount = commonServiceImpl.findAllLatest(MetaType.uploadExec).size();
		UploadExec uploadExec = (UploadExec) commonServiceImpl.getLatest(MetaType.uploadExec.toString());
		if(uploadExec != null) {
			countHolder.add(addToCount(MetaType.uploadExec.toString(), uploadExecCount, uploadExec.getCreatedBy().getRef().getName(), uploadExec.getCreatedOn()));
		}
		
		int downloadExecCount = commonServiceImpl.findAllLatest(MetaType.downloadExec).size();
		DownloadExec downloadloadExec = (DownloadExec) commonServiceImpl.getLatest(MetaType.downloadExec.toString());
		if(downloadloadExec != null) {
			countHolder.add(addToCount(MetaType.downloadExec.toString(), downloadExecCount, downloadloadExec.getCreatedBy().getRef().getName(), downloadloadExec.getCreatedOn()));
		}
		
		int predictExecCount = commonServiceImpl.findAllLatest(MetaType.predictExec).size();
		PredictExec predictExec = (PredictExec) commonServiceImpl.getLatest(MetaType.predictExec.toString());
		if(predictExec != null) {
			countHolder.add(addToCount(MetaType.predictExec.toString(), predictExecCount, predictExec.getCreatedBy().getRef().getName(), predictExec.getCreatedOn()));
		}

		int simulateExecCount = commonServiceImpl.findAllLatest(MetaType.simulateExec).size();
		SimulateExec simulateExec = (SimulateExec) commonServiceImpl.getLatest(MetaType.simulateExec.toString());
		if (simulateExec != null) {
			countHolder.add(addToCount(MetaType.simulateExec.toString(), simulateExecCount,
					simulateExec.getCreatedBy().getRef().getName(), simulateExec.getCreatedOn()));
		}
		
		int trainExecCount = commonServiceImpl.findAllLatest(MetaType.trainExec).size();
		TrainExec trainExec = (TrainExec) commonServiceImpl.getLatest(MetaType.trainExec.toString());
		if (trainExec != null) {
			countHolder.add(addToCount(MetaType.trainExec.toString(), trainExecCount,
					trainExec.getCreatedBy().getRef().getName(), trainExec.getCreatedOn()));
		}
		int reconExecCount = commonServiceImpl.findAllLatest(MetaType.reconExec).size();
		ReconExec reconExec = (ReconExec) commonServiceImpl.getLatest(MetaType.reconExec.toString());
		if (reconExec != null) {
			countHolder.add(addToCount(MetaType.reconExec.toString(), reconExecCount,
					reconExec.getCreatedBy().getRef().getName(), reconExec.getCreatedOn()));
		}
		
		int reconGroupExecCount = commonServiceImpl.findAllLatest(MetaType.recongroupExec).size();
		ReconGroupExec reconGroupExec = (ReconGroupExec) commonServiceImpl.getLatest(MetaType.recongroupExec.toString());
		if (reconGroupExec != null) {
			countHolder.add(addToCount(MetaType.recongroupExec.toString(), reconGroupExecCount,
					reconGroupExec.getCreatedBy().getRef().getName(), reconGroupExec.getCreatedOn()));
		}
        
		int operatorExecCount = commonServiceImpl.findAllLatest(MetaType.operatorExec).size();
		OperatorExec operatorExec = (OperatorExec) commonServiceImpl.getLatest(MetaType.operatorExec.toString());
		if (operatorExec != null) {
			countHolder.add(addToCount(MetaType.operatorExec.toString(), operatorExecCount,
					operatorExec.getCreatedBy().getRef().getName(), operatorExec.getCreatedOn()));
		}
		int graphExecCount = commonServiceImpl.findAllLatest(MetaType.graphExec).size();
		GraphExec graphExec = (GraphExec) commonServiceImpl.getLatest(MetaType.graphExec.toString());
		
		if (graphExec != null) {
			countHolder.add(addToCount(MetaType.graphExec.toString(), graphExecCount,
					graphExec.getCreatedBy().getRef().getName(), graphExec.getCreatedOn()));
		}
		int reprotExecCount = commonServiceImpl.findAllLatest(MetaType.reportExec).size();
		ReportExec reportExec = (ReportExec) commonServiceImpl.getLatest(MetaType.reportExec.toString());
		
		if (reportExec != null) {
			countHolder.add(addToCount(MetaType.reportExec.toString(), reprotExecCount,
					reportExec.getCreatedBy().getRef().getName(), reportExec.getCreatedOn()));
		}
		
		int batchExecCount = commonServiceImpl.findAllLatest(MetaType.batchExec).size();
		BatchExec batchExec = (BatchExec) commonServiceImpl.getLatest(MetaType.batchExec.toString());
		if (batchExec != null) {
			countHolder.add(addToCount(MetaType.batchExec.toString(), batchExecCount,
					batchExec.getCreatedBy().getRef().getName(), batchExec.getCreatedOn()));
		}

		return countHolder;
		
		
	}

	public long getMetaStatsByType() throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return datapodServiceImpl.count();
	}

	public List<MetaStatsHolder> getMetaStats() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonProcessingException {
		List<MetaStatsHolder> countHolder = new ArrayList<>();

		/*int activityCount = activityServiceImpl.findAllLatest().size();*/
		int activityCount = commonServiceImpl.getAllLatest(MetaType.activity.toString(), null).size();
		/*Activity activity = activityServiceImpl.findLatest();*/
		Activity activity = (Activity) commonServiceImpl.getLatest(MetaType.activity.toString());
		countHolder.add(addToCount(MetaType.activity.toString(), activityCount,
		activity.getCreatedBy().getRef().getName(), activity.getCreatedOn()));

		/*int datapodCount = datapodServiceImpl.findAllLatest().size();*/
		int datapodCount = commonServiceImpl.getAllLatest(MetaType.datapod.toString(), null).size();
		/*Datapod datapod = datapodServiceImpl.findLatest();*/
		Datapod datapod = (Datapod) commonServiceImpl.getLatest(MetaType.datapod.toString());
		countHolder.add(addToCount(MetaType.datapod.toString(), datapodCount, datapod.getCreatedBy().getRef().getName(),
				datapod.getCreatedOn()));

		/*int filterCount = filterServiceImpl.findAllLatest().size();*/
		int filterCount = commonServiceImpl.getAllLatest(MetaType.filter.toString(), null).size();
		/*Filter filter = filterServiceImpl.findLatest();*/
		Filter filter = (Filter) commonServiceImpl.getLatest(MetaType.filter.toString());
		countHolder.add(addToCount(MetaType.filter.toString(), filterCount, filter.getCreatedBy().getRef().getName(),
				filter.getCreatedOn()));

		/*int dagCout = dagServiceImpl.findAllLatest().size();*/
		int dagCout = commonServiceImpl.getAllLatest(MetaType.dag.toString(), null).size();
		/*Dag dag = dagServiceImpl.findLatest();*/
		Dag dag = (Dag) commonServiceImpl.getLatest(MetaType.dag.toString());
		countHolder.add(addToCount(MetaType.dag.toString(), dagCout, dag.getCreatedBy().getRef().getName(),
				dag.getCreatedOn()));

		/*int formulaCount = formulaServiceImpl.findAllLatest().size();*/
		int formulaCount = commonServiceImpl.getAllLatest(MetaType.formula.toString(), null).size();
		/*Formula formula = formulaServiceImpl.findLatest();*/
		Formula formula = (Formula) commonServiceImpl.getLatest(MetaType.formula.toString());
		countHolder.add(addToCount(MetaType.formula.toString(), formulaCount, formula.getCreatedBy().getRef().getName(),
				formula.getCreatedOn()));

		/*
		 * List<Condition> conditionList = daoRegister.getConditionDao().findAll();
		 * countHolder.add(addToCount(MetaType.condition.toString(),
		 * conditionList.size(),null,null));
		 */

		/*int relationCount = relationServiceImpl.findAllLatest().size();*/
		int relationCount = commonServiceImpl.getAllLatest(MetaType.relation.toString(), null).size();
		/*Relation relation = relationServiceImpl.findLatest();*/
		Relation relation = (Relation) commonServiceImpl.getLatest(MetaType.relation.toString());
		countHolder.add(addToCount(MetaType.relation.toString(), relationCount,
				relation.getCreatedBy().getRef().getName(), relation.getCreatedOn()));

		/*int mapCout = mapServiceImpl.findAllLatest().size();*/
		int mapCout = commonServiceImpl.getAllLatest(MetaType.map.toString(), null).size();
		/*com.inferyx.framework.domain.Map map = mapServiceImpl.findLatest();*/
		com.inferyx.framework.domain.Map map = (com.inferyx.framework.domain.Map) commonServiceImpl.getLatest(MetaType.map.toString());
		countHolder.add(addToCount(MetaType.map.toString(), mapCout, map.getCreatedBy().getRef().getName(),
				map.getCreatedOn()));

		/*
		 * List<Measure> measureList = daoRegister.getMeasureDao().findAll();
		 * countHolder.add(addToCount(MetaType.measure.toString(),
		 * measureList.size(),null,null));
		 */

		/*int dataStoreCount = dataStoreServiceImpl.findAllLatest().size();*/
		int dataStoreCount = commonServiceImpl.getAllLatest(MetaType.datastore.toString(), null).size();
		/*DataStore dataStore = dataStoreServiceImpl.findLatest();*/
		DataStore dataStore = (DataStore) commonServiceImpl.getLatest(MetaType.datastore.toString());
		countHolder.add(addToCount(MetaType.datastore.toString(), dataStoreCount,
				dataStore.getCreatedBy().getRef().getName(), dataStore.getCreatedOn()));

		/*int vizpodCount = vizpodServiceImpl.findAllLatest().size();*/
		int vizpodCount = commonServiceImpl.getAllLatest(MetaType.vizpod.toString(), null).size();
		/*Vizpod vizpod = vizpodServiceImpl.findLatest();*/
		Vizpod vizpod = (Vizpod) commonServiceImpl.getLatest(MetaType.vizpod.toString());
		countHolder.add(addToCount(MetaType.vizpod.toString(), vizpodCount, vizpod.getCreatedBy().getRef().getName(),
				vizpod.getCreatedOn()));

		/*int userCount = userServiceImpl.findAllLatest().size();*/
		int userCount = commonServiceImpl.getAllLatest(MetaType.user.toString(), null).size();
		/*User user = userServiceImpl.findLatest();*/
		User user = (User) commonServiceImpl.getLatest(MetaType.user.toString());
		countHolder.add(addToCount(MetaType.user.toString(), userCount, user.getCreatedBy().getRef().getName(),
				user.getCreatedOn()));

		/*int groupCount = groupServiceImpl.findAllLatest().size();*/
		int groupCount = commonServiceImpl.getAllLatest(MetaType.group.toString(), null).size();
		/*Group group = groupServiceImpl.findLatest();*/
		Group group = (Group) commonServiceImpl.getLatest(MetaType.group.toString());
		countHolder.add(addToCount(MetaType.group.toString(), groupCount, group.getCreatedBy().getRef().getName(),
				group.getCreatedOn()));

		/*int roleCount = roleServiceImpl.findAllLatest().size();*/
		int roleCount = commonServiceImpl.getAllLatest(MetaType.role.toString(), null).size();
		/*Role role = roleServiceImpl.findLatest();*/
		Role role = (Role) commonServiceImpl.getLatest(MetaType.role.toString());
		countHolder.add(addToCount(MetaType.role.toString(), roleCount, role.getCreatedBy().getRef().getName(),
				role.getCreatedOn()));

		/*int privilegeCount = privilegeServiceImpl.findAllLatest().size();*/
		int privilegeCount = commonServiceImpl.getAllLatest(MetaType.privilege.toString(), null).size();
		/*Privilege privilege = privilegeServiceImpl.findLatest();*/
		Privilege privilege = (Privilege) commonServiceImpl.getLatest(MetaType.privilege.toString());
		countHolder.add(addToCount(MetaType.privilege.toString(), privilegeCount,
				privilege.getCreatedBy().getRef().getName(), privilege.getCreatedOn()));

		int applicationCount = commonServiceImpl.getAllLatest(MetaType.application.toString(), null).size();
		Application application = (Application) commonServiceImpl.getLatest(MetaType.application.toString());
		countHolder.add(addToCount(MetaType.application.toString(), applicationCount,
				application.getCreatedBy().getRef().getName(), application.getCreatedOn()));

		int datasourceCount = commonServiceImpl.getAllLatest(MetaType.datasource.toString(), null).size();
		Datasource datasource = (Datasource) commonServiceImpl.getLatest(MetaType.datasource.toString());
		countHolder.add(addToCount(MetaType.datasource.toString(), datasourceCount,
				datasource.getCreatedBy().getRef().getName(), datasource.getCreatedOn()));

		int sessionCount = commonServiceImpl.getAllLatest(MetaType.session.toString(), null).size();
		Session session = (Session) commonServiceImpl.getLatest(MetaType.session.toString());
		countHolder.add(addToCount(MetaType.session.toString(), sessionCount, session.getCreatedBy().getRef().getName(),
				session.getCreatedOn()));

		/*
		 * List<DagExec> dagExecList = daoRegister.getDagExecDao().findAll();
		 * countHolder.add(addToCount(MetaType.dagexec.toString(),
		 * dagExecList.size(),null,null));
		 */

		/*
		 * List<VizExec> vizExecList = daoRegister.getiVizpodExecDao().findAll();
		 * countHolder.add(addToCount(MetaType.vizexec.toString(),
		 * vizExecList.size(),null,null));
		 */

		int expressionCount = commonServiceImpl.getAllLatest(MetaType.expression.toString(), null).size();
		Expression expression = (Expression) commonServiceImpl.getLatest(MetaType.expression.toString());
		if (expression != null)
			countHolder.add(addToCount(MetaType.expression.toString(), expressionCount,
					expression.getCreatedBy().getRef().getName(), expression.getCreatedOn()));

		int datasetCount = commonServiceImpl.getAllLatest(MetaType.dataset.toString(), null).size();
		DataSet dataset = (DataSet) commonServiceImpl.getLatest(MetaType.dataset.toString());
		countHolder.add(addToCount(MetaType.dataset.toString(), datasetCount, dataset.getCreatedBy().getRef().getName(),
				dataset.getCreatedOn()));

		int loadCount = commonServiceImpl.getAllLatest(MetaType.load.toString(), null).size();
		Load load = (Load) commonServiceImpl.getLatest(MetaType.load.toString());
		countHolder.add(addToCount(MetaType.load.toString(), loadCount, load.getCreatedBy().getRef().getName(),
				load.getCreatedOn()));

		int dashboardCount = commonServiceImpl.getAllLatest(MetaType.dashboard.toString(), null).size();
		Dashboard dashboard = (Dashboard) commonServiceImpl.getLatest(MetaType.dashboard.toString());
		countHolder.add(addToCount(MetaType.dashboard.toString(), dashboardCount,
				dashboard.getCreatedBy().getRef().getName(), dashboard.getCreatedOn()));

		int ruleGroupCount = commonServiceImpl.getAllLatest(MetaType.rulegroup.toString(), null).size();
		RuleGroup ruleGroup = (RuleGroup) commonServiceImpl.getLatest(MetaType.rulegroup.toString());
		countHolder.add(addToCount(MetaType.rulegroup.toString(), ruleGroupCount,
				ruleGroup.getCreatedBy().getRef().getName(), ruleGroup.getCreatedOn()));

		int dataQualCount = commonServiceImpl.getAllLatest(MetaType.dq.toString(), null).size();
		DataQual dataQual = (DataQual) commonServiceImpl.getLatest(MetaType.dq.toString());
		countHolder.add(addToCount(MetaType.dq.toString(), dataQualCount, dataQual.getCreatedBy().getRef().getName(),
				dataQual.getCreatedOn()));

		/*
		 * List<DataQualExec> dataQualexecList =
		 * daoRegister.getiDataQualExecDao().findAll();
		 * countHolder.add(addToCount(MetaType.dqExec.toString(),
		 * dataQualexecList.size(),null,null));
		 */

		int dataQualGroupCount = commonServiceImpl.getAllLatest(MetaType.dqgroup.toString(), null).size();
		DataQualGroup dataQualGroup = (DataQualGroup) commonServiceImpl.getLatest(MetaType.dqgroup.toString());
		countHolder.add(addToCount(MetaType.dqgroup.toString(), dataQualGroupCount,
				dataQualGroup.getCreatedBy().getRef().getName(), dataQualGroup.getCreatedOn()));

		/*
		 * List<DataQualGroupExec> dataQualGroupExecList =
		 * daoRegister.getiDataQualGroupExecDao().findAll();
		 * countHolder.add(addToCount(MetaType.dqgroupExec.toString(),
		 * dataQualGroupExecList.size(),null,null));
		 */

		int ruleCount = commonServiceImpl.getAllLatest(MetaType.rule.toString(), null).size();
		Rule rule = (Rule) commonServiceImpl.getLatest(MetaType.rule.toString());
		countHolder.add(addToCount(MetaType.rule.toString(), ruleCount, rule.getCreatedBy().getRef().getName(),
				rule.getCreatedOn()));

		int functionCount = commonServiceImpl.getAllLatest(MetaType.function.toString(), null).size();
		Function function = (Function) commonServiceImpl.getLatest(MetaType.function.toString());
		countHolder.add(addToCount(MetaType.function.toString(), functionCount,
				function.getCreatedBy().getRef().getName(), function.getCreatedOn()));

		/*
		 * int loadExecCount=loadExecServiceImpl.findAllLatest().size(); LoadExec
		 * loadExec=loadExecServiceImpl.findLatest();
		 * countHolder.add(addToCount(MetaType.loadexec.toString(),loadExecCount,
		 * loadExec.getCreatedBy().getRef().getName(),loadExec.getCreatedOn()));
		 */

		/*
		 * int mapExecCount=dataQualServiceImpl.findAllLatest().size(); MapExec
		 * mapExec=mapExecServiceImpl.findLatest();
		 * countHolder.add(addToCount(MetaType.dq.toString(),mapExecCount,dataQual.
		 * getCreatedBy().getRef().getName(),mapExec.getCreatedOn()));
		 */
		/*
		 * int ruleExecCount=dataQualServiceImpl.findAllLatest().size(); RuleExec
		 * ruleExec=ruleExecServiceImpl.findLatest();
		 * countHolder.add(addToCount(MetaType.dq.toString(),ruleExecCount,dataQual.
		 * getCreatedBy().getRef().getName(),ruleExec.getCreatedOn()));
		 */

		int profileCount = commonServiceImpl.getAllLatest(MetaType.profile.toString(), null).size();
		Profile profile = (Profile) commonServiceImpl.getLatest(MetaType.profile.toString());
		countHolder.add(addToCount(MetaType.profile.toString(), profileCount, profile.getCreatedBy().getRef().getName(),
				profile.getCreatedOn()));

		int profileGroupCount = commonServiceImpl.getAllLatest(MetaType.profilegroup.toString(), null).size();
		ProfileGroup profileGroup = (ProfileGroup) commonServiceImpl.getLatest(MetaType.profilegroup.toString());
		countHolder.add(addToCount(MetaType.profilegroup.toString(), profileGroupCount,
				profileGroup.getCreatedBy().getRef().getName(), profileGroup.getCreatedOn()));

		/*
		 * int profileExecCount=profileExecServiceImpl.findAllLatest().size();
		 * ProfileExec profileExec=profileExecServiceImpl.findLatest();
		 * countHolder.add(addToCount(MetaType.profileexec.toString(),profileExecCount,
		 * profileExec.getCreatedBy().getRef().getName(),profileExec.getCreatedOn()));
		 */

		int algorithmCount = commonServiceImpl.getAllLatest(MetaType.algorithm.toString(), null).size();
		Algorithm algorithm = (Algorithm) commonServiceImpl.getLatest(MetaType.algorithm.toString());
		countHolder.add(addToCount(MetaType.algorithm.toString(), algorithmCount,
				algorithm.getCreatedBy().getRef().getName(), algorithm.getCreatedOn()));

		int modelCount = commonServiceImpl.getAllLatest(MetaType.model.toString(), null).size();
		Model model = (Model) commonServiceImpl.getLatest(MetaType.model.toString());
		countHolder.add(addToCount(MetaType.model.toString(), modelCount, model.getCreatedBy().getRef().getName(),
				model.getCreatedOn()));

		int paramListCount = commonServiceImpl.getAllLatest(MetaType.paramlist.toString(), null).size();
		ParamList paramList = (ParamList) commonServiceImpl.getLatest(MetaType.paramlist.toString());
		countHolder.add(addToCount(MetaType.paramlist.toString(), paramListCount,
				paramList.getCreatedBy().getRef().getName(), paramList.getCreatedOn()));

		int paramsetCount = commonServiceImpl.getAllLatest(MetaType.paramset.toString(), null).size();
		ParamSet paramSet = (ParamSet) commonServiceImpl.getLatest(MetaType.paramset.toString());
		countHolder.add(addToCount(MetaType.paramset.toString(), paramsetCount,
				paramSet.getCreatedBy().getRef().getName(), paramSet.getCreatedOn()));

		return countHolder;
	}

	public String getNumRowsbyExec(String execUuid, String execVersion, String type) throws Exception {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		if (type != null && !type.isEmpty()) {
			type = type.toLowerCase();
			switch (type) {
			case "ruleexec":
				result = ow.writeValueAsString(ruleExecServiceImpl.getNumRowsbyExec(execUuid, execVersion));
				break;
			case "dqexec":
				result = ow.writeValueAsString(dataQualExecServiceImpl.getNumRowsbyExec(execUuid, execVersion));
				break;
			case "profileexec":
				result = ow.writeValueAsString(profileExecServiceImpl.getNumRowsbyExec(execUuid, execVersion));
				break;
			case "trainexec":
				result = ow.writeValueAsString(modelExecServiceImpl.getNumRowsbyExec(execUuid, execVersion, type));
				break;
			case "predictexec":
				result = ow.writeValueAsString(modelExecServiceImpl.getNumRowsbyExec(execUuid, execVersion, type));
				break;
			case "simulateexec":
				result = ow.writeValueAsString(modelExecServiceImpl.getNumRowsbyExec(execUuid, execVersion, type));
				break;
			case "reconexec":
				result = ow.writeValueAsString(reconExecServiceImpl.getNumRowsbyExec(execUuid, execVersion, type));
				break;			
			case "mapexec":
				result = ow.writeValueAsString(mapExecServiceImpl.getNumRowsbyExec(execUuid, execVersion, type));
				break;
			case "operatorexec":
				result = ow.writeValueAsString(modelExecServiceImpl.getNumRowsbyExec(execUuid, execVersion, type));
				break;
			case "loadexec":
				result = ow.writeValueAsString(loadExecServiceImpl.getNumRowsbyExec(execUuid, execVersion));
				break;
			}
		}
		return result;
	}
	public List<RuleExec> getRuleExecByRule(String ruleUuid, String startDate, String endDate, String type, String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		Query query = new Query();
		query.fields().include("statusList");
		query.fields().include("dependsOn");
		query.fields().include("exec");
		query.fields().include("result");
		query.fields().include("refKeyList");
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("createdBy");
		query.fields().include("createdOn");
		query.fields().include("active");
		query.fields().include("published");
		query.fields().include("appInfo");
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("EEE MMM dd hh:mm:ss yyyy z");// Tue Mar 13 04:15:00 2018 UTC
			
		try {
			if ((startDate != null	&& !StringUtils.isEmpty(startDate))
				&& (endDate != null	&& !StringUtils.isEmpty(endDate)))

			query.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
			query.addCriteria(Criteria.where("active").is("Y")); 
			
			query.addCriteria(Criteria.where("createdOn").gt(simpleDateFormat.parse(startDate))
						.lte(simpleDateFormat.parse(endDate)));
			
			query.addCriteria(Criteria.where("statusList.stage").in(Status.Stage.Completed.toString()));
			query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(ruleUuid));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		List<RuleExec> ruleExecObjList = new ArrayList<>();
		ruleExecObjList = (List<RuleExec>) mongoTemplate.find(query, RuleExec.class);
		
		return ruleExecObjList;
	}

	
}
