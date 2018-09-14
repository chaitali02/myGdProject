/**
 * 
 */
package com.inferyx.framework.test;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.SqoopInput;
import com.inferyx.framework.executor.SqoopExecutor;

/**
 * @author joy
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:/framework-batch.xml"})
public class SqoopImportTest {
	
	@Autowired
	private SqoopExecutor sqoopExecutor;
	private static final String connectionString = "jdbc:mysql://127.0.0.1:3306/mydatabase";
	private static final String username = "root";
	private static final String password = "root";

	/**
	 * 
	 */
	public SqoopImportTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void importTest() throws IOException {
		SqoopInput input = new SqoopInput();
		Datasource datasource = new Datasource();
		datasource.setHost("127.0.0.1");
		datasource.setPort("3306");
		datasource.setSid("mydatabase");
		datasource.setDbname("mydatabase");
		datasource.setUsername(username);
		datasource.setPassword(password);
		datasource.setType("MYSQL");
		input.setSourceDs(datasource);
		input.setTable("customer");
		input.setTargetDirectory("/user/cloudera/ingest/raw/customers");
		input.setImportIntended(true);
		sqoopExecutor.execute(input);
//		input.setso
	}

}
