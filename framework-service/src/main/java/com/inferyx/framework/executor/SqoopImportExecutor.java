/**
 * 
 */
package com.inferyx.framework.executor;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudera.sqoop.SqoopOptions;
import com.cloudera.sqoop.SqoopOptions.IncrementalMode;
import com.cloudera.sqoop.tool.ImportTool;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.SqoopConnector;
import com.inferyx.framework.domain.SqoopInput;

/**
 * @author joy
 *
 */
@Service
public class SqoopImportExecutor {
	
	@Autowired
	private SqoopConnector sqoopConnector;

	/**
	 * 
	 */
	public SqoopImportExecutor() {
		// TODO Auto-generated constructor stub
	}
	
	private void setSqoopOptions(SqoopOptions sqoopOptions, SqoopInput sqoopInput) {
		sqoopOptions.setHiveImport(sqoopInput.getHiveImport());
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
		if (StringUtils.isNotBlank(sqoopInput.getIncrementalMode().toString())) {
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
	    
	    
	    
		
	}
	
	public Object execute(Object input) throws IOException {
		SqoopInput sqoopInput = null;
		SqoopOptions sqoopOptions = new SqoopOptions();
		ConnectionHolder connHolder = null;
		if (input == null) {
			return null;
		}
		if (input instanceof SqoopInput) {
			sqoopInput = (SqoopInput) input;
			sqoopOptions = (SqoopOptions) sqoopConnector.getConnection(sqoopInput.getSourceDs(), sqoopOptions).getConObject();
			setSqoopOptions(sqoopOptions, sqoopInput);
		}
		int res;
	    res = new ImportTool().run(sqoopOptions);
	    if (res != 0) {
	      throw new RuntimeException("Sqoop API Failed - return code : "+Integer.toString(res));
	    }
	    return res;
	}

}
