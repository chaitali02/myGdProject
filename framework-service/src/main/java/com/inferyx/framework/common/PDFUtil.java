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
package com.inferyx.framework.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.service.CommonServiceImpl;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;

import java.awt.Color;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

/**
 * @author Ganesh
 *
 */
@Component
public class PDFUtil {

	static final Logger logger = Logger.getLogger(PDFUtil.class);
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	private PDPageContentStream contentStream; 
	
	public PDDocument getLandscapePDFDocForReport(List<Map<String, Object>> resultList, ReportExec reportExec)
			throws IOException {

		float y = 400.0f;
		float margin = 50.0f;
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(reportExec.getDependsOn().getRef().getUuid(),
				reportExec.getDependsOn().getRef().getVersion(),
				reportExec.getDependsOn().getRef().getType().toString());
		PDDocument pdfDoc = new PDDocument();
		PDPage pdfPage = new PDPage(PDRectangle.A4);
//		pdfPage.setRotation(90);
//		pdfPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
		pdfPage.setMediaBox(new PDRectangle(pdfPage.getMediaBox().getHeight() - (2 * margin), pdfPage.getMediaBox().getWidth() - 2.0f * margin));
		pdfDoc.addPage(pdfPage);

		contentStream = new PDPageContentStream(pdfDoc, pdfPage);
//		contentStream.transform(new Matrix(0, 1, -1, 0, pdfPage.getMediaBox().getWidth(), 0));

		//writting title
		final float titleWidth = pdfPage.getMediaBox().getWidth() - 2.0f * margin;
		float titleX = ((titleWidth - (report.getTitle().length()+margin))/2);
		if(titleX < 0) {
			titleX = margin;
		}
		PDStreamUtils.write(contentStream, report.getTitle(), (PDFont)PDType1Font.HELVETICA, 12.0f, titleX, y, Color.black);
		y -= 50.0f;
		
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10.0f);
		
		//writting other contents
		PDStreamUtils.write(contentStream, "Report Name".concat(": ").concat(report.getName()),
				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
		y -= 15.0f;
		
		PDStreamUtils.write(contentStream,
				"Report Description".concat(": ").concat(report.getDesc() != null ? report.getDesc() : ""),
				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
		y -= 15.0f;

		StringBuilder reportParameters = new StringBuilder("");
		if (report.getParamList() != null && reportExec.getExecParams() != null
				&& reportExec.getExecParams().getParamListInfo() != null
				&& !reportExec.getExecParams().getParamListInfo().isEmpty()) {

			for (ParamListHolder paramListHolder : reportExec.getExecParams().getParamListInfo()) {
				String paramName = paramListHolder.getParamName();
				String paramValue = paramListHolder.getParamValue().getValue();
				reportParameters.append(paramName).append(": ").append(paramValue).append(", ");
			}

			if (reportParameters.length() > 0) {
				reportParameters = new StringBuilder(
						reportParameters.substring(0, reportParameters.toString().lastIndexOf(",")));
			}

		}

		PDStreamUtils.write(contentStream, "Report Parameters: ".concat(reportParameters.toString()),
				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
		y -= 15.0f;
		
		PDStreamUtils.write(contentStream, "Report Generation Date: ".concat(reportExec.getCreatedOn()),
				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
		y -= 30.0f;
		
		//writting header
		final float headerWidth = pdfPage.getMediaBox().getWidth() - 2.0f * margin;
		float headerX = ((headerWidth - (report.getHeader().length()+margin))/2);
		if(headerX < 0) {
			headerX = margin;
		}
		PDStreamUtils.write(contentStream, report.getHeader(), (PDFont)PDType1Font.HELVETICA, 10.0f, headerX, y, Color.black);
		y -= 30.0f;
		
		pdfPage = new PDPage(PDRectangle.A4);
		pdfPage.setRotation(90);
//		pdfPage.setMediaBox(new PDRectangle(pdfPage.getMediaBox().getHeight() - (2 * margin), pdfPage.getMediaBox().getWidth() - 2.0f * margin));
		pdfPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
		pdfDoc.addPage(pdfPage);

		//adding column names into list as row
		Map<String, Object> colMap = new LinkedHashMap<>();
		for(String colName : resultList.get(0).keySet()) {
			colMap.put(colName, colName);
		}
		
//		resultList.add(0, colMap);
		
		//writting data into table
//		final float tableWidth = pdfPage.getMediaBox().getWidth();
//		float yStartNewPage = pdfPage.getMediaBox().getHeight();
		final float tableWidth = pdfPage.getMediaBox().getHeight() - 3.00f * margin;
//		final float tableWidth = pdfPage.getMediaBox().getWidth() - 2.0f * margin;
//		float yStartNewPage = pdfPage.getMediaBox().getHeight() - (2 * margin);
		BaseTable baseTable = new BaseTable(545.0f, 545.0f, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
//		BaseTable baseTable = new BaseTable(y, yStartNewPage, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
	    
	    
	    Row<PDPage> headerRow = baseTable.createRow(15f);
	    for(String colName : colMap.keySet()) {
		    Cell<PDPage> cell = headerRow.createCell(10, colMap.get(colName).toString());
		    cell.setFont(PDType1Font.HELVETICA_BOLD);
		    cell.setFillColor(Color.WHITE);
	    }
	    baseTable.addHeaderRow(headerRow);
	    
	    for(final Map<String, Object> row : resultList) {
	    	Row<PDPage> tableRow = baseTable.createRow(12);
			for(String colName : row.keySet()) {
				Cell<PDPage> cell = tableRow.createCell(10, row.get(colName).toString());
			}
		}
	    
	    baseTable.draw();
	    
//	    PDStreamUtils.write(contentStream, report.getFooter(),
//				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
		contentStream.close();
		PDDocument pdfDoc2 = new PDDocument();
		for(int i = 0; i < pdfDoc.getNumberOfPages(); i++) {
			PDPage pdPage = pdfDoc.getPages().get(i);
			if(i != 0) {
				pdPage.setRotation(90);
			}
//			Matrix matrix = pdPage.getMatrix();
			pdPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
//			System.out.println("Height: "+PDRectangle.A4.getHeight()+" : "+(pdPage.getMediaBox().getHeight() - (2 * margin)));
//			System.out.println("Width: "+PDRectangle.A4.getWidth()+" : "+(pdPage.getMediaBox().getWidth() - (2 * margin)));
//			pdPage.setMediaBox(new PDRectangle(pdPage.getMediaBox().getHeight() - (2 * margin), pdPage.getMediaBox().getWidth() - 2.0f * margin));
			
			pdfDoc2.addPage(pdPage);
		}
		return pdfDoc2;
	}
	


	public PDDocument getPDFDocForReport(List<Map<String, Object>> resultList, ReportExec reportExec)
			throws IOException {
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(reportExec.getDependsOn().getRef().getUuid(),
				reportExec.getDependsOn().getRef().getVersion(),
				reportExec.getDependsOn().getRef().getType().toString());
		PDDocument pdfDoc = new PDDocument();
		PDPage pdfPage = new PDPage();
//		pdfPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
		pdfDoc.addPage(pdfPage);

		contentStream = new PDPageContentStream(pdfDoc, pdfPage);
		
		float y = 750.0f;
		float margin = 50.0f;

		//writting title
		final float titleWidth = pdfPage.getMediaBox().getWidth() - 2.0f * margin;
		float titleX = ((titleWidth - (report.getTitle().length()+margin))/2);
		if(titleX < 0) {
			titleX = margin;
		}
		PDStreamUtils.write(contentStream, report.getTitle(), (PDFont)PDType1Font.HELVETICA, 12.0f, titleX, y, Color.black);
		y -= 50.0f;
		
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10.0f);
		
		//writting other contents
		PDStreamUtils.write(contentStream, "Report Name".concat(": ").concat(report.getName()),
				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
		y -= 15.0f;
		
		PDStreamUtils.write(contentStream,
				"Report Description".concat(": ").concat(report.getDesc() != null ? report.getDesc() : ""),
				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
		y -= 15.0f;

		StringBuilder reportParameters = new StringBuilder("");
		if (report.getParamList() != null && reportExec.getExecParams() != null
				&& reportExec.getExecParams().getParamListInfo() != null
				&& !reportExec.getExecParams().getParamListInfo().isEmpty()) {

			for (ParamListHolder paramListHolder : reportExec.getExecParams().getParamListInfo()) {
				String paramName = paramListHolder.getParamName();
				String paramValue = paramListHolder.getParamValue().getValue();
				reportParameters.append(paramName).append(": ").append(paramValue).append(", ");
			}

			if (reportParameters.length() > 0) {
				reportParameters = new StringBuilder(
						reportParameters.substring(0, reportParameters.toString().lastIndexOf(",")));
			}

		}

		PDStreamUtils.write(contentStream, "Report Parameters: ".concat(reportParameters.toString()),
				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
		y -= 15.0f;
		
		PDStreamUtils.write(contentStream, "Report Generation Date: ".concat(reportExec.getCreatedOn()),
				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
		y -= 30.0f;
		
		//writting header
		final float headerWidth = pdfPage.getMediaBox().getWidth() - 2.0f * margin;
		float headerX = ((headerWidth - (report.getHeader().length()+margin))/2);
		if(headerX < 0) {
			headerX = margin;
		}
		PDStreamUtils.write(contentStream, report.getHeader(), (PDFont)PDType1Font.HELVETICA, 10.0f, headerX, y, Color.black);
		y -= 30.0f;

		//adding column names into list as row
		Map<String, Object> colMap = new LinkedHashMap<>();
		for(String colName : resultList.get(0).keySet()) {
			colMap.put(colName, colName);
		}
		
//		resultList.add(0, colMap);
		
		//writting data into table
		final float tableWidth = pdfPage.getMediaBox().getWidth() - 2.0f * margin;
		float yStartNewPage = pdfPage.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 0;
		BaseTable baseTable = new BaseTable(y, yStartNewPage, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
//		
//	    for(final Map<String, Object> row : resultList) {
//	    	Row<PDPage> tableRow = table.createRow(12);
//			for(String colName : row.keySet()) {
//				Cell<PDPage> cell = tableRow.createCell(25, row.get(colName).toString());
//			}
//		}

//	    BaseTable baseTable = new BaseTable(y, yStartNewPage, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
	    
//	    BaseTable baseTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, pdfDoc, pdfPage, true,
//                true);
//	    DataTable t = new DataTable(baseTable, pdfPage);
//	    
//	    List<List> data = new ArrayList();
//	    for(final Map<String, Object> row : resultList) {
//	    	List<String> row2 = new ArrayList();
//	    	for(String colName : row.keySet()) {
//	    		row2.add(row.get(colName).toString());
//	    	}
//	    	data.add(row2);
//	    }
//	    t.addListToTable(data, DataTable.HASHEADER);
	    
	    
	    Row<PDPage> headerRow = baseTable.createRow(15f);
	    for(String colName : colMap.keySet()) {
		    Cell<PDPage> cell = headerRow.createCell(25, colMap.get(colName).toString());
		    cell.setFont(PDType1Font.HELVETICA_BOLD);
		    cell.setFillColor(Color.WHITE);
	    }
	    baseTable.addHeaderRow(headerRow);
	    
	    for(final Map<String, Object> row : resultList) {
	    	Row<PDPage> tableRow = baseTable.createRow(12);
			for(String colName : row.keySet()) {
				Cell<PDPage> cell = tableRow.createCell(25, row.get(colName).toString());
			}
		}
	    
	    baseTable.draw();
	    
//	    PDStreamUtils.write(contentStream, report.getFooter(),
//				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
		contentStream.close();
		return pdfDoc;
	}
	
	public PDDocument getPDFDoc(List<Map<String, Object>> resultList)
			throws IOException {
		PDDocument pdfDoc = new PDDocument();
		PDPage pdfPage = new PDPage();
		pdfDoc.addPage(pdfPage);

		contentStream = new PDPageContentStream(pdfDoc, pdfPage);
		
		float y = 750.0f;
		float margin = 50.0f;

		//adding column names into list as row
		Map<String, Object> colMap = new LinkedHashMap<>();
		for(String colName : resultList.get(0).keySet()) {
			colMap.put(colName, colName);
		}
		
		resultList.add(0, colMap);
		
		//writting data into table
		final float tableWidth = pdfPage.getMediaBox().getWidth() - 2.0f * margin;
		float tableHeight = pdfPage.getMediaBox().getHeight() - (2 * margin);
		BaseTable table = new BaseTable(y, tableHeight, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
		
	    for(final Map<String, Object> row : resultList) {
	    	Row<PDPage> tableRow = table.createRow(12);
			for(String colName : row.keySet()) {
				Cell<PDPage> cell = tableRow.createCell(25, row.get(colName).toString());
			}
		}
	    
//	    List<List> tempList = new ArrayList<>();
//	    for(final Map<String, Object> row : resultList) {
////	    	Row<PDPage> tableRow = table.createRow(12);
////			for(String colName : row.keySet()) {
////				Cell<PDPage> cell = tableRow.createCell(25, row.get(colName).toString());
////			}
//			List<Object> rowList = new ArrayList<>(row.values());
//			tempList.add(rowList);
//		}
//	    DataTable t = new DataTable(table, pdfPage);
//		t.addListToTable(tempList, DataTable.HASHEADER);

	    table.draw();
		contentStream.close();
		return pdfDoc;
	}
}
