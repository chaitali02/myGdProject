/**
 * 
 */
package com.inferyx.framework.executor;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.ws.client.disource.IngestServiceService;

/**
 * @author joy
 *
 */
@Service
public class WSSourceExecutor<T> {

	@Autowired
	ConnectionFactory connectionFactory;
	@Autowired
	SparkExecutor sparkExecutor;
	
	static final Logger logger = Logger.getLogger(WSSourceExecutor.class);
	
	/**
	 * 
	 */
	public WSSourceExecutor() {
		// TODO Auto-generated constructor stub
	}
	
	public ResultSetHolder stream(Datasource ds, List<Attribute> attributes, String tableName) {
		try {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			IngestServiceService ingestService = new IngestServiceService();
			List<T> streams = (List<T>) ingestService.getIngestServicePort().stream();
			logger.info("Before dumping stream >>>>>>>>>> ");
			for (T stream : streams) {
				logger.info(stream);
			}
			return sparkExecutor.createAndRegisterDataset(streams, attributes, tableName);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
