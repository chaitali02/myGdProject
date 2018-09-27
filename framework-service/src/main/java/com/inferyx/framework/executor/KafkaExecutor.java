/**
 * 
 */
package com.inferyx.framework.executor;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.ZKConnector;
import com.inferyx.framework.domain.Datasource;

/**
 * @author joy
 *
 */
@Service
public class KafkaExecutor {
	
	@Autowired
	private ZKConnector zkConnector;

	/**
	 * 
	 */
	public KafkaExecutor() {
		// TODO Auto-generated constructor stub
	}
	
	public List<String> getTopics(Datasource ds) throws IOException {
		ConnectionHolder connHolder = zkConnector.getConnection(ds);
		List<String> topics = null;
		if (!(connHolder.getConObject() instanceof ZooKeeper)) {
			throw new IOException("Framework exception : Streaming topics not available");
		}
		ZooKeeper zk = (ZooKeeper) connHolder.getConObject();
        try {
			topics = zk.getChildren("/brokers/topics", false);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			throw new IOException("Framework Exception : Could not extract streaming topics", e);
		}
        return topics;
	}

}
