/**
 * 
 *//*
package com.inferyx.framework.connector;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Datasource;

*//**
 * @author joy
 *
 *//*
@Component
public class ZKConnector {
	
	@Resource
	ConcurrentHashMap<String, ZooKeeper> zkConnMap;

	*//**
	 * 
	 *//*
	public ZKConnector() {
		// TODO Auto-generated constructor stub
	}
	
	public ConnectionHolder getConnection(Datasource ds) throws IOException {
		String connectionString = ds.getHost() + ":" + ds.getPort();
		ConnectionHolder connHolder = new ConnectionHolder();
		if (zkConnMap.containsKey(connectionString)) {
			connHolder.setConObject(zkConnMap.get(connectionString));
			return connHolder;
		}
		ZooKeeper zk = new ZooKeeper(connectionString, 10000, null);
		zkConnMap.put(connectionString, zk);
		connHolder.setConObject(zk);
        return connHolder;
	}

}
*/