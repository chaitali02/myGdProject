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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.PDFUtil;
import com.inferyx.framework.common.WorkbookUtil;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Document;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.enums.Layout;

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

	public String getDocumentFilePath(String metaObjType, BaseEntity metaObject, String execVersion)
			throws FileNotFoundException, IOException {
		String defaultFilePath = null;
		if (metaObjType != null && metaObjType.equalsIgnoreCase(MetaType.report.toString())) {
			defaultFilePath = Helper.getPropertyValue("framework.report.Path");
		} else {
			defaultFilePath = Helper.getPropertyValue("framework.file.download.path");
		}

		defaultFilePath = defaultFilePath.endsWith("/") ? defaultFilePath : defaultFilePath.concat("/");
		String metFilePath = String.format("%s/%s/%s/%s/", metaObject.getUuid(), metaObject.getVersion(), execVersion, "doc");
		return defaultFilePath.concat(metFilePath);
	}

	public String getDocumentFileName(String metaName, String execVersion, String fileExtension) {
		if (fileExtension != null && !fileExtension.isEmpty()
				&& fileExtension.equalsIgnoreCase(FileType.PDF.toString())) {
			return String.format("%s_%s.%s", metaName, execVersion, fileExtension);
		} else {
			return String.format("%s_%s.%s", metaName, execVersion, fileExtension);
		}
	}

	public boolean createPDF(String metaObjType, BaseEntity metaObject, Object metaExecObject,
			List<Map<String, Object>> data, String layout) throws IOException {
		try {
			LOGGER.info("creating PDF file for "+metaObject.getName()+"...");
			String execVersion = (String) metaExecObject.getClass().getMethod("getVersion").invoke(metaExecObject);
			String metaFilePath = getDocumentFilePath(metaObjType, metaObject, execVersion);

			File metaDocDir = new File(metaFilePath);
			if (!metaDocDir.exists()) {
				metaDocDir.mkdirs();
			}

			String metaFileName = getDocumentFileName(metaObject.getName(), execVersion, FileType.PDF.toString().toLowerCase());

			String filePathUrl = metaFilePath.concat(metaFileName);
			LOGGER.info("PDF file path: "+filePathUrl);
			
			PDDocument doc = null;
			if (metaObjType != null && metaObjType.equalsIgnoreCase(MetaType.report.toString())) {
				if (layout == null || (layout != null && layout.equalsIgnoreCase(Layout.PORTRAIT.toString()))) {
					doc = pdfUtil.getPDFDocForReport(data, (ReportExec) metaExecObject);
				} else {
					doc = pdfUtil.getLandscapePDFDocForReport(data, (ReportExec) metaExecObject);
				}
			} else {
				doc = pdfUtil.getPDFDoc(data);
			}
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

	public boolean createXLS(String metaObjType, BaseEntity metaObject, Object metaExecObject,
			List<Map<String, Object>> data) {
		try {
			LOGGER.info("creating EXCEL file for "+metaObject.getName()+"...");
			String execVersion = (String) metaExecObject.getClass().getMethod("getVersion").invoke(metaExecObject);
			String metaFilePath = getDocumentFilePath(metaObjType, metaObject, execVersion);

			File metaDocDir = new File(metaFilePath);
			if (!metaDocDir.exists()) {
				metaDocDir.mkdirs();
			}

			String metaFileName = getDocumentFileName(metaObject.getName(), execVersion, FileType.XLS.toString().toLowerCase());

			String filePathUrl = metaFilePath.concat(metaFileName);
			LOGGER.info("EXCEL file path: "+filePathUrl);
			
			Workbook workbook = null;
			if (metaObjType != null && metaObjType.equalsIgnoreCase(MetaType.report.toString())) {
				workbook = workbookUtil.getWorkbookForReport(data, (ReportExec) metaExecObject);
			} else {
				workbook = WorkbookUtil.getWorkbook(data);
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
	
//	public boolean createPDF(Document document) throws IOException {
//		try {
//			LOGGER.info("creating PDF file for ...");
////			String execVersion = (String) metaExecObject.getClass().getMethod("getVersion").invoke(metaExecObject);
////			String metaFilePath = getDocumentFilePath(metaObjType, metaObject, execVersion);
////
////			File metaDocDir = new File(metaFilePath);
////			if (!metaDocDir.exists()) {
////				metaDocDir.mkdirs();
////			}
////
////			String metaFileName = getDocumentFileName(metaObject.getName(), execVersion, FileType.PDF.toString().toLowerCase());
////
////			String filePathUrl = metaFilePath.concat(metaFileName);
//			String filePathUrl = document.getLocation();
//			LOGGER.info("PDF file path: "+filePathUrl);
//			
//			PDDocument doc = null;
//			if (document.getMetaObjType() != null && document.getMetaObjType().equalsIgnoreCase(MetaType.report.toString())) {
//				if (document.getLayout() == null || (document.getLayout() != null && document.getLayout().equals(Layout.PORTRAIT))) {
//					doc = pdfUtil.getPDFDocForReport(document.getData(), (ReportExec) metaExecObject);
//				} else {
//					doc = pdfUtil.getLandscapePDFDocForReport(document.getData(), (ReportExec) metaExecObject);
//				}
//			} else {
//				doc = pdfUtil.getPDFDoc(document.getData());
//			}
//			FileOutputStream fileOutPDF = new FileOutputStream(new File(filePathUrl));
//			doc.save(fileOutPDF);
//			fileOutPDF.close();
//			return true;
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	public boolean createXLS(Document document) {
//		try {
//			LOGGER.info("creating EXCEL file for "+metaObject.getName()+"...");
//			String execVersion = (String) metaExecObject.getClass().getMethod("getVersion").invoke(metaExecObject);
//			String metaFilePath = getDocumentFilePath(metaObjType, metaObject, execVersion);
//
//			File metaDocDir = new File(metaFilePath);
//			if (!metaDocDir.exists()) {
//				metaDocDir.mkdirs();
//			}
//
//			String metaFileName = getDocumentFileName(metaObject.getName(), execVersion, FileType.XLS.toString().toLowerCase());
//
//			String filePathUrl = metaFilePath.concat(metaFileName);
//			LOGGER.info("EXCEL file path: "+filePathUrl);
//			
//			Workbook workbook = null;
//			if (metaObjType != null && metaObjType.equalsIgnoreCase(MetaType.report.toString())) {
//				workbook = workbookUtil.getWorkbookForReport(data, (ReportExec) metaExecObject);
//			} else {
//				workbook = WorkbookUtil.getWorkbook(data);
//			}
//
//			FileOutputStream fileOut = new FileOutputStream(filePathUrl);
//			workbook.write(fileOut);
//			fileOut.close();
//			return true;
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			return false;
//		}
//	}
}
