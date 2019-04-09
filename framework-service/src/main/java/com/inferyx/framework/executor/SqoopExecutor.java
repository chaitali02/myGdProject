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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudera.sqoop.SqoopOptions;
import com.cloudera.sqoop.SqoopOptions.FileLayout;
import com.cloudera.sqoop.SqoopOptions.IncrementalMode;
import com.cloudera.sqoop.tool.ExportTool;
import com.cloudera.sqoop.tool.ImportTool;
import com.inferyx.framework.connector.SqoopConnector;
import com.inferyx.framework.domain.SqoopInput;
import com.inferyx.framework.enums.SaveMode;

/**
 * @author joy
 *
 */
@SuppressWarnings("deprecation")
@Service
public class SqoopExecutor {
	
	@Autowired
	private SqoopConnector sqoopConnector;
	
//	private Map<String, String> keyConverter = new HashMap<>();
	
	static final Logger logger = Logger.getLogger(SqoopExecutor.class);

	/**
	 * 
	 */
	public SqoopExecutor() {
		// TODO Auto-generated constructor stub
	}
	
	
	private void setSqoopOptions(SqoopOptions sqoopOptions, SqoopInput sqoopInput, Map<String, String> inputParams) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		sqoopOptions.setHiveImport(sqoopInput.getHiveImport());
		if (sqoopInput.getHiveImport()) {
			sqoopOptions.doHiveImport();
		}
		if (StringUtils.isNotBlank(sqoopInput.getTable())) {
			sqoopOptions.setTableName(sqoopInput.getTable());
		}
		if (StringUtils.isNotBlank(sqoopInput.getWarehouseDirectory())) {
			sqoopOptions.setWarehouseDir(sqoopInput.getWarehouseDirectory());
		}
		if (StringUtils.isNotBlank(sqoopInput.getTargetDirectory())) {
			sqoopOptions.setTargetDir(sqoopInput.getTargetDirectory());
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

		if(sqoopInput.getSqlQuery() != null && StringUtils.isNotBlank(sqoopInput.getSqlQuery())) {
			sqoopOptions.setSqlQuery(sqoopInput.getSqlQuery());
		}
		
		if(sqoopInput.getSplitByCol() != null && StringUtils.isNotBlank(sqoopInput.getSplitByCol())) {
			sqoopOptions.setSplitByCol(sqoopInput.getSplitByCol());
		}
		
		if(sqoopInput.isDeleteMode()) {
			sqoopOptions.setDeleteMode(sqoopInput.isDeleteMode());
		}
		
		sqoopOptions.setConnManagerClassName(sqoopInput.getConnManagerClassName());
		
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
			Class<SqoopOptions> sqoopOptionsClass = SqoopOptions.class;
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
//	    logger.info("SqoopInput : " + sqoopOptions);
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
//		ConnectionHolder connHolder = null;
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
			logger.info("SqoopInput before execution : " + sqoopInput);
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

	/******************Unused***********************/
	/*public FileLayout getFileLayout(String targetFormat) {
		if(targetFormat != null) {
			switch(targetFormat.toLowerCase()) {
			case "parquet" : return FileLayout.ParquetFile;
			}
		}
		return null;
	}*/

}
