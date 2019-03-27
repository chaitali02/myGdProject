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

import com.inferyx.framework.domain.Document;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.service.CommonServiceImpl;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * @author Ganesh
 *
 */
@Component
public class PDFUtil {

	static final Logger logger = Logger.getLogger(PDFUtil.class);

	private PDPageContentStream contentStream;

	public PDDocument createPDF(Document document) throws IOException {

		if (document.getLayout() == null) {
			throw new RuntimeException("Layout not defined ...");
		}

		float y = 550.0f;
		if (document.getLayout().equals(Layout.LANDSCAPE)) {
			y = 400.0f;
		}
		float margin = 50.0f;

		PDDocument pdfDoc = new PDDocument();
		PDPage pdfPage = new PDPage();
		pdfPage.setMediaBox(new PDRectangle(PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight()));

		if (document.getLayout().equals(Layout.LANDSCAPE)) {
			pdfPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
		}

		pdfDoc.addPage(pdfPage);

		PDFont font = PDType1Font.HELVETICA_BOLD;
		float fontSize = 10.0f;

		contentStream = new PDPageContentStream(pdfDoc, pdfPage);
		contentStream.setFont(font, fontSize);

		final String title = !StringUtils.isBlank(document.getTitle()) ? document.getTitle() : "";
		float titleWidth = font.getStringWidth(title) / 1000 * 20.0f;
		float titleX = (pdfPage.getMediaBox().getWidth() - titleWidth) / 2;

		PDStreamUtils.write(contentStream, title, PDType1Font.HELVETICA_BOLD, 20.0f, titleX, y, Color.black);
		y -= 50.0f;

		final float tableWidth2 = pdfPage.getMediaBox().getHeight() - 3.00f * margin;
		BaseTable baseTable2 = new BaseTable(y, tableWidth2, 50f, tableWidth2, margin, pdfDoc, pdfPage, true, true);

		Map<String, String> otherDetails = new LinkedHashMap<>();
		otherDetails.put("Name", document.getName());
		otherDetails.put("Description", document.getDescription() != null ? document.getDescription() : "");
		otherDetails.put("Parameters", document.getParameters());
		otherDetails.put("Generation Date", document.getGenerationDate());

		float cell1Width = 20;
		float cell2Width = 35;

		if (document.getLayout().equals(Layout.LANDSCAPE)) {
			cell1Width = 30;
			cell2Width = 50;
		}

		for (String key : otherDetails.keySet()) {
			Row<PDPage> tableRow = baseTable2.createRow(18);
			Cell<PDPage> cell1 = tableRow.createCell(cell1Width, key);
			Cell<PDPage> cell2 = tableRow.createCell(cell2Width, otherDetails.get(key));
		}

		baseTable2.draw();

		List<Map<String, Object>> data = document.getData();

		// adding column names into list as row
		Map<String, Object> colMap = new LinkedHashMap<>();
		for (String colName : data.get(0).keySet()) {
			colMap.put(colName, colName);
		}

		// writting data into table
		float cellSize = 12.0f;
		int numCellsPerPage = 6;
		if (document.getLayout().equals(Layout.LANDSCAPE)) {
			cellSize = 16.5f;
			numCellsPerPage = 10;
		}
		final float tableWidth = pdfPage.getMediaBox().getHeight() - 3.00f * margin;
		List<List<String>> partitionedLeyList = new ArrayList<>(
				partitionBasedOnSize(new ArrayList<String>(data.get(0).keySet()), numCellsPerPage));
		for (List<String> keySubList : partitionedLeyList) {
			pdfPage = new PDPage();
			if (document.getLayout().equals(Layout.LANDSCAPE)) {
				pdfPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			}
			pdfDoc.addPage(pdfPage);

			BaseTable baseTable = null;
			if (document.getLayout().equals(Layout.LANDSCAPE)) {
				baseTable = new BaseTable(545.0f, 545.0f, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
			} else if (document.getLayout().equals(Layout.PORTRAIT)) {
				float yStartNewPage = pdfPage.getMediaBox().getHeight();
				float yStart = yStartNewPage;
				baseTable = new BaseTable(yStart, yStartNewPage, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
			}

			Row<PDPage> headerRow = baseTable.createRow(15f);
			for (String colName : keySubList) {
				Cell<PDPage> cell = headerRow.createCell(cellSize, colMap.get(colName).toString());
				cell.setFont(PDType1Font.HELVETICA_BOLD);
				cell.setFillColor(Color.WHITE);
			}
			baseTable.addHeaderRow(headerRow);

			for (final Map<String, Object> row : data) {
				Row<PDPage> tableRow = baseTable.createRow(12);
				for (String colName : keySubList) {
					Cell<PDPage> cell = tableRow.createCell(cellSize, row.get(colName).toString());
				}
			}

			baseTable.draw();
		}

		contentStream.close();

		PDDocument pdfDoc2 = new PDDocument();
		for (int i = 0; i < pdfDoc.getNumberOfPages(); i++) {
			PDPage pdPage = pdfDoc.getPages().get(i);
			pdPage.setMediaBox(new PDRectangle(PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight()));

			String header = !StringUtils.isBlank(document.getHeader()) ? document.getHeader() : "";
			float headerWidth = font.getStringWidth(header) / 1000 * fontSize;
			float headerX = (pdPage.getMediaBox().getWidth() - headerWidth) / 2;
			float headerYStart = pdfPage.getMediaBox().getHeight() + 32;

			final String footer = !StringUtils.isBlank(document.getFooter()) ? document.getFooter() : "";
			float footerWidth = font.getStringWidth(footer) / 1000 * fontSize;
			float footerX = (pdPage.getMediaBox().getWidth() - footerWidth) / 2;
			float footerYStart = (pdfPage.getMediaBox().getHeight() - (pdfPage.getMediaBox().getHeight() - margin))
					- 20;

			if (document.getLayout().equals(Layout.LANDSCAPE)) {
				pdPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));

				headerYStart = pdfPage.getMediaBox().getHeight() - 22;
				headerX = (pdPage.getMediaBox().getWidth() - headerWidth) / 2;

				footerYStart = (pdfPage.getMediaBox().getHeight() - (pdfPage.getMediaBox().getHeight() - margin)) - 20;
				footerX = (pdPage.getMediaBox().getWidth() - footerWidth) / 2;
			}

			PDPageContentStream contentStream = new PDPageContentStream(pdfDoc, pdPage, AppendMode.APPEND, true);

			PDStreamUtils.write(contentStream, footer, PDType1Font.HELVETICA, 10, footerX, footerYStart,
					new Color(102, 102, 102));// set stayle and size
			if (i != 0) {
				PDStreamUtils.write(contentStream, header, PDType1Font.HELVETICA, 10, headerX, headerYStart,
						new Color(102, 102, 102));// set stayle and size
			}

			contentStream.close();

			pdfDoc2.addPage(pdPage);
		}
		return pdfDoc2;
	}

	public static Collection<List<String>> partitionBasedOnSize(List<String> inputList, int size) {
		final AtomicInteger counter = new AtomicInteger(0);
		return inputList.stream().collect(Collectors.groupingBy(s -> counter.getAndIncrement() / size)).values();
	}

//	public PDDocument getPDFDocForReport(List<Map<String, Object>> resultList, ReportExec reportExec)
//			throws IOException {
//		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(reportExec.getDependsOn().getRef().getUuid(),
//				reportExec.getDependsOn().getRef().getVersion(),
//				reportExec.getDependsOn().getRef().getType().toString());
//		PDDocument pdfDoc = new PDDocument();
//		PDPage pdfPage = new PDPage();
////		pdfPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
//		pdfDoc.addPage(pdfPage);
//
//		contentStream = new PDPageContentStream(pdfDoc, pdfPage);
//		
//		float y = 750.0f;
//		float margin = 50.0f;
//
//		//writting title
//		final float titleWidth = pdfPage.getMediaBox().getWidth() - 2.0f * margin;
//		float titleX = ((titleWidth - (report.getTitle().length()+margin))/2);
//		if(titleX < 0) {
//			titleX = margin;
//		}
//		PDStreamUtils.write(contentStream, report.getTitle(), (PDFont)PDType1Font.HELVETICA, 12.0f, titleX, y, Color.black);
//		y -= 50.0f;
//		
//		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10.0f);
//		
//		//writting other contents
//		PDStreamUtils.write(contentStream, "Report Name".concat(": ").concat(report.getName()),
//				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
//		y -= 15.0f;
//		
//		PDStreamUtils.write(contentStream,
//				"Report Description".concat(": ").concat(report.getDesc() != null ? report.getDesc() : ""),
//				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
//		y -= 15.0f;
//
//		StringBuilder reportParameters = new StringBuilder("");
//		if (report.getParamList() != null && reportExec.getExecParams() != null
//				&& reportExec.getExecParams().getParamListInfo() != null
//				&& !reportExec.getExecParams().getParamListInfo().isEmpty()) {
//
//			for (ParamListHolder paramListHolder : reportExec.getExecParams().getParamListInfo()) {
//				String paramName = paramListHolder.getParamName();
//				String paramValue = paramListHolder.getParamValue().getValue();
//				reportParameters.append(paramName).append(": ").append(paramValue).append(", ");
//			}
//
//			if (reportParameters.length() > 0) {
//				reportParameters = new StringBuilder(
//						reportParameters.substring(0, reportParameters.toString().lastIndexOf(",")));
//			}
//
//		}
//
//		PDStreamUtils.write(contentStream, "Report Parameters: ".concat(reportParameters.toString()),
//				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
//		y -= 15.0f;
//		
//		PDStreamUtils.write(contentStream, "Report Generation Date: ".concat(reportExec.getCreatedOn()),
//				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
//		y -= 30.0f;
//		
//		//writting header
//		final float headerWidth = pdfPage.getMediaBox().getWidth() - 2.0f * margin;
//		float headerX = ((headerWidth - (report.getHeader().length()+margin))/2);
//		if(headerX < 0) {
//			headerX = margin;
//		}
//		PDStreamUtils.write(contentStream, report.getHeader(), (PDFont)PDType1Font.HELVETICA, 10.0f, headerX, y, Color.black);
//		y -= 30.0f;
//
//		//adding column names into list as row
//		Map<String, Object> colMap = new LinkedHashMap<>();
//		for(String colName : resultList.get(0).keySet()) {
//			colMap.put(colName, colName);
//		}
//		
////		resultList.add(0, colMap);
//		
//		//writting data into table
//		final float tableWidth = pdfPage.getMediaBox().getWidth() - 2.0f * margin;
//		float yStartNewPage = pdfPage.getMediaBox().getHeight() - (2 * margin);
//        float yStart = yStartNewPage;
//        float bottomMargin = 0;
//		BaseTable baseTable = new BaseTable(y, yStartNewPage, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
////		
////	    for(final Map<String, Object> row : resultList) {
////	    	Row<PDPage> tableRow = table.createRow(12);
////			for(String colName : row.keySet()) {
////				Cell<PDPage> cell = tableRow.createCell(25, row.get(colName).toString());
////			}
////		}
//
////	    BaseTable baseTable = new BaseTable(y, yStartNewPage, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
//	    
////	    BaseTable baseTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, pdfDoc, pdfPage, true,
////                true);
////	    DataTable t = new DataTable(baseTable, pdfPage);
////	    
////	    List<List> data = new ArrayList();
////	    for(final Map<String, Object> row : resultList) {
////	    	List<String> row2 = new ArrayList();
////	    	for(String colName : row.keySet()) {
////	    		row2.add(row.get(colName).toString());
////	    	}
////	    	data.add(row2);
////	    }
////	    t.addListToTable(data, DataTable.HASHEADER);
//	    
//	    
//	    Row<PDPage> headerRow = baseTable.createRow(15f);
//	    for(String colName : colMap.keySet()) {
//		    Cell<PDPage> cell = headerRow.createCell(25, colMap.get(colName).toString());
//		    cell.setFont(PDType1Font.HELVETICA_BOLD);
//		    cell.setFillColor(Color.WHITE);
//	    }
//	    baseTable.addHeaderRow(headerRow);
//	    
//	    for(final Map<String, Object> row : resultList) {
//	    	Row<PDPage> tableRow = baseTable.createRow(12);
//			for(String colName : row.keySet()) {
//				Cell<PDPage> cell = tableRow.createCell(25, row.get(colName).toString());
//			}
//		}
//	    
//	    baseTable.draw();
//	    
////	    PDStreamUtils.write(contentStream, report.getFooter(),
////				(PDFont) PDType1Font.HELVETICA, 10.0f, margin, y, Color.black);
//		contentStream.close();
//		return pdfDoc;
//	}
//	
//	public PDDocument getPDFDoc(List<Map<String, Object>> resultList)
//			throws IOException {
//		PDDocument pdfDoc = new PDDocument();
//		PDPage pdfPage = new PDPage();
//		pdfDoc.addPage(pdfPage);
//
//		contentStream = new PDPageContentStream(pdfDoc, pdfPage);
//		
//		float y = 750.0f;
//		float margin = 50.0f;
//
//		//adding column names into list as row
//		Map<String, Object> colMap = new LinkedHashMap<>();
//		for(String colName : resultList.get(0).keySet()) {
//			colMap.put(colName, colName);
//		}
//		
//		resultList.add(0, colMap);
//		
//		//writting data into table
//		final float tableWidth = pdfPage.getMediaBox().getWidth() - 2.0f * margin;
//		float tableHeight = pdfPage.getMediaBox().getHeight() - (2 * margin);
//		BaseTable table = new BaseTable(y, tableHeight, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
//		
//	    for(final Map<String, Object> row : resultList) {
//	    	Row<PDPage> tableRow = table.createRow(12);
//			for(String colName : row.keySet()) {
//				Cell<PDPage> cell = tableRow.createCell(25, row.get(colName).toString());
//			}
//		}
//	    
////	    List<List> tempList = new ArrayList<>();
////	    for(final Map<String, Object> row : resultList) {
//////	    	Row<PDPage> tableRow = table.createRow(12);
//////			for(String colName : row.keySet()) {
//////				Cell<PDPage> cell = tableRow.createCell(25, row.get(colName).toString());
//////			}
////			List<Object> rowList = new ArrayList<>(row.values());
////			tempList.add(rowList);
////		}
////	    DataTable t = new DataTable(table, pdfPage);
////		t.addListToTable(tempList, DataTable.HASHEADER);
//
//	    table.draw();
//		contentStream.close();
//		return pdfDoc;
//	}
}
