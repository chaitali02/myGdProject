package com.inferyx.framework.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class CORSFilter extends OncePerRequestFilter {
    private static final Log LOG = LogFactory.getLog(CORSFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	HttpServletRequest req= (HttpServletRequest) request;
    	HttpSession session = req.getSession(false);
    	
    	System.out.println("URL: "+request.getRequestURL()+ (session==null? "null session" : "session not null"));
        response.addHeader("Access-Control-Allow-Origin", "*");
       // response.addHeader("Content-type", "application/json");
        if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
            LOG.trace("Sending Header....");
            // CORS "pre-flight" request
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, sessionId, withCredentials");
         response.addHeader("Access-Control-Max-Age", "1");
        }
        filterChain.doFilter(request, response);
    }

}
