package com.inferyx.framework.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Dashboard;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DashboardServiceImpl;
import com.inferyx.framework.service.DashboardViewServiceImpl;
import com.inferyx.framework.view.metadata.DashboardView;

@RestController
@RequestMapping(value="/dashboard")
public class DashboardController {

	@Autowired private DashboardServiceImpl dashboardServiceImpl;
	@Autowired private DashboardViewServiceImpl dashboardViewServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	@RequestMapping(value="/getAll", method=RequestMethod.GET)
	public List<Dashboard> getAllDashboards(HttpServletResponse response,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws JsonProcessingException{
    	return dashboardServiceImpl.findAll();
	}
    	
	@RequestMapping(value="/getAllViews", method=RequestMethod.GET)
	public List<DashboardView> getAllDashboardViews(HttpServletResponse response,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonProcessingException{
    	List<Dashboard> dashboardList = dashboardServiceImpl.findAll();
    	return dashboardViewServiceImpl.getDashboardViews(dashboardList);
   }
}
