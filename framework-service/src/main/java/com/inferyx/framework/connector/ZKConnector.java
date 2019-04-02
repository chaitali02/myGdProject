/**
 * 
 */
package com.inferyx.framework.connector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author joy
 *
 */
@Component
public class ZKConnector {
	
	@Resource
	ConcurrentHashMap<String, ZooKeeper> zkConnMap;
	@Autowired
	CommonServiceImpl commonServiceImpl;

	/**
	 * 
	 */
	public ZKConnector() {
		// TODO Auto-generated constructor stub
	}
	
	public ConnectionHolder getConnection(Datasource ds) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String connectionString = ds.getHost() + ":" + commonServiceImpl.getConfigValue("framework.kafka.topic.port");
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
