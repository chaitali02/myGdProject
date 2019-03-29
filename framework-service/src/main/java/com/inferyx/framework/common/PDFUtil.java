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

import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Document;
import com.inferyx.framework.enums.Alignment;
import com.inferyx.framework.enums.Layout;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.line.LineStyle;
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
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

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
		
		//******************* Start of title page *******************//		
		PDPage pdfPage = new PDPage();
		pdfPage.setMediaBox(new PDRectangle(PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight()));

		if (document.getLayout().equals(Layout.LANDSCAPE)) {
			pdfPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
		}

		pdfDoc.addPage(pdfPage);

		PDFont boldFont = PDType1Font.HELVETICA_BOLD;
		PDFont nonBoldFont = PDType1Font.HELVETICA;		
		final float fontSize = 10.0f;

		contentStream = new PDPageContentStream(pdfDoc, pdfPage);
		contentStream.setFont(boldFont, fontSize);
		contentStream.setNonStrokingColor(new Color(36, 92, 148));
		
		//adding rectangle to title page and filling it with color
		final float boxPadding = 15.0f;
		final float rectXPos = margin + boxPadding;
		final float rextYPos = margin + boxPadding;
		final float rectWidth = pdfPage.getMediaBox().getWidth() - 2.0f*margin - (2*boxPadding);
		final float rectHeight = pdfPage.getMediaBox().getHeight() - 2.0f*margin - (2*boxPadding);
		contentStream.addRect(rectXPos, rextYPos, rectWidth, rectHeight);
		contentStream.fill();
		
		//drawing title
		final float titleSize = 30.0f;
		final String title = !StringUtils.isBlank(document.getTitle()) ? document.getTitle() : "";
		float titleWidth = boldFont.getStringWidth(title) / 1000 * titleSize;
		float titleX = getAlignmentPosition(document.getTitleAlignment().toString(), pdfPage.getMediaBox().getWidth(), titleWidth, margin);

		PDStreamUtils.write(contentStream, title, boldFont, titleSize, titleX, y, Color.WHITE);

		//drawing document details
		float tableWidth2 = pdfPage.getMediaBox().getWidth() - 2.00f * (margin+boxPadding+10);
		if (document.getLayout().equals(Layout.LANDSCAPE)) {
			pdfPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			tableWidth2 = pdfPage.getMediaBox().getWidth() - 2.00f * margin;
		}
		
		float margin2 = (pdfPage.getMediaBox().getWidth() - tableWidth2) / 2;
		if (document.getLayout().equals(Layout.LANDSCAPE)) {
			margin2 += 20;
		}
		 
		y -= 110.0f;
		BaseTable baseTable2 = new BaseTable(y, y, 50f, tableWidth2, margin2, pdfDoc, pdfPage, true, true);

		Map<String, String> otherDetails = new LinkedHashMap<>();
		otherDetails.put("Name", document.getName());
		otherDetails.put("Description", document.getDescription() != null ? document.getDescription() : "");
		otherDetails.put("Parameters", document.getParameters());
		otherDetails.put("Generation Date", document.getGenerationDate());

		float cell0Width = 14;
		float cell1Width = 32;
		float cell2Width = 40;

		if (document.getLayout().equals(Layout.LANDSCAPE)) {
			cell0Width = 20;
			cell1Width = 25;
			cell2Width = 28;
		}

		for (String key : otherDetails.keySet()) {
			Row<PDPage> tableRow = baseTable2.createRow(25);
			
			Cell<PDPage> cell0 = tableRow.createCell(cell0Width, " ");
			cell0.setTextColor(Color.WHITE);
			cell0.setFontSize(12);
			cell0.setRightBorderStyle(LineStyle.produceDotted(new Color(36, 92, 148), 0));
			cell0.setLeftBorderStyle(LineStyle.produceDotted(new Color(36, 92, 148), 0));
			cell0.setTopBorderStyle(LineStyle.produceDotted(new Color(36, 92, 148), 0));
			cell0.setBottomBorderStyle(LineStyle.produceDotted(new Color(36, 92, 148), 0));
			
			Cell<PDPage> cell1 = tableRow.createCell(cell1Width, "   "+key);
			cell1.setTextColor(Color.WHITE);
			cell1.setFontSize(12);
			cell1.setRightBorderStyle(LineStyle.produceDotted(new Color(36, 92, 148), 0));
			cell1.setLeftBorderStyle(LineStyle.produceDotted(new Color(36, 92, 148), 0));
			cell1.setTopBorderStyle(LineStyle.produceDotted(new Color(36, 92, 148), 0));
			cell1.setBottomBorderStyle(LineStyle.produceDotted(Color.white, 1));
			
			Cell<PDPage> cell2 = tableRow.createCell(cell2Width, otherDetails.get(key));
			cell2.setTextColor(Color.WHITE);
			cell2.setFontSize(12);
			cell2.setRightBorderStyle(LineStyle.produceDotted(new Color(36, 92, 148), 0));
			cell2.setLeftBorderStyle(LineStyle.produceDotted(new Color(36, 92, 148), 0));
			cell2.setTopBorderStyle(LineStyle.produceDotted(new Color(36, 92, 148), 0));
			cell2.setBottomBorderStyle(LineStyle.produceDotted(Color.white, 1));
		}

		baseTable2.draw();
		
		//******************* End of title page *******************//


		// adding column names into list as row
		List<Map<String, Object>> data = document.getData();
		Map<String, Object> colMap = new LinkedHashMap<>();
		for (String colName : data.get(0).keySet()) {
			colMap.put(colName, colName);
		}

		// writting data into table
		float cellSize = 16.70f;
		int numCellsPerPage = 6;
		if (document.getLayout().equals(Layout.LANDSCAPE)) {
			cellSize = 14.30f;
			numCellsPerPage = 7;
		}
		
		float tableWidth = pdfPage.getMediaBox().getWidth() - 2.00f * margin;
		if (document.getLayout().equals(Layout.LANDSCAPE)) {
			pdfPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			tableWidth = pdfPage.getMediaBox().getWidth() - 2.00f * margin;
		}
		
		List<List<String>> partitionedLeyList = new ArrayList<>(
				partitionBasedOnSize(new ArrayList<String>(data.get(0).keySet()), numCellsPerPage));
		for (List<String> keySubList : partitionedLeyList) {
			pdfPage = new PDPage();			
			pdfDoc.addPage(pdfPage);
			
			BaseTable baseTable = null;
			if (document.getLayout().equals(Layout.LANDSCAPE)) {
				float yStartNewPage = 545.0f - 30;
				float yStart = yStartNewPage;
				baseTable = new BaseTable(yStart, yStartNewPage, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
			} else if (document.getLayout().equals(Layout.PORTRAIT)) {
				float yStartNewPage = pdfPage.getMediaBox().getHeight() - 30;
				float yStart = yStartNewPage;
				baseTable = new BaseTable(yStart, yStartNewPage, 50f, tableWidth, margin, pdfDoc, pdfPage, true, true);
			}

			Row<PDPage> headerRow = baseTable.createRow(30f);
			for (String colName : keySubList) {
				Cell<PDPage> cell = headerRow.createCell(cellSize, colMap.get(colName).toString());
				cell.setFont(boldFont);
				cell.setTextColor(Color.WHITE);
				cell.setFontSize(10);
//				cell.setFillColor(Color.WHITE);
				cell.setAlign(HorizontalAlignment.CENTER);
				cell.setFillColor(new Color(36, 92, 148));
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
		
		float headerFontSize = 15;
		PDDocument pdfDoc2 = new PDDocument();
		for (int i = 0; i < pdfDoc.getNumberOfPages(); i++) {
			PDPage pdPage = pdfDoc.getPages().get(i);
			pdPage.setMediaBox(new PDRectangle(PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight()));

			float pageWidth = pdPage.getMediaBox().getWidth();
			if (document.getLayout().equals(Layout.LANDSCAPE)) {
				pdPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
				pageWidth = pdPage.getMediaBox().getWidth();
			}
			
			String header = !StringUtils.isBlank(document.getHeader()) ? document.getHeader() : "";
			float headerWidth = boldFont.getStringWidth(header) / 1000 * headerFontSize;
			float headerX = getAlignmentPosition(document.getHeaderAlignment(), pageWidth, headerWidth, margin);
			float headerYStart = pdPage.getMediaBox().getHeight() - margin;

			final String footer = !StringUtils.isBlank(document.getFooter()) ? document.getFooter() : "";
			float footerWidth = boldFont.getStringWidth(footer) / 1000 * fontSize;
			float footerX = getAlignmentPosition(document.getFooterAlignment(), pageWidth, footerWidth, margin);
			float footerYStart = (pdPage.getMediaBox().getHeight() - (pdPage.getMediaBox().getHeight() - margin))
					- 20;
			
			final String pageNum = ""+(i+1);
			float pageNumWidth = boldFont.getStringWidth(pageNum) / 1000 * fontSize;
			String pageNumAlignment = Alignment.RIGHT.toString();
			if(document.getFooterAlignment().equalsIgnoreCase(Alignment.RIGHT.toString())) {
				pageNumAlignment = Alignment.CENTER.toString();
			}
			float pageNumberX = getAlignmentPosition(pageNumAlignment, pageWidth, pageNumWidth, margin);
			float pageNumYStart = footerYStart;
			
			if (document.getLayout().equals(Layout.LANDSCAPE)) {
//				pdPage.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));

				headerYStart = pdPage.getMediaBox().getHeight() - margin;
				headerX = getAlignmentPosition(document.getHeaderAlignment(), pageWidth, headerWidth, margin);

				footerYStart = (pdPage.getMediaBox().getHeight() - (pdPage.getMediaBox().getHeight() - margin)) - 20;
				footerX = getAlignmentPosition(document.getFooterAlignment(), pageWidth, footerWidth, margin);
				

				pageNumberX = getAlignmentPosition(pageNumAlignment, pageWidth, pageNumWidth, margin);
				pageNumYStart = footerYStart;
			}

			PDPageContentStream contentStream = new PDPageContentStream(pdfDoc, pdPage, AppendMode.APPEND, true);
			
			//drawing organization logo
			if(!StringUtils.isBlank(document.getOrgLogoName())) {
				final float imgWidth = 35.0f;
				final float imgHeight = 35.0f;
				final float imgXPos = getAlignmentPosition(document.getLogoAlignment().toString(), pageWidth, imgWidth, 15);
				final float imgYPos = pdPage.getMediaBox().getHeight() - 15.0f - imgHeight;
				PDImageXObject pdImage = PDImageXObject.createFromFile(document.getOrgLogoName(), pdfDoc);
				contentStream.drawImage(pdImage, imgXPos, imgYPos, imgWidth, imgHeight);
			}
			
			//drawing header
			if (i != 0) {
				PDStreamUtils.write(contentStream, header, boldFont, headerFontSize, headerX, headerYStart,
						Color.BLACK);// set stayle and size
			}

			//drawing footer
			PDStreamUtils.write(contentStream, footer, nonBoldFont, 10, footerX, footerYStart,
					new Color(102, 102, 102));// set stayle and size
			
			//drawing page number
			PDStreamUtils.write(contentStream, pageNum, nonBoldFont, 10, pageNumberX, pageNumYStart,
					new Color(102, 102, 102));// set stayle and size
			
			contentStream.close();

			pdfDoc2.addPage(pdPage);
		}
		return pdfDoc2;
	}

	public float getAlignmentPosition(String alignMent, float pageWidth, float contentWidth, float margin) {
		switch (alignMent.toLowerCase()) {
		case "left":
			return margin;

		case "right":
			return pageWidth - contentWidth - margin;

		case "center":
			return (pageWidth - contentWidth) / 2;

		default:
			return (pageWidth - contentWidth) / 2;
		}
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
