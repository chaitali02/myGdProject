/**
 * 
 */
package com.inferyx.router;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author joy
 *
 */
@RestController
@RequestMapping("/predict")
public class PredictController {
	
	public static final Logger logger = Logger.getLogger(PredictController.class);

	/**
	 * 
	 */
	public PredictController() {
		// TODO Auto-generated constructor stub
	}
	
	 @RequestMapping(value = "/hello/", method = RequestMethod.GET)
	    public String helloWorld() {
	        return "Hello World";
	    }
	

}
