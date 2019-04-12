/**
 * 
 */
package com.inferyx.framework.executor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.StreamInput;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.ws.client.disource.IngestServiceService;

/**
 * @author joy
 *
 */
public class WSSourceExecutor<T, K> {

	@Autowired
	ConnectionFactory connectionFactory;
	
	/**
	 * 
	 */
	public WSSourceExecutor() {
		// TODO Auto-generated constructor stub
	}
	
	public void stream(Datasource ds, StreamInput<T, K> streamInput) {
		try {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			IngestServiceService ingestService = new IngestServiceService();
			List<String> stream = ingestService.getIngestServicePort().stream();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
