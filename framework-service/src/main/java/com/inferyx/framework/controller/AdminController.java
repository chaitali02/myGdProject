package com.inferyx.framework.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.domain.Export;
import com.inferyx.framework.domain.Import;
import com.inferyx.framework.service.AdminServiceImpl;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.ExportServiceImpl;
import com.inferyx.framework.service.ImportServiceImpl;

@RestController
@RequestMapping(value="/admin")
public class AdminController {
	
	@Autowired
	AdminServiceImpl adminServiceImpl;
	@Autowired
	ExportServiceImpl exportServiceImpl;
	@Autowired
	ImportServiceImpl importServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;

	@RequestMapping(value="/getTaskThreadMap", method=RequestMethod.GET)
    public ConcurrentHashMap getTaskThreadMap() throws Exception {
		return adminServiceImpl.getTaskThreadMap();
	}
	@RequestMapping(value="settings/get", method=RequestMethod.GET)
    public String getSettings( @RequestParam("type") String type,@RequestParam(value = "action", required = false) String action) throws Exception {
		return adminServiceImpl.getSettings();
	}
	
	@RequestMapping("export/download")
    public boolean download(HttpServletRequest request, HttpServletResponse response,@RequestParam(value = "uuid") String uuid){
		try {
			response = exportServiceImpl.download(uuid, response);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
    }
	@RequestMapping(value="settings/submit",method = RequestMethod.POST)
    public boolean setSettings(@RequestBody Map<String, Object>document, @RequestParam("type") String type,
			@RequestParam(value = "action", required = false) String action, HttpServletRequest request) throws Exception {
		 try {
			adminServiceImpl.setSettings(document);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@RequestMapping(value="import/validate", method = RequestMethod.POST)
	public @ResponseBody String validate(@RequestBody Map<String, Object>document,
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "fileName", required = false) String fileName) throws IOException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, JSONException  {
		ObjectMapper mapper = new ObjectMapper();
		Import imprt = mapper.convertValue(document, Import.class);
		return importServiceImpl.validateMetaInfoDependencies(imprt, fileName);
	}
	
	@RequestMapping(value="import/submit", method = RequestMethod.POST)
	public @ResponseBody String importSubmit(@RequestBody Object metaObject,
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "fileName", required = false) String fileName) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Import imprt = mapper.convertValue(metaObject, Import.class);
		return  mapper.writeValueAsString(importServiceImpl.save(imprt, fileName));
	}
	
	@RequestMapping(value="export/submit", method = RequestMethod.POST)
	public @ResponseBody String exportSubmit(@RequestBody Object metaObject,
			@RequestParam(value = "type", required=false) String type,
			@RequestParam(value = "action", required = false) String action
			) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Export export = mapper.convertValue(metaObject, Export.class);
		return  mapper.writeValueAsString(exportServiceImpl.save(export));
	}	
	
	@RequestMapping(value = "/getAllByMetaList", method = RequestMethod.GET)
	public List<Object> getAllByMetaList(@RequestParam(value = "type") String[] type,
										 @RequestParam(value = "action", required = false) String action){
		return commonServiceImpl.getAllByMetaList(type);
		
	}
}

