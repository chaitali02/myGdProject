/**
 * 
 */
package com.inferyx.framework.executor;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.connector.SqoopConnector;
import com.inferyx.framework.domain.SqoopInput;

/**
 * @author joy
 *
 */
@Service
public class SqoopExecutor {
	
	@Autowired
	private SqoopConnector sqoopConnector;

	/**
	 * 
	 */
	public SqoopExecutor() {
		// TODO Auto-generated constructor stub
	}
	
	public Object execute(Object input) throws IOException {
		SqoopInput sqoopInput = null;
		if (input == null) {
			return null;
		}
		if (input instanceof SqoopInput) {
			sqoopInput = (SqoopInput) input;
			sqoopConnector.getConnection(sqoopInput.getSourceDs()).getConObject();
		}
		return null;
	}

}
