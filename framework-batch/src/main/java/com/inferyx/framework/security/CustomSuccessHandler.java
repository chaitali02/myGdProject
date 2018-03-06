package com.inferyx.framework.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.service.SecurityServiceImpl;
import com.inferyx.framework.service.UserServiceImpl;

public class CustomSuccessHandler implements Filter {

	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;

	@Override
	public void destroy() {
		// Do nothing
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		//HttpServletRequest request = (HttpServletRequest) req;

		/*SessionContext sessionContext = (SessionContext) request.getSession().getAttribute("sessionContext");
		securityServiceImpl.setAppRole("d7c11fd7-ec1a-40c7-ba25-7da1e8b730cb",
				"d04716df-e96a-419f-9118-c81342b47f87");		
		System.out.println(request.getSession().getAttribute("sessionContext"));*/
		chain.doFilter(req, res);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// Do nothing
	}

	public MetaIdentifierHolder getSelectedApp(MetaIdentifierHolder appInfo) {

		return appInfo;
	}

}
