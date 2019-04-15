/**
 * 
 */
package com.inferyx.framework.executor;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.service.FrameworkThreadServiceImpl;
//import com.inferyx.framework.ws.client.disource.IngestServiceService;

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
	
	public static void main(String[] args) throws JSONException, ParseException, IOException {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:framework-batch.xml");
		WSSourceExecutor wsSourceExecutor = context.getBean(WSSourceExecutor.class);
		FrameworkThreadServiceImpl frameworkThreadServiceImpl = context.getBean(FrameworkThreadServiceImpl.class);
		frameworkThreadServiceImpl.setSession("ypalrecha");
		List<Attribute> attributes = new ArrayList<>();
		Attribute attr = new Attribute();
		attr.setAttributeId(0);
		attr.setAttrUnitType("String");
		attr.setName("custid");
		attributes.add(attr);
		attr = new Attribute();
		attr.setAttributeId(1);
		attr.setAttrUnitType("String");
		attr.setName("fname");
		attributes.add(attr);
		attr = new Attribute();
		attr.setAttributeId(2);
		attr.setAttrUnitType("String");
		attr.setName("lname");
		attributes.add(attr);
		wsSourceExecutor.stream(null, attributes, "table1");
	}
	
	/**
	 * 
	 */
	public WSSourceExecutor() {
		// TODO Auto-generated constructor stub
	}
	
	public ResultSetHolder stream(Datasource ds, List<Attribute> attributes, String tableName) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String []arr = null;
			int count = 0;
			LinkedHashMap<String, String> readMap = new LinkedHashMap<>();
			List<String[]> readList = new ArrayList<>();
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
//			IngestServiceService ingestService = new IngestServiceService();
//			List<T> streams = (List<T>) ingestService.getIngestServicePort().stream();
			logger.info("Before dumping stream >>>>>>>>>> ");
//			for (T stream : streams) {
//				logger.info(stream);
//				readMap = mapper.readValue((String)stream, readMap.getClass());
//				logger.info(" Map >>> " + readMap);
//				arr = new String[readMap.size()];
//				count = 0;
//				for (Entry<String, String> val : readMap.entrySet()) {
//					arr[count] = val.getValue();
//					count++;
//				}
//				readList.add(arr);
//			}
			return sparkExecutor.createAndRegisterDataset(readList, attributes, tableName);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}