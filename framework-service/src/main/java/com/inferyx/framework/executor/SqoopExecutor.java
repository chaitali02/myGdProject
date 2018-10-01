/**
 * 
 */
package com.inferyx.framework.executor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.hive.common.util.HiveStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudera.sqoop.SqoopOptions;
import com.cloudera.sqoop.SqoopOptions.FileLayout;
import com.cloudera.sqoop.SqoopOptions.IncrementalMode;
import com.cloudera.sqoop.tool.BaseSqoopTool;
import com.cloudera.sqoop.tool.ExportTool;
import com.cloudera.sqoop.tool.ImportTool;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.SqoopConnector;
import com.inferyx.framework.domain.SqoopInput;
import com.inferyx.framework.enums.SaveMode;

/**
 * @author joy
 *
 */
@Service
public class SqoopExecutor {
	
	@Autowired
	private SqoopConnector sqoopConnector;
	
	private Map<String, String> keyConverter = new HashMap<>();
	
	static final Logger logger = Logger.getLogger(SqoopExecutor.class);

	/**
	 * 
	 */
	public SqoopExecutor() {
		// TODO Auto-generated constructor stub
	}
	
	private void populateKeyConverter() {
		/*this.keyConverter.put(BaseSqoopTool.CONNECT_STRING_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.CONN_MANAGER_CLASS_NAME  ,);
		this.keyConverter.put(BaseSqoopTool.CONNECT_PARAM_FILE  ,);
		this.keyConverter.put(BaseSqoopTool.DRIVER_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.USERNAME_ARG  ,); <<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.PASSWORD_ARG  ,); <<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.PASSWORD_PROMPT_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.PASSWORD_PATH_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.PASSWORD_ALIAS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.DIRECT_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.BATCH_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.TABLE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.STAGING_TABLE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.CLEAR_STAGING_TABLE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.COLUMNS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.SPLIT_BY_ARG  ,); <<<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.SPLIT_LIMIT_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.WHERE_ARG  ,); <<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.HADOOP_HOME_ARG  ,); 
		this.keyConverter.put(BaseSqoopTool.HADOOP_MAPRED_HOME_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HIVE_HOME_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.WAREHOUSE_DIR_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.TARGET_DIR_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.APPEND_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.DELETE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.DELETE_COMPILE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.NULL_STRING  ,);
		this.keyConverter.put(BaseSqoopTool.INPUT_NULL_STRING  ,);
		this.keyConverter.put(BaseSqoopTool.NULL_NON_STRING  ,);
		this.keyConverter.put(BaseSqoopTool.INPUT_NULL_NON_STRING  ,);
		this.keyConverter.put(BaseSqoopTool.MAP_COLUMN_JAVA  ,);
		this.keyConverter.put(BaseSqoopTool.MAP_COLUMN_HIVE  ,);
		this.keyConverter.put(BaseSqoopTool.FMT_SEQUENCEFILE_ARG  ,); <<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.FMT_TEXTFILE_ARG  ,); <<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.FMT_AVRODATAFILE_ARG  ,); <<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.FMT_PARQUETFILE_ARG  ,); <<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.FMT_BINARYFILE_ARG  ,); <<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.HIVE_IMPORT_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HIVE_TABLE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HIVE_DATABASE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HIVE_OVERWRITE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HIVE_DROP_DELIMS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HIVE_DELIMS_REPLACEMENT_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HIVE_PARTITION_KEY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HIVE_PARTITION_VALUE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HIVE_EXTERNAL_TABLE_LOCATION_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HCATCALOG_PARTITION_KEYS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HCATALOG_PARTITION_VALUES_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.CREATE_HIVE_TABLE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HCATALOG_TABLE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HCATALOG_DATABASE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.CREATE_HCATALOG_TABLE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.DROP_AND_CREATE_HCATALOG_TABLE  ,);
		this.keyConverter.put(BaseSqoopTool.HCATALOG_STORAGE_STANZA_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HCATALOG_HOME_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HS2_URL_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HS2_USER_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HS2_KEYTAB_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.MAPREDUCE_JOB_NAME  ,);
		this.keyConverter.put(BaseSqoopTool.NUM_MAPPERS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.NUM_MAPPERS_SHORT_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.COMPRESS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.COMPRESSION_CODEC_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.COMPRESS_SHORT_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.DIRECT_SPLIT_SIZE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.INLINE_LOB_LIMIT_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.FETCH_SIZE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.EXPORT_PATH_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.FIELDS_TERMINATED_BY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.LINES_TERMINATED_BY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.OPTIONALLY_ENCLOSED_BY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ENCLOSED_BY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ESCAPED_BY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.MYSQL_DELIMITERS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.INPUT_FIELDS_TERMINATED_BY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.INPUT_LINES_TERMINATED_BY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.INPUT_OPTIONALLY_ENCLOSED_BY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.INPUT_ENCLOSED_BY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.INPUT_ESCAPED_BY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.CODE_OUT_DIR_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.BIN_OUT_DIR_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.PACKAGE_NAME_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.CLASS_NAME_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.JAR_FILE_NAME_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.SQL_QUERY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.SQL_QUERY_BOUNDARY  ,);
		this.keyConverter.put(BaseSqoopTool.SQL_QUERY_SHORT_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.VERBOSE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HELP_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.TEMP_ROOTDIR_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.UPDATE_KEY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.UPDATE_MODE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.CALL_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.SKIP_DISTCACHE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.RELAXED_ISOLATION  ,);
		this.keyConverter.put(BaseSqoopTool.THROW_ON_ERROR_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ORACLE_ESCAPING_DISABLED  ,);
		this.keyConverter.put(BaseSqoopTool.ESCAPE_MAPPING_COLUMN_NAMES_ENABLED  ,);
		this.keyConverter.put(BaseSqoopTool.PARQUET_CONFIGURATOR_IMPLEMENTATION  ,); <<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.VALIDATE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.VALIDATOR_CLASS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.VALIDATION_THRESHOLD_CLASS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.VALIDATION_FAILURE_HANDLER_CLASS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.INCREMENT_TYPE_ARG  ,); <<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.INCREMENT_COL_ARG  ,); <<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.INCREMENT_LAST_VAL_ARG  ,); <<<<<<<<<<<<<
		this.keyConverter.put(BaseSqoopTool.ALL_TABLE_EXCLUDES_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HBASE_TABLE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HBASE_COL_FAM_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HBASE_ROW_KEY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HBASE_BULK_LOAD_ENABLED_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HBASE_CREATE_TABLE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.HBASE_NULL_INCREMENTAL_MODE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ACCUMULO_TABLE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ACCUMULO_COL_FAM_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ACCUMULO_ROW_KEY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ACCUMULO_VISIBILITY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ACCUMULO_CREATE_TABLE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ACCUMULO_BATCH_SIZE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ACCUMULO_MAX_LATENCY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ACCUMULO_ZOOKEEPERS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ACCUMULO_INSTANCE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ACCUMULO_USER_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.ACCUMULO_PASSWORD_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.STORAGE_METASTORE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.METASTORE_USER_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.METASTORE_PASS_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.JOB_CMD_CREATE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.JOB_CMD_DELETE_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.JOB_CMD_EXEC_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.JOB_CMD_LIST_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.JOB_CMD_SHOW_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.METASTORE_SHUTDOWN_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.NEW_DATASET_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.OLD_DATASET_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.MERGE_KEY_ARG  ,);
		this.keyConverter.put(BaseSqoopTool.AUTORESET_TO_ONE_MAPPER ,);*/
	}
	
	private void setSqoopOptions(SqoopOptions sqoopOptions, SqoopInput sqoopInput, Map<String, String> inputParams) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		sqoopOptions.setHiveImport(sqoopInput.getHiveImport());
		if (sqoopInput.getHiveImport()) {
			sqoopOptions.doHiveImport();
		}
		if (StringUtils.isNotBlank(sqoopInput.getTable())) {
			sqoopOptions.setTableName(sqoopInput.getTable());
		}
		if (StringUtils.isNotBlank(sqoopInput.getTargetDirectory())) {
			sqoopOptions.setWarehouseDir(sqoopInput.getTargetDirectory());
		}
		if (StringUtils.isNotBlank(sqoopInput.getPartitionKey())) {
			sqoopOptions.setHivePartitionKey(sqoopInput.getPartitionKey());
		}
		if (StringUtils.isNotBlank(sqoopInput.getPartitionValue())) {
			sqoopOptions.setHivePartitionValue(sqoopInput.getPartitionValue());
		}
		if (StringUtils.isNotBlank(sqoopInput.getIncrementalMode() == null ? "" : sqoopInput.getIncrementalMode().toString())) {
			switch(sqoopInput.getIncrementalMode()) {
			case AppendRows : sqoopOptions.setIncrementalMode(IncrementalMode.AppendRows);
				break;
			case DateLastModified : sqoopOptions.setIncrementalMode(IncrementalMode.DateLastModified);
				break;
			case None : sqoopOptions.setIncrementalMode(IncrementalMode.None);
				break;
			default : 
			}
		}
		sqoopOptions.setAppendMode(sqoopInput.getAppendMode());
		if (StringUtils.isNotBlank(sqoopInput.getIncrementalTestColumn())) {
			sqoopOptions.setIncrementalTestColumn(sqoopInput.getIncrementalTestColumn());
		}
		if (StringUtils.isNotBlank(sqoopInput.getIncrementalLastValue())) {
			sqoopOptions.setIncrementalLastValue(sqoopInput.getIncrementalLastValue());
		}
		if (StringUtils.isNotBlank(sqoopInput.getWhereClause())) {
			sqoopOptions.setWhereClause(sqoopInput.getWhereClause());
		}
		if (StringUtils.isNotBlank(sqoopInput.getExportDir())) {
			sqoopOptions.setExportDir(sqoopInput.getExportDir());
		}
		if (sqoopInput.getNumMappers() > 0) {
			sqoopOptions.setNumMappers(sqoopInput.getNumMappers());
		}
		if (sqoopInput.getLinesTerminatedBy() > 0) {
			sqoopOptions.setLinesTerminatedBy(sqoopInput.getLinesTerminatedBy());
		}
		if (sqoopInput.getFieldsTerminatedBy() > 0) {
			sqoopOptions.setFieldsTerminatedBy(sqoopInput.getFieldsTerminatedBy());
		}
		sqoopOptions.setExplicitInputDelims(sqoopInput.getExplicitInputDelims());
		sqoopOptions.setExplicitOutputDelims(sqoopInput.getExplicitOutputDelims());

		if (StringUtils.isNotBlank(sqoopInput.getHiveTableName())) {
			sqoopOptions.setHiveTableName(sqoopInput.getHiveTableName());
		}
		
		if(sqoopInput.getOverwriteHiveTable() != null && sqoopInput.getOverwriteHiveTable().equalsIgnoreCase(SaveMode.OVERWRITE.toString())) {
			sqoopOptions.setOverwriteHiveTable(true);
		}
		
		if (StringUtils.isNotBlank(sqoopInput.getHiveTableName())) {
			sqoopOptions.setHiveTableName(sqoopInput.getHiveTableName());
		}
		if(StringUtils.isNotBlank(sqoopInput.getHiveDatabaseName())) {
			sqoopOptions.setHiveDatabaseName(sqoopInput.getHiveDatabaseName());
		}
		
		if (StringUtils.isNotBlank(sqoopInput.getHCatalogTableName())) {
			sqoopOptions.setHCatTableName(sqoopInput.getHCatalogTableName());
		}
		
		if(StringUtils.isNotBlank(sqoopInput.getHCatalogDatabaseName())) {
			sqoopOptions.setHCatDatabaseName(sqoopInput.getHCatalogDatabaseName());
		}

		if (StringUtils.isNotBlank(sqoopInput.getFileLayout() == null ? "" : sqoopInput.getFileLayout().toString())) {
			switch(sqoopInput.getFileLayout()) {
			case AvroDataFile : sqoopOptions.setFileLayout(FileLayout.AvroDataFile);
				break;
			case SequenceFile : sqoopOptions.setFileLayout(FileLayout.SequenceFile);
				break;
			case TextFile : sqoopOptions.setFileLayout(FileLayout.TextFile);
				break;
			case ParquetFile : sqoopOptions.setFileLayout(FileLayout.ParquetFile);
				break;
			default : 
			}
		}
		
		sqoopOptions.setThrowOnError(true);
		
		if(inputParams != null && !inputParams.isEmpty()) {
			Class sqoopOptionsClass = SqoopOptions.class;
			Method[] methods = sqoopOptionsClass.getMethods();
			// populate all methods in Set
			Map<String, Method> methodMap = new HashMap<>();
			for (Method method : methods) {
				if (method.getName().startsWith("set")) {
					methodMap.put(method.getName().substring(3, method.getName().length()-1).toUpperCase(), method);
				}
			}
			for (String key : inputParams.keySet()) {
				if (methodMap.containsKey(key.toUpperCase())) {
					Method method = methodMap.get(key.toUpperCase());
					Object argument = resolveArgument(method, inputParams.get(key));
					methodMap.get(key.toUpperCase()).invoke(sqoopOptions, argument);
				}
			}
		}		
	    logger.info("SqoopInput : " + sqoopInput);
	}
	
	public Object resolveArgument(Method method , String argument) {
		Class<?> argumentClass = method.getParameterTypes()[0];
		Object argumentVal = argument;
		if(argumentClass.getName().toLowerCase().contains("int")) {
			argumentVal = Integer.parseInt(""+argument);
		} else if(argumentClass.getName().toLowerCase().contains("long")) {
			argumentVal = Long.parseLong(""+argument);
		} else if(argumentClass.getName().toLowerCase().contains("double")) {
			argumentVal = Double.parseDouble(""+argument);
		} else if(argumentClass.getName().toLowerCase().contains("float")) {
			argumentVal = Float.parseFloat(""+argument);
		} else if(argumentClass.getName().toLowerCase().contains("short")) {
			argumentVal = Short.parseShort(""+argument);
		}
		return argumentVal;
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public Object execute(Object input) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return execute(input, null);
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public Object execute(Object input, Map<String, String> inputParams) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SqoopInput sqoopInput = null;
		SqoopOptions sqoopOptions = new SqoopOptions();
		ConnectionHolder connHolder = null;
		if (input == null) {
			return null;
		}
		if (input instanceof SqoopInput) {
			sqoopInput = (SqoopInput) input;
			if (sqoopInput.getImportIntended()) {
				sqoopOptions = (SqoopOptions) sqoopConnector.getConnection(sqoopInput.getSourceDs(), sqoopOptions).getConObject();
			} else {
				sqoopOptions = (SqoopOptions) sqoopConnector.getConnection(sqoopInput.getTargetDs(), sqoopOptions).getConObject();
			}
			setSqoopOptions(sqoopOptions, sqoopInput, inputParams);
		}
		int res;

		//System.out.println("Jar File used in classpath : "+ HiveStringUtils.class.getProtectionDomain().getCodeSource().getLocation().toString());
		
		if (sqoopInput.getImportIntended()) {
			res = new ImportTool().run(sqoopOptions);
		} else {
			res = new ExportTool().run(sqoopOptions);
		}
	    if (res != 0) {
	      throw new RuntimeException("Sqoop API Failed - return code : "+Integer.toString(res));
	    }
	    return res;
	}

	public FileLayout getFileLayout(String targetFormat) {
		if(targetFormat != null) {
			switch(targetFormat.toLowerCase()) {
			case "parquet" : return FileLayout.ParquetFile;
			}
		}
		return null;
	}

}
