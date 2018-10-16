/**
 * 
 */
package com.inferyx.framework.executor.helper;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.StreamInput;
import com.inferyx.framework.enums.IngestionType;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.HiveExecutor;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class KafkaStreamToTableHelper<T, K> {
	
	static Logger logger = Logger.getLogger(KafkaStreamToTableHelper.class);
	
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	HiveExecutor hiveExecutor;

	/**
	 * 
	 */
	public KafkaStreamToTableHelper() {
		// TODO Auto-generated constructor stub
	}

	public void help (StreamInput<T, K> streamInput, KStream<T, K> lines) throws IOException {
		Map<String, Object> runParams = streamInput.getRunParams();
		StructType schema = (StructType) runParams.get("SCHEMA");
		IConnector connector = streamInput.getConnector();
		ConnectionHolder conHolder = connector.getConnection();
		SparkSession session = (SparkSession) conHolder.getStmtObject();
//		SQLContext sqlContext = session.sqlContext();
		String saveMode = streamInput.getSaveMode();
//		String url = (String) inputMap.get("URL");
		String tableName = (String) streamInput.getTargetTableName();
		Properties connectionProperties = (Properties) runParams.get("CONN_PROPS");
		Datasource targetDS = streamInput.getTargetDS();
		Datapod targetDP = streamInput.getTargetDP();
		String datasourceType = targetDS.getType();
		String sessionParameters = targetDS.getSessionParameters();
		String url = Helper.genUrlByDatasource(targetDS);
		String driver = targetDS.getDriver(); 
		String userName = targetDS.getUsername();
		String password = targetDS.getPassword();
		String targetPath = streamInput.getTargetDir();
		String targetType = streamInput.getTargetType();
		String fileFormat = streamInput.getFileFormat();
		String ingestionType = streamInput.getIngestionType();
		String[] dataTypes =  new String[] {"integer", "varchar(70)", "varchar(70)"
				, "integer", "integer", "integer"};;
		String[] attributeList2 = null;
		if(targetDS.getType().equalsIgnoreCase(ExecContext.FILE.toString()) 
				&& targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
			attributeList2 = new String[targetDP.getAttributes().size()];
			for(int i=0; i<targetDP.getAttributes().size(); i++) {
				attributeList2[i] = targetDP.getAttributes().get(i).getName();
			}
		}
		final String[] attributeList = attributeList2;
		
		ExecParams execParams = streamInput.getExecParams();
		
		ParamListHolder keyInfo = paramSetServiceImpl.getParamByName(execParams, "key");
		ParamListHolder valueInfo = paramSetServiceImpl.getParamByName(execParams, "value");
		ParamListHolder topicInfo = paramSetServiceImpl.getParamByName(execParams, "topic");
		
		String keyColName = keyInfo.getParamValue().getValue();
		String valueColName = valueInfo.getParamValue().getValue();
		String topicColName = topicInfo.getParamValue().getValue();
		
		logger.info("Hive writing starts for this partition >>>>>>>>>>>>>>>>>>>> ");
		if(ingestionType.equalsIgnoreCase(IngestionType.STREAMTOTABLE.toString())) {
			lines.foreach(new ForeachAction<T, K>() {
				
				@Override
				public void apply(T key, K value) {
					String sql1 = " INSERT INTO " + targetDP.getName() + "(";
					String sql2 = " VALUES (";
					String sql = "";
					for(int i=0; i<attributeList.length; i++){
						sql1 += attributeList[i] + ", ";
						
						if (attributeList[i].equals(keyColName)) {
							sql2 += key + ", "; 
						} else if (attributeList[i].equals(valueColName)) {
							sql2 += value + ", ";
						} else if (attributeList[i].equals(topicColName)) {
							sql2 += streamInput.getTopicName() + ", ";
						}
						
					} 
					sql = sql1.concat(sql2);
					try {
						hiveExecutor.executeSql(sql);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			
		} /*else if(ingestionType.equalsIgnoreCase(IngestionType.STREAMTOFILE.toString())) {
			if(fileFormat == null) {
				df.coalesce(1).write().mode(saveMode).format("csv").option("delimiter", ",").csv(targetPath);
			} else if(fileFormat.equalsIgnoreCase(FileType.CSV.toString())) {
				df.coalesce(1).write().mode(saveMode).option("delimiter", ",").csv(targetPath);
			} else if(fileFormat.equalsIgnoreCase(FileType.TSV.toString())) {
				df.coalesce(1).write().mode(saveMode).option("delimiter", "\t").csv(targetPath);
			} else if(fileFormat.equalsIgnoreCase(FileType.PSV.toString())) {
				df.coalesce(1).write().mode(saveMode).option("delimiter", "|").csv(targetPath);
			} else if(fileFormat.equalsIgnoreCase(FileType.PARQUET.toString())) {
//				rsHolder = registerAndPersistDataframe(rsHolder, datapod, "append", targetPath, tableName, false);
			}
		}
*/

	}
	
}
