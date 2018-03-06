package com.inferyx.framework.security;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.ILogDao;
import com.inferyx.framework.domain.Log;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.LogServiceImpl;
import com.inferyx.framework.service.MessageServiceImpl;
import com.inferyx.framework.service.MessageStatus;
import com.inferyx.framework.service.SessionServiceImpl;

public class ApiLogFilter extends GenericFilterBean {
	
	@Autowired
	LogServiceImpl logServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SessionServiceImpl sessionServiceImpl;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	
	static final Logger logger = Logger.getLogger(ApiLogFilter.class);
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException { 
		String value = Helper.getPropertyValue("framework.api.log");
		if(value.equalsIgnoreCase("true")) {
			HttpServletRequest req= (HttpServletRequest) request;
	    	HttpServletResponse res=(HttpServletResponse) response;
	    	String requestedUrl = req.getRequestURL().toString();
	    	logger.info("Requested URL: "+requestedUrl);
	    	Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
	    	if(authentication != null){
	    		//logger.info("User name : " + authentication.getName());
	    	}    	
	    	HttpSession session = req.getSession(false);
	    	if(session == null) {
	    		if(requestedUrl.toLowerCase().contains("app".toLowerCase()) 
	    				|| requestedUrl.toLowerCase().contains("/metadata/validateUser".toLowerCase()) ) {
//	    				|| requestedUrl.toLowerCase().contains("/security/logoutSession".toLowerCase())) {
	    			logger.info("session is null 1.0");
	    			chain.doFilter(request, response);
	    		}else {
	    			logger.info("session is null 1.1");
	    			try {
	    				Message message = new Message("419", MessageStatus.FAIL.toString(), "Session expired.");
						//Message savedMessage = messageServiceImpl.save(message);
						//PrintWriter out = response.getWriter();
						ObjectMapper mapper = new ObjectMapper();
						String messageJson = mapper.writeValueAsString(message);
						res.setContentType("application/json");
						//response.setCharacterEncoding("UTF-8");
						res.setStatus(419);
						//out.print(messageJson);
						///out.flush();
						res.getOutputStream().write(messageJson.getBytes());
						//response.getOutputStream().flush();
						res.getOutputStream().close(); 
	    			}catch (Exception e) {
						// TODO: handle exception
	    				e.printStackTrace();
					}	   			
	    		}
	    	}else  
	        	chain.doFilter(request, response);
		}else
			chain.doFilter(request, response);
    	
	}
}
