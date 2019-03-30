/**
 * 
 * @Ganesh
 *
 */
package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;

/**
 * @author Ganesh
 *
 */
@Component
public class LoadOperator implements IOperator {
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	
	public static Logger logger = Logger.getLogger(LoadOperator.class);
	
	public String generateSql(String targetTableName, MetaIdentifierHolder targetHolder, String filePathUrl) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {

		//Datasource datasource = commonServiceImpl.getDatasourceByApp();
		StringBuilder loadQuery = new StringBuilder();
		Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(), targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());
		Datasource datasource = commonServiceImpl.getDatasourceByDatapod(targetDatapod);

//		loadQuery.append("LOAD DATA LOCAL INPATH '");
		loadQuery.append("LOAD DATA INPATH '");
		loadQuery.append(filePathUrl).append("' ");
		loadQuery.append(" INTO TABLE ");
		loadQuery.append(targetTableName);
		if(datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString())) {
//			String version = Helper.getVersion();
//			boolean isVersion = false;
			if(loadQuery.toString().contains("INPATH"))
				loadQuery.replace(loadQuery.indexOf("INPATH"), loadQuery.indexOf("INPATH")+6, "INFILE");
			
			loadQuery.append(" FIELDS TERMINATED BY ',' ");
			loadQuery.append("LINES TERMINATED BY '\\r' ");
			loadQuery.append(" IGNORE 1 LINES ");
			String query = loadQuery.toString();
//			loadQuery.append(" ( ");
//			for(Attribute attribute : targetDatapod.getAttributes()) {
//				loadQuery.append(attribute.getName());
//				loadQuery.append(", ");
//				if(attribute.getName().equalsIgnoreCase("version")){
//					isVersion = true;
//				}
//			}
//			String query = loadQuery.subSequence(0, loadQuery.lastIndexOf(",")).toString().concat(" ) ");
//			if(isVersion){
//				query = query.concat("SET version="+version);
//			}
			logger.info("query: "+query);
			return query;
		} else if(datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
//			String version = Helper.getVersion();
//			boolean isVersion = false;
//			if(loadQuery.toString().contains("INPATH"))
//				loadQuery.replace(loadQuery.indexOf("INPATH"), loadQuery.indexOf("INPATH")+6, "INFILE");
//			
//			loadQuery.append(" FIELDS TERMINATED BY ',' ");
//			loadQuery.append("LINES TERMINATED BY '\\n' ");
//			loadQuery.append(" IGNORE 1 LINES ");
//			loadQuery.append(" ( ");
//			for(Attribute attribute : targetDatapod.getAttributes()) {
//				loadQuery.append(attribute.getName());
//				loadQuery.append(", ");
//				if(attribute.getName().equalsIgnoreCase("version")){
//					isVersion = true;
//				}
//			}
//			String query = loadQuery.subSequence(0, loadQuery.lastIndexOf(",")).toString().concat(" ) ");
//			if(isVersion){
//				query = query.concat("SET version="+version);
//			}
//			logger.info("query: "+query);
//			return query;
		} else if(datasource.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) {
			loadQuery = new StringBuilder();
			loadQuery.append("COPY ");
			loadQuery.append(targetTableName);
			loadQuery.append(" ( ");
			for(Attribute attribute : targetDatapod.getAttributes()) {
				loadQuery.append(attribute.getName());
				loadQuery.append(", ");
			}
			String query = loadQuery.subSequence(0, loadQuery.lastIndexOf(",")).toString().concat(" ) ");
			loadQuery = new StringBuilder(query);
			loadQuery.append(" FROM ");
			loadQuery.append(" '");
			loadQuery.append(filePathUrl);
			loadQuery.append("' ");
			loadQuery.append(" WITH ");
			loadQuery.append(" DELIMITER ").append(" AS ").append(" ',' ");
			loadQuery.append(" NULL ").append(" AS ").append(" 'null' ");
			loadQuery.append(" ESCAPE ").append(" AS ").append(" '\n'");
			loadQuery.append(" CSV ").append(" HEADER ");
			
			logger.info("postgresql load query: "+loadQuery);
			return loadQuery.toString();
		} else if(datasource.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
			String partitionClos = Helper.getPartitionColumns(targetDatapod);
			if(partitionClos.length() > 0)
				loadQuery.append(" PARTITION ( " + Helper.getPartitionColumns(targetDatapod) +" ) ");

			logger.info("hive load query: "+loadQuery);
			return loadQuery.toString();
		} else if(datasource.getType().equalsIgnoreCase(ExecContext.IMPALA.toString())) {
			String partitionClos = Helper.getPartitionColumns(targetDatapod);
			if(partitionClos.length() > 0)
				loadQuery.append(" PARTITION ( " + Helper.getPartitionColumns(targetDatapod) +" ) ");

			logger.info("impala load query: "+loadQuery);
			return loadQuery.toString();
		}
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		LoadExec loadExec = (LoadExec) baseExec;
		MetaIdentifier dependsOnMI = loadExec.getDependsOn().getRef();
		Load load = (Load) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(), dependsOnMI.getType().toString());
		MetaIdentifierHolder targetHolder = load.getTarget();
		String filePathUrl = load.getSource().getValue();
		Datapod targetDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(), targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());
		String targetTableName = datapodServiceImpl.genTableNameByDatapod(targetDp, loadExec.getVersion(), null, null, null, runMode, true);
		String sql = generateSql(targetTableName, targetHolder, filePathUrl);
		baseExec.setExec(sql);
		commonServiceImpl.save(MetaType.loadExec.toString(), baseExec);
		return baseExec;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
