/**
 * 
 */
package com.inferyx.framework.connector;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.cloudera.sqoop.SqoopOptions;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.ExecContext;

/**
 * @author joy
 *
 */
@Component
public class SqoopConnector implements IConnector {

	/**
	 * 
	 */
	public SqoopConnector() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ConnectionHolder getConnection(Object input, Object input2) throws IOException {
		SqoopOptions sqoopOptions = null;
		if (input2 == null) {
			sqoopOptions = new SqoopOptions();
		} else {
			sqoopOptions = (SqoopOptions) input2;
		}
		ConnectionHolder connHolder = new ConnectionHolder();
		Datasource datasource = null;
		String connectionString = null;
		String userName = null;
		String password = null;
		connHolder.setType(ExecContext.SQOOP.toString());
		if (input == null) {
			connHolder.setConObject(sqoopOptions);
			return connHolder;
		}
		if (input instanceof Datasource) {
			datasource = (Datasource) input;
			switch (datasource.getType()) {
			case "ORACLE" : 
				connectionString = "jdbc:oracle:thin:@" + datasource.getHost() + ":"
					+ datasource.getPort() + ":" + datasource.getSid();
				userName = datasource.getUsername();
				password = datasource.getPassword();
				break;
			case "MYSQL" : 
				connectionString = "jdbc:mysql://" + datasource.getHost() + ":" + datasource.getPort()
				+ "/" + datasource.getDbname()+"?useSSL=false";
				userName = datasource.getUsername();
				password = datasource.getPassword();
				break;
			case "IMPALA" : 
				connectionString = "jdbc:postgresql://" + datasource.getHost() + ":" + datasource.getPort()
				+ "/" + datasource.getDbname();
				userName = datasource.getUsername();
				password = datasource.getPassword();
				break;
			case "HIVE" : 
				connectionString = "jdbc:hive2://" + datasource.getHost() + ":" + datasource.getPort()
				+ "/" + datasource.getDbname();
				userName = datasource.getUsername();
				password = datasource.getPassword();
				break;
			case "POSTGRES" : 
				connectionString = "jdbc:postgresql://" + datasource.getHost() + ":" + datasource.getPort()
				+ "/" + datasource.getDbname();
				userName = datasource.getUsername();
				password = datasource.getPassword();
				break;
			case "spark" : 
			case "PYTHON" : 
			case "R" : 
			case "SQOOP" : 
				default : 
			}
			sqoopOptions.setConnectString(connectionString);
		    sqoopOptions.setUsername(userName);
		    sqoopOptions.setPassword(password);
		    connHolder.setConObject(sqoopOptions);
		    return connHolder;
		}
			
		return null;
	}

	@Override
	public ConnectionHolder getConnection() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionHolder getConnectionByDatasource(Datasource datasource) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}