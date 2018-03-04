/**
 * 
 */
package com.inferyx.framework.domain;

/**
 * @author joy
 *
 */
/**
 * Framework's custom ThreadLocal class
 * @author joy
 *
 */
public class FrameworkThreadLocal {
	
	private static ThreadLocal<SessionContext> sessionContext = new ThreadLocal<>();

	/**
	 * 
	 */
	public FrameworkThreadLocal() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the sessionContext
	 */
	public static ThreadLocal<SessionContext> getSessionContext() {
		return sessionContext;
	}

}
