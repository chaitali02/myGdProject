/**
 * 
 */
package com.inferyx.framework.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author joy
 *
 */
@Service
public class DagVisitorFactory {

	@Autowired
	JsonVisitor jsonVisitor;
	@Autowired
	PrettyPrintVisitor prettyPrintVisitor;
	
	/**
	 * 
	 */
	public DagVisitorFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public DagVisitor getInstance(String type) {
		switch (type) {
		case "JSON":
			return jsonVisitor;
		case "PRETTY":
			return prettyPrintVisitor;
		default:
			return jsonVisitor;
		}
	}

}
