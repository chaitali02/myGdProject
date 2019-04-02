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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.service.CommonServiceImpl;

@Component
public class WorkbookUtil {
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl; 

	public static HSSFWorkbook getWorkbook(Map<String, LinkedHashMap<String, String>> resultMap) {
		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFCellStyle headerStyle = wb.createCellStyle();
		HSSFSheet sheet3 = wb.createSheet("main");

		HSSFFont headerFont = wb.createFont();
		headerFont.setBold(true);

		headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headerStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		headerStyle.setFillBackgroundColor(HSSFColor.RED.index);
		headerStyle.setFont(headerFont);

		HSSFRow sessionname = sheet3.createRow(2);
		HSSFCell title = sessionname.createCell(3);
		title.setCellStyle(headerStyle);
		title.setCellValue("main");

		HSSFRow row = sheet3.createRow(5);

		Map<String, LinkedHashMap<String, String>> rMap = resultMap;
		Map<String, String> columnDetails = rMap.get("1");

		Set<String> s = columnDetails.keySet();
		int cellNo = 0;
		for (String s1 : s) {
			HSSFCell cell0 = row.createCell(cellNo);
			cell0.setCellStyle(headerStyle);
			cell0.setCellValue(s1);
			cellNo++;
		}

		for (int i = 1; i <= rMap.size(); i++) {
			columnDetails = rMap.get(new Integer(i).toString());
			HSSFRow nextrow = sheet3.createRow(5 + i);
			Set<String> set = columnDetails.keySet();
			int cellNum = 0;
			for (String s2 : set) {
				nextrow.createCell(cellNum).setCellValue(columnDetails.get(s2));
				cellNum++;
			}
		}
//		headerFont = wb.createFont();
//		headerFont.setBold(true);
//
//		headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//		headerStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
//		headerStyle.setFillBackgroundColor(HSSFColor.RED.index);
//		headerStyle.setFont(headerFont);
//		sheet3.autoSizeColumn(0);
//		sheet3.autoSizeColumn(1);
//		sheet3.autoSizeColumn(2);
//		sheet3.autoSizeColumn(3);
		return wb;
	}

	public static HSSFWorkbook getWorkbook(List<Map<String, Object>> resultList) {
			HSSFWorkbook workBook = new HSSFWorkbook();

			HSSFCellStyle headerStyle = workBook.createCellStyle();
			HSSFSheet hssfSheet = workBook.createSheet("Data");

			HSSFFont headerFont = workBook.createFont();
			headerFont.setBold(true);

			headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headerStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
			headerStyle.setFillBackgroundColor(HSSFColor.RED.index);
			headerStyle.setFont(headerFont);

			/*HSSFRow sessionname = hssfSheet.createRow(2);
			HSSFCell title = sessionname.createCell(3);
			title.setCellStyle(headerStyle);
			title.setCellValue("Sample Data");*/

			HSSFRow row = hssfSheet.createRow(0);
			
			Set<String> columnNames = resultList.get(0).keySet();
			int cellNo = 0;
			for (String column : columnNames) {
				HSSFCell cell0 = row.createCell(cellNo);
				cell0.setCellStyle(headerStyle);
				cell0.setCellValue(column);
				cellNo++;
			}
			
			for (int i = 0; i < resultList.size(); i++) {
				HSSFRow nextrow = hssfSheet.createRow(1 + i);
				columnNames = resultList.get(i).keySet();
				int cellNum = 0;
				for (String column : columnNames) {
					String value = "";
					try {
						value = resultList.get(i).get(column).toString();
					}catch (Exception e) {
						value = "null";
					}
					nextrow.createCell(cellNum).setCellValue(value);
					cellNum++;
				}
			}
//			headerFont = workBook.createFont();
//			headerFont.setBold(true);
//
//			headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//			headerStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
//			headerStyle.setFillBackgroundColor(HSSFColor.RED.index);
//			headerStyle.setFont(headerFont);
//			hssfSheet.autoSizeColumn(0);
//			hssfSheet.autoSizeColumn(1);
//			hssfSheet.autoSizeColumn(2);
//			hssfSheet.autoSizeColumn(3);
			return workBook;
	}
	
	@SuppressWarnings("deprecation")
	public Workbook getWorkbookForReport(List<Map<String, Object>> resultList, ReportExec reportExec) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(reportExec.getDependsOn().getRef().getUuid(), reportExec.getDependsOn().getRef().getVersion(), reportExec.getDependsOn().getRef().getType().toString());
		Workbook workBook = new HSSFWorkbook();
		HSSFPalette palette = ((HSSFWorkbook)workBook).getCustomPalette();
		short colorIndex = 45;		
		palette.setColorAtIndex(colorIndex, (byte)63, (byte)137, (byte)201); 		
		Sheet hssfSheet = workBook.createSheet("Main");
		if (report.getAttributeInfo().size() > 1) {
			hssfSheet.addMergedRegion(new CellRangeAddress(1, 3, 0, report.getAttributeInfo().size() - 1));
		} else {
			hssfSheet.addMergedRegion(new CellRangeAddress(1, 3, 0, 1));
		}

	/******* adding title *******/
		Font titleHeaderFont = workBook.createFont();
		titleHeaderFont.setBold(false);
		titleHeaderFont.setColor(HSSFColor.WHITE.index);
	//	titleHeaderFont.setFontHeight((short)8);
		titleHeaderFont.setFontHeightInPoints((short)20);
		CellStyle titleHeaderStyle = workBook.createCellStyle();
		titleHeaderStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		titleHeaderStyle.setFillForegroundColor(getCustomColor(workBook, (byte)63, (byte)137, (byte)201));  
		titleHeaderStyle.setFillBackgroundColor(HSSFColor.RED.index);
		titleHeaderStyle.setFont(titleHeaderFont);
		titleHeaderStyle.setAlignment(CellStyle.ALIGN_CENTER);
		
		titleHeaderStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		Row title = hssfSheet.createRow((short)1);
		Cell titleCell = title.createCell((short)0);
		titleCell.setCellStyle(titleHeaderStyle);
		titleCell.setCellValue(new HSSFRichTextString(report.getTitle()));
		
	/******* adding Report name *******/
		short cellRowNum = 6; 
		Font reportNameFont = workBook.createFont();
		reportNameFont.setBold(false);
		reportNameFont.setColor(HSSFColor.WHITE.index);
		CellStyle reportNameCellStyle = workBook.createCellStyle();
		reportNameCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		reportNameCellStyle.setFillForegroundColor(getCustomColor(workBook, (byte)63, (byte)137, (byte)201));  
		reportNameCellStyle.setFillBackgroundColor(HSSFColor.RED.index);
		reportNameCellStyle.setFont(reportNameFont);
		reportNameCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
//		reportNameCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//		reportNameCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
//		reportNameCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
//		reportNameCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		
		Row reportNameRow = hssfSheet.createRow(cellRowNum);
		Cell reportNameCellKey = reportNameRow.createCell(0);
		reportNameCellKey.setCellStyle(reportNameCellStyle);
		reportNameCellKey.setCellValue(new HSSFRichTextString("Report Name"));
		Cell reportNameCellVal = reportNameRow.createCell(1);
		reportNameCellVal.setCellValue(new HSSFRichTextString(report.getName()));
		
		cellRowNum++;
		
	/******* adding Report description *******/	
		Font reportDescFont = workBook.createFont();
		reportDescFont.setBold(false);
		reportDescFont.setColor(HSSFColor.WHITE.index);
		CellStyle reportDescCellStyle = workBook.createCellStyle();
		reportDescCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		reportDescCellStyle.setFillForegroundColor(getCustomColor(workBook, (byte)63, (byte)137, (byte)201));  
		reportDescCellStyle.setFillBackgroundColor(HSSFColor.RED.index);
		reportDescCellStyle.setFont(reportDescFont);
		reportDescCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
//		reportDescCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//		reportDescCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
//		reportDescCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
//		reportDescCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		
		Row reportDescRow = hssfSheet.createRow(cellRowNum);
		Cell reportDescCellKey = reportDescRow.createCell(0);
		reportDescCellKey.setCellStyle(reportDescCellStyle);
		reportDescCellKey.setCellValue(new HSSFRichTextString("Report Description"));
		Cell reportDescCellVal = reportDescRow.createCell(1);
		reportDescCellVal.setCellValue(new HSSFRichTextString(report.getDesc()));
		
		cellRowNum++;	
		
	 /******* adding params *******/	

		if (report.getAttributeInfo().size() > 1) {
			hssfSheet.addMergedRegion(new CellRangeAddress(cellRowNum, cellRowNum, 1, report.getAttributeInfo().size() - 1));
		} else { 
			hssfSheet.addMergedRegion(new CellRangeAddress(cellRowNum, cellRowNum, 1, 1));
		}
		Font paramsHeaderFont = workBook.createFont();
		paramsHeaderFont.setBold(false);
		paramsHeaderFont.setColor(HSSFColor.WHITE.index);
		CellStyle paramsHeaderStyle = workBook.createCellStyle();
		paramsHeaderStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		paramsHeaderStyle.setFillForegroundColor(getCustomColor(workBook, (byte)63, (byte)137, (byte)201));  
		paramsHeaderStyle.setFillBackgroundColor(HSSFColor.RED.index);
		paramsHeaderStyle.setFont(paramsHeaderFont);

		StringBuilder reportParameters = new StringBuilder("");
		if (report.getParamList() != null && reportExec.getExecParams() != null
				&& reportExec.getExecParams().getParamListInfo() != null
				&& !reportExec.getExecParams().getParamListInfo().isEmpty()) {
					
					for(ParamListHolder paramListHolder : reportExec.getExecParams().getParamListInfo()) {
						String paramName = paramListHolder.getParamName();
						String paramValue =null;
						if(paramListHolder.getParamValue() !=null) {
							paramValue=paramListHolder.getParamValue().getValue();
						}
						reportParameters.append(paramName).append(": ").append(paramValue).append(", ");
					}
			
			if(reportParameters.length() > 0) {
				reportParameters = new StringBuilder(reportParameters.substring(0, reportParameters.toString().lastIndexOf(",")));
			}
			
		}		

		Row filter = hssfSheet.createRow(cellRowNum);
		Cell lhsAttrCell = filter.createCell(0);
		lhsAttrCell.setCellStyle(paramsHeaderStyle);
		lhsAttrCell.setCellValue("Report Parameters");
		Cell rhsAttrCell = filter.createCell(1);
		rhsAttrCell.setCellValue(reportParameters.toString());
		
		cellRowNum++;
		
	/******* adding Report generation date *******/
		Font reportGenDateFont = workBook.createFont();
		reportGenDateFont.setBold(false);
		reportGenDateFont.setColor(HSSFColor.WHITE.index);
		CellStyle reportGenDateCellStyle = workBook.createCellStyle();
		reportGenDateCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		reportGenDateCellStyle.setFillForegroundColor(getCustomColor(workBook, (byte)63, (byte)137, (byte)201));  
//		reportGenDateCellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
		reportGenDateCellStyle.setFillBackgroundColor(HSSFColor.RED.index);
		reportGenDateCellStyle.setFont(reportGenDateFont);
		reportGenDateCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
//		reportGenDateCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//		reportGenDateCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
//		reportGenDateCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
//		reportGenDateCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		
		Row reportGenDateRow = hssfSheet.createRow(cellRowNum);
		Cell reportGenDateCellKey = reportGenDateRow.createCell(0);
		reportGenDateCellKey.setCellStyle(reportGenDateCellStyle);
		reportGenDateCellKey.setCellValue(new HSSFRichTextString("Report Generation Date"));
		Cell reportGenDateCellVal = reportGenDateRow.createCell(1);
		reportGenDateCellVal.setCellValue(new HSSFRichTextString(reportExec.getCreatedOn()));
		
		//cellRowNum++;
		
	/******* adding header *******/
		short headerRowNum = cellRowNum;
		if(cellRowNum != (short)3) {
			headerRowNum = (short) (cellRowNum + 2);
		}
		if (report.getAttributeInfo().size() > 1) {
			hssfSheet.addMergedRegion(new CellRangeAddress(headerRowNum, headerRowNum, 0, report.getAttributeInfo().size() - 1));
		} else {
			hssfSheet.addMergedRegion(new CellRangeAddress(headerRowNum, headerRowNum, 0, 1));
		}
		Font headerFont = workBook.createFont();
		headerFont.setBold(false);
		headerFont.setColor(HSSFColor.WHITE.index);
		CellStyle headerCellStyle = workBook.createCellStyle();
		headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headerCellStyle.setFillForegroundColor(HSSFColor.DARK_BLUE.index);
		headerCellStyle.setFillBackgroundColor(HSSFColor.RED.index);
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(Helper.getStylesheetAlignment(report.getHeaderAlign()));
		headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		headerCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		headerCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		headerCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		
		Row headerRow = hssfSheet.createRow(headerRowNum);
		Cell headerCell = headerRow.createCell(0);
		headerCell.setCellStyle(headerCellStyle);
		headerCell.setCellValue(new HSSFRichTextString(report.getHeader()));
		
	/******* adding columns *******/
		Font columnHeaderFont = workBook.createFont();
		columnHeaderFont.setBold(false);
		columnHeaderFont.setColor(HSSFColor.WHITE.index);
		CellStyle columnHeaderStyle = workBook.createCellStyle();
		columnHeaderStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		columnHeaderStyle.setFillForegroundColor(getCustomColor(workBook, (byte)63, (byte)137, (byte)201));  
		
//		columnHeaderStyle.setFillForegroundColor(HSSFColor.ROYAL_BLUE.index);
		columnHeaderStyle.setFillBackgroundColor(HSSFColor.RED.index);
		columnHeaderStyle.setFont(columnHeaderFont);
		columnHeaderStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		columnHeaderStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		columnHeaderStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		columnHeaderStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		short columnRowNum = headerRowNum;
		if(headerRowNum != (short)3) {
			columnRowNum = (short) (headerRowNum + 2);
		}
		Row columns = hssfSheet.createRow(columnRowNum);		
		String[] columnNames = resultList.get(0).keySet().toArray(new String[resultList.get(0).keySet().size()]);
		for (int i = 0; i<columnNames.length; i++) {
			Cell cell = columns.createCell(i);
			cell.setCellStyle(columnHeaderStyle);
			cell.setCellValue(columnNames[i]+"                     ");
			hssfSheet.autoSizeColumn(i);
		}
	
	/******* adding rows *******/
//		Font columnFont = workBook.createFont();
		CellStyle columnStyle = workBook.createCellStyle();
		columnStyle.setAlignment(CellStyle.ALIGN_CENTER);
//		columnStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//		columnStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
//		columnStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
//		columnStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		int lastRowNum = columnRowNum+1;
		if(resultList.get(0).get(new ArrayList<>(resultList.get(0).keySet()).get(0)).equals("no data available.")) {
			if (report.getAttributeInfo().size() > 1) {
				hssfSheet.addMergedRegion(new CellRangeAddress(lastRowNum, lastRowNum, 0, report.getAttributeInfo().size() - 1));
			} else {
				hssfSheet.addMergedRegion(new CellRangeAddress(lastRowNum, lastRowNum, 0, 1));
			}
			Row noDataRow = hssfSheet.createRow(lastRowNum);
			Cell noDataCell = noDataRow.createCell(0);
			noDataCell.setCellStyle(columnStyle);
			noDataCell.setCellValue("no data available.");
		} else {
			for (int i = 0; i < resultList.size(); i++) {
				lastRowNum = (columnRowNum+1) + i;
				Row nextrow = hssfSheet.createRow(lastRowNum);
				columnNames = resultList.get(i).keySet().toArray(new String[resultList.get(0).keySet().size()]);
				int cellNum = 0;
				for (String column : columnNames) {
					String value = "";
					try {
						value = resultList.get(i).get(column).toString();
					}catch (Exception e) {
						value = "null";
					}
					Cell cell = nextrow.createCell(cellNum);
					cell.setCellValue(value);
					cell.setCellStyle(columnStyle);
					cellNum++;
				}
				hssfSheet.autoSizeColumn(i);
			}
		}		
		
	/******* adding footer *******/
		if (report.getAttributeInfo().size() > 1) {
			hssfSheet.addMergedRegion(new CellRangeAddress(lastRowNum+2, lastRowNum+2, 0, report.getAttributeInfo().size() - 1));
		} else {
			hssfSheet.addMergedRegion(new CellRangeAddress(lastRowNum+2, lastRowNum+2, 0, 1));
		}
		
		Font footerFont = workBook.createFont();
		footerFont.setBold(false);
//		footerFont.setFontHeight((short)8);
//		footerFont.setFontHeightInPoints((short) 10);
		
		CellStyle footerCellStyle = workBook.createCellStyle();
		footerCellStyle.setAlignment(Helper.getStylesheetAlignment(report.getFooterAlign()));
		footerCellStyle.setFont(footerFont);
		footerCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		
		Row footerRow  = hssfSheet.createRow(lastRowNum+2);
		Cell footerCell = footerRow.createCell(0);
		footerCell.setCellStyle(footerCellStyle);
		footerCell.setCellValue(new HSSFRichTextString(report.getFooter()));
		
		return workBook;
	}
	
	public short getCustomColor(Workbook workBook, byte R, byte G, byte B) {
		HSSFPalette palette = ((HSSFWorkbook)workBook).getCustomPalette(); 
		short colorIndex = 45; 
		palette.setColorAtIndex(colorIndex, R, G, B); 
		return colorIndex;
	}
}
