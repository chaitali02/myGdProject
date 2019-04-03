/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.inferyx.framework.domain.FileRefHolder;
import com.inferyx.framework.service.DownloadServiceImpl;
import com.inferyx.framework.service.MetadataServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;
import com.inferyx.framework.service.UploadServiceImpl;

@Controller
@RequestMapping(value="/file")
public class FileController {
	@Autowired
	MetadataServiceImpl metadataServiceImpl;
	@Autowired
	ModelServiceImpl modelServiceImpl;
	@Autowired
	UploadServiceImpl uploadServiceImpl;
	@Autowired
	DownloadServiceImpl downloadServiceImpl;
	
	@RequestMapping(value = "/uploadOrgLogo", method = RequestMethod.POST)
	public @ResponseBody FileRefHolder uploadOrgLogo(@RequestParam("file") MultipartFile multiPartFile,
			@RequestParam("fileName") String filename, @RequestParam(value = "uuid", required = false) String uuid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action)
			throws Exception {

		return metadataServiceImpl.uploadOrgLogo(multiPartFile, filename, uuid, type);
	}
	
	
	@RequestMapping(value = "/uploadProfileImage", method = RequestMethod.POST)
	public @ResponseBody String uploadProfileImage(@RequestParam("file") MultipartFile file, 
			@RequestParam("fileName") String filename,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action) throws IOException {
		String filePath = "src/main/webapp/app/avatars/" + filename;
		File dest = new File(filePath);
		file.transferTo(dest);
		return "";
	} 

	
	@RequestMapping(value = "/upload", headers = ("content-type=multipart/form-data; boundary=abcd"), method = RequestMethod.POST)
	public @ResponseBody String upload(@RequestParam("file") MultipartFile file,
									   @RequestParam(value = "extension") String extension,
									   @RequestParam(value = "fileType") String fileType,
									   @RequestParam(value = "type", required = false) String type,
									   @RequestParam(value = "fileName", required = false) String fileName) throws FileNotFoundException, IOException, ParseException, JSONException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
	 return modelServiceImpl.upload(file, extension, fileType, fileName, type);
	}
	
	@RequestMapping(value = "/uploadGen", headers = ("content-type=multipart/form-data; boundary=abcd"), method = RequestMethod.POST)
	public @ResponseBody FileRefHolder uploadGen(@RequestParam("file") MultipartFile file,
									   @RequestParam(value = "extension") String extension,
									   @RequestParam(value = "fileType") String fileType,
									   @RequestParam(value = "uuid", required = false) String uuid,
									   @RequestParam(value = "type", required = false) String type,
									   @RequestParam(value = "fileName", required = false) String fileName) throws Exception {
	 return uploadServiceImpl.uploadGen(file, extension, fileType, fileName, type, uuid);
	}
	
	
	
/*	public HttpServletResponse download(
			@RequestParam(value = "fileType") String fileType,
			@RequestParam(value = "filePath") String filePath, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			response = downloadServiceImpl.download(fileType, filePath, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}
	*/
	
	@RequestMapping(value="/download",method = RequestMethod.GET)
    public HttpServletResponse download(@RequestParam(value = "fileType",required = false) String fileType,
    						@RequestParam(value = "fileName",required = false) String fileName,
    						HttpServletResponse response
    						){
		try {
			response = downloadServiceImpl.download(fileType, fileName, response);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
    }
	
}



