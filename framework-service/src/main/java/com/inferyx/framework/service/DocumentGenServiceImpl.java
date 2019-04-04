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
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.PDFUtil;
import com.inferyx.framework.common.WorkbookUtil;
import com.inferyx.framework.domain.Document;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ReportExec;

/**
 * @author Ganesh
 *
 */
@Service
public class DocumentGenServiceImpl {
	public static final Logger LOGGER = Logger.getLogger(DocumentGenServiceImpl.class);

	@Autowired
	private PDFUtil pdfUtil;
	@Autowired
	private WorkbookUtil workbookUtil;
	@Autowired
	private CommonServiceImpl commonServiceImpl;
	
	public boolean createDocument(Document document) throws IOException {
		if(document.getDocumentType().equalsIgnoreCase(FileType.PDF.toString())) {
			return createPDF(document);
		} else if(document.getDocumentType().equalsIgnoreCase(FileType.XLS.toString())) {
			return createXLS(document);
		} else {
			throw new RuntimeException("Please provide document type ...");
		}
	}
	
	public boolean createPDF(Document document) throws IOException {
		try {
			LOGGER.info("creating PDF file ...");

			String filePathUrl = document.getLocation();
			LOGGER.info("PDF file path: "+filePathUrl);
						
			createFilePathIfNotExists(filePathUrl, document.getFileName());
			
			if(!StringUtils.isBlank(document.getOrgLogoName())) {
				String logoDefaultPath = commonServiceImpl.getConfigValue("framework.image.logo.Path");
				logoDefaultPath = logoDefaultPath.endsWith("/") ? logoDefaultPath : logoDefaultPath.concat("/");
				String logoRelativePath = document.getOrgLogoName();
				if(logoRelativePath.startsWith("/")) {
					logoRelativePath = logoRelativePath.substring(1);
				} 
				
				String logoAbsolutePath = logoDefaultPath.concat(logoRelativePath);
				document.setOrgLogoName(logoAbsolutePath);
			}
			
			PDDocument doc = pdfUtil.createPDF(document);
			
			FileOutputStream fileOutPDF = new FileOutputStream(new File(filePathUrl));
			doc.save(fileOutPDF);
			fileOutPDF.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	public boolean createFilePathIfNotExists(String filePathUrl, String fileName) {
		String metObjDirPath = filePathUrl.replaceAll(fileName, "");
		File metObjDocDir = new File(metObjDirPath);
		if (!metObjDocDir.exists()) {
			metObjDocDir.mkdirs();
		}
		return true;
	}
	
	public boolean createXLS(Document document) {
		try {
			LOGGER.info("creating EXCEL file ...");
			
			String filePathUrl = document.getLocation();
			LOGGER.info("EXCEL file path: "+filePathUrl);
			
			createFilePathIfNotExists(filePathUrl, document.getFileName());
			
			Workbook workbook = null;
			if (document.getMetaObjType() != null && document.getMetaObjType().equalsIgnoreCase(MetaType.report.toString())) {
				workbook = workbookUtil.getWorkbookForReport(document.getData(), (ReportExec) document.getMetExecObject());
			} else {
				workbook = WorkbookUtil.getWorkbook(document.getData());
			}

			FileOutputStream fileOut = new FileOutputStream(filePathUrl);
			workbook.write(fileOut);
			fileOut.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
}
