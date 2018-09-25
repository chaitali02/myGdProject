/**
 * 
 */
package com.inferyx.framework.demo.kafka;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author joy
 *
 */
public class KafkaTopicList {

	/**
	 * 
	 */
	public KafkaTopicList() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		listBrokers();
	}
	
	public static List<String> listBrokers() throws IOException, KeeperException, InterruptedException {
		
		ZooKeeper zk = new ZooKeeper("localhost:2181", 10000, null);
        List<String> topics = zk.getChildren("/brokers/topics", false);
        for (String topic : topics) {
            System.out.println(topic);
        }
        return topics;
	}

}
