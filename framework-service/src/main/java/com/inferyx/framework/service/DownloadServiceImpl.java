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
package com.inferyx.framework.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.Document;
import com.inferyx.framework.domain.DownloadExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Organization;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.Alignment;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;

/**
 * @author Ganesh
 *
 */
@Service
public class DownloadServiceImpl {

	private static final Logger LOGGER = Logger.getLogger(DownloadServiceImpl.class);

	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DocumentGenServiceImpl documentGenServiceImpl;
	@Autowired
	private MetadataServiceImpl metadataServiceImpl;
	@Autowired
	private Helper helper;

	public DownloadExec create(MetaIdentifierHolder dependsOn) throws Exception {
		LOGGER.info("Creating downloadExec ...");
		DownloadExec downloadExec = new DownloadExec();
		downloadExec.setBaseEntity();
		downloadExec.setDependsOn(dependsOn);

		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec,
				Status.Stage.PENDING);

		return downloadExec;
	}

	public DownloadExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		LOGGER.info("Parsing downloadExec ...");
		DownloadExec downloadExec = (DownloadExec) baseExec;
		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec,
				Status.Stage.STARTING);
		downloadExec.setExecParams(execParams);
		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec,
				Status.Stage.READY);
		return downloadExec;
	}

	public HttpServletResponse download(String format, HttpServletResponse response, RunMode runMode,
			List<Map<String, Object>> data, MetaIdentifierHolder dependsOn, Layout layout, BaseExec baseExec,
			boolean checkFileExistance, String defaultPathKey, String parameters, MetaIdentifierHolder metaObjectHolder,
			Map<String, String> otherDetails) throws Exception {
		LOGGER.info("Inside download method....");
		
		DownloadExec downloadExec = create(dependsOn);
		downloadExec = parse(downloadExec, null, null);
		try {
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec,
					Status.Stage.RUNNING);
			
			MetaIdentifier metaObjectMI = metaObjectHolder.getRef();
			BaseEntity metaObject = (BaseEntity) commonServiceImpl.getOneByUuidAndVersion(metaObjectMI.getUuid(),
					metaObjectMI.getVersion(), metaObjectMI.getType().toString(), "N");

			if (StringUtils.isBlank(format)) {
				throw new RuntimeException("Format not provided ...");
			}

			format = Helper.mapFileFormat(format);

			String execVersion = null;
			if(baseExec != null) {
				execVersion = baseExec.getVersion();
			} else {
				execVersion = downloadExec.getVersion();
			}
			
			String filePathUrl = Helper.getDocumentFilePath(
					commonServiceImpl.getConfigValue(defaultPathKey), metaObject.getUuid(),
					metaObject.getVersion(), execVersion, metaObject.getName(), format, true);
			String fileName = Helper.getDocumentFileName(metaObject.getName(), execVersion, format);

			downloadExec.setLocation(filePathUrl);
			
			Workbook workbook = null;
			PDDocument doc = null;
			
			File reportFile = new File(filePathUrl);
			if (checkFileExistance && reportFile.exists()) {
				if (format.equalsIgnoreCase(FileType.PDF.toString())) {
					doc = PDDocument.load(reportFile);
				} else if (format.equalsIgnoreCase(FileType.XLS.toString())) {
					workbook = WorkbookFactory.create(reportFile);
				} else {
					throw new RuntimeException("Unsupported file format provided ...");
				}
			} else {
				Document document = new Document();
				document.setLocation(filePathUrl);
				document.setLayout(layout);
				document.setData(data);
				document.setMetaObjType(metaObjectMI.getType().toString());
				
				if(otherDetails != null) {
					document.setLayout(layout);
					document.setHeader(otherDetails.get("doc_header"));
					document.setHeaderAlignment(otherDetails.get("doc_header_align"));
					document.setFooter(otherDetails.get("doc_footer"));
					document.setFooterAlignment(otherDetails.get("doc_footer_align"));
					document.setTitle(otherDetails.get("doc_title"));
					document.setMetExecObject(baseExec);
					document.setParameters(parameters);
					document.setGenerationDate(baseExec.getCreatedOn());					
				} else {
					document.setHeader("Confidential document");
					document.setHeaderAlignment(Alignment.CENTER.toString());
					document.setFooter("All rights reserved");
					document.setFooterAlignment(Alignment.RIGHT.toString());
					document.setTitle(metaObject.getName());
					document.setMetExecObject(downloadExec);
					document.setParameters("");
					SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
					document.setGenerationDate(formatter.format(new Date()));
				}
				
				document.setDocumentType(format);
				document.setName(metaObject.getName());
				document.setDescription(!StringUtils.isBlank(metaObject.getDesc()) ? metaObject.getDesc() : "");
				document.setFileName(fileName);
				document.setLogoAlignment(Alignment.RIGHT);
				document.setTitleAlignment(Alignment.CENTER);
				
				Organization organization = commonServiceImpl.getOrgInfoByCurrentApp();
				document.setOrgName(organization.getName());
				document.setOrgLogoName(organization.getLogoName());
				document.setOrgAddress(commonServiceImpl.getOrganizationAddr(organization.getAddress()));
				
				File metaObjFile = new File(filePathUrl);
				boolean isDocCreated = documentGenServiceImpl.createDocument(document);

				if (!isDocCreated) {
					throw new RuntimeException(
							(format != null ? format.toUpperCase() : "Document") + " creation failed...");
				}

				if (format.equalsIgnoreCase(FileType.PDF.toString())) {
					doc = PDDocument.load(metaObjFile);
				} else if (format.equalsIgnoreCase(FileType.XLS.toString())) {
					workbook = WorkbookFactory.create(metaObjFile);
				}
			}

			if (response != null) {
				response = writeDocumentToResponse(response, format, doc, workbook, fileName, runMode);
			}

			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec,
					Status.Stage.COMPLETED);

			return response;
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			message = !StringUtils.isBlank(message) ? message : "Can not download file ...";
			LOGGER.error(message);
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec,
					Status.Stage.FAILED);
			response.setStatus(300);
			throw new RuntimeException(message);
		}
	}

	public HttpServletResponse writeDocumentToResponse(HttpServletResponse response, String format, PDDocument doc,
			Workbook workbook, String fileName, RunMode runMode) throws IOException {
		ServletOutputStream servletOutputStream = response.getOutputStream();
		if (format.equalsIgnoreCase(FileType.PDF.toString())) {
			response.setContentType("application/pdf");
			response.setHeader("Content-disposition", "attachment");
			response.setHeader("filename", fileName);
			doc.save(servletOutputStream);
			doc.close();
		} else if (format.equalsIgnoreCase(FileType.XLS.toString())) {
			response.setContentType("application/xml");
			response.setHeader("Content-disposition", "attachment");
			response.setHeader("filename", fileName);
			workbook.write(servletOutputStream);
			workbook.close();
		} else {
			throw new RuntimeException("Unsupported file format provided ...");
		}

		return response;
	}

	public HttpServletResponse download(String fileType, String filePath, HttpServletResponse response)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, JSONException, ParseException, IOException {
		
		
		try {
			FileType type = Helper.getFileType(fileType);

			String directoryLocation = helper.getFileDirectoryByFileType(type);
			filePath = directoryLocation + "/" + filePath;
			File file = new File(filePath);
			if (file.exists()) {
				String mimeType = null;// context.getMimeType(file.getPath());

				if (mimeType == null) {
					mimeType = "application/octet-stream";
				}

				response.setContentType(mimeType);
				response.setContentLength((int) file.length());
				response.setContentType("application/xml charset=utf-16");
				response.setHeader("Content-disposition", "attachment");
				response.setHeader("filename", filePath);
				ServletOutputStream os = response.getOutputStream();
				FileInputStream fis = new FileInputStream(file);
				Long fileSize = file.length();
				byte[] buffer = new byte[fileSize.intValue()];
				int b = -1;

				while ((b = fis.read(buffer)) != -1) {
					os.write(buffer, 0, b);
				}

				fis.close();
				os.close();
			} else {
				response.setStatus(300);
				throw new FileNotFoundException("Requested " + filePath + " file not found!!");
			}
		} catch (

		IOException e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Requested " + filePath + " file not found!!", null);
			throw new IOException((message != null) ? message : "Requested " + filePath + " file not found!!");
		}
		return response;
	}
}
