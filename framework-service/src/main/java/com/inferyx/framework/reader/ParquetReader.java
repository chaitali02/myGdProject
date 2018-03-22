package com.inferyx.framework.reader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DataFrameHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.service.DataFrameService;
import com.inferyx.framework.service.MessageServiceImpl;
import com.inferyx.framework.service.MessageStatus;
import com.inferyx.framework.service.ProfileServiceImpl;

@Component
public class ParquetReader implements IReader
{
	/*@Autowired
	ExecutorFactory execFactory;*/
	@Autowired
	DataFrameService dataFrameService;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	
	/*DataFrame df;*/
	/*HiveContext hiveContext;*/
	/*String tableName="";
	DataFrameHolder dfm;*/

	static final Logger logger = Logger.getLogger(ParquetReader.class);
	
	@Override
	public DataFrameHolder read(Datapod dp, DataStore datastore, HDFSInfo hdfsInfo, Object conObject, Datasource ds) throws IOException {
		String tableName="";
		DataFrameHolder dfmh = new DataFrameHolder();
		try {
			String filePath = datastore.getLocation();
			String hdfsLocation = String.format("%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
			if (!filePath.contains(hdfsLocation)) 
				filePath = String.format("%s%s", hdfsLocation, filePath);
			/*DataFrameHolder dfm = dataFrameService.getaDataFrameHolder(filePath, conObject);*/
			Dataset<Row> df = null;
			SparkSession sparkSession = (SparkSession) conObject;
			DataFrameReader reader = sparkSession.read();
			df = reader.load(filePath);
			tableName = Helper.genTableName(filePath);
			dfmh.setDataframe(df);
			dfmh.setTableName(tableName);
		}catch (Exception e) {
//			e.printStackTrace();
			String errorMessage = e.getMessage();
			if(errorMessage.contains("Path does not exist:")) {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
						.getRequestAttributes();
				if (requestAttributes != null) {
					HttpServletResponse response = requestAttributes.getResponse();
					if (response != null) {
						response.setContentType("application/json");
						Message message = new Message("404", MessageStatus.FAIL.toString(), "File path does not exist.");
						try {
							Message savedMessage = messageServiceImpl.save(message);
							ObjectMapper mapper = new ObjectMapper();
							String messageJson = mapper.writeValueAsString(savedMessage);
							response.setContentType("application/json");
							response.setStatus(404);
							response.getOutputStream().write(messageJson.getBytes());
							response.getOutputStream().close();
						} catch (IllegalAccessException e1) {
							e1.printStackTrace();
						} catch (IllegalArgumentException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						} catch (NoSuchMethodException e1) {
							e1.printStackTrace();
						} catch (SecurityException e1) {
							e1.printStackTrace();
						} catch (NullPointerException e1) {
							e1.printStackTrace();
						} catch (JSONException e1) {
							e1.printStackTrace();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					} else
						logger.info("HttpServletResponse response is \"" + null + "\"");
				} else
					logger.info("ServletRequestAttributes requestAttributes is \"" + null + "\"");
				throw new IOException("File path does not exist.");
			} 
			}
		
		return dfmh;
	}	

}
