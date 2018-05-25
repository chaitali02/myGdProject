/**
 * 
 */
package com.inferyx.framework.factory;

import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.RowObj;

/**
 * @author joy
 *
 */
@Service
public class RowObjFactory {

	/**
	 * 
	 */
	public RowObjFactory() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param objects
	 * @return
	 */
	public RowObj createRowObj(Object... objects) {
		return new RowObj(objects);
	}
	
	

}
