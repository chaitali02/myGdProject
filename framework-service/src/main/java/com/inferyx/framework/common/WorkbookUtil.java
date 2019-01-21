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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
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
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.service.CommonServiceImpl;

@Component
public class WorkbookUtil {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl; 

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
	
	public Workbook getWorkbookForReport(List<Map<String, Object>> resultList, ReportExec reportExec) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(reportExec.getDependsOn().getRef().getUuid(), reportExec.getDependsOn().getRef().getVersion(), reportExec.getDependsOn().getRef().getType().toString());
		Workbook workBook = new HSSFWorkbook();
		Sheet hssfSheet = workBook.createSheet("report");
		if (report.getAttributeInfo().size() > 1) {
			hssfSheet.addMergedRegion(new CellRangeAddress(1, 3, 0, report.getAttributeInfo().size() - 1));
		} else {
			hssfSheet.addMergedRegion(new CellRangeAddress(1, 3, 0, 1));
		}

	/******* adding title *******/
		Font titleHeaderFont = workBook.createFont();
		titleHeaderFont.setBold(false);
	//	titleHeaderFont.setFontHeight((short)8);
		titleHeaderFont.setFontHeightInPoints((short)20);
		CellStyle titleHeaderStyle = workBook.createCellStyle();
		titleHeaderStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		titleHeaderStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		titleHeaderStyle.setFillBackgroundColor(HSSFColor.RED.index);
		titleHeaderStyle.setFont(titleHeaderFont);
		titleHeaderStyle.setAlignment(CellStyle.ALIGN_CENTER);
		
		titleHeaderStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		Row title = hssfSheet.createRow((short)1);
		Cell titleCell = title.createCell((short)0);
		titleCell.setCellStyle(titleHeaderStyle);
		titleCell.setCellValue(new HSSFRichTextString(report.getTitle()));
		
		
		
		
	 /******* adding filter *******/
		if (report.getAttributeInfo().size() > 1)
			hssfSheet.addMergedRegion(new CellRangeAddress(6, 6, 0, report.getAttributeInfo().size() - 1));
		else
			hssfSheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 1));
		
		short filterRowNum = 6;
		ExecParams execParams = reportExec.getExecParams();
		if ( execParams !=null && execParams.getFilterInfo() != null && !execParams.getFilterInfo().isEmpty()) {
			Font filterHeaderFont = workBook.createFont();
			filterHeaderFont.setBold(true);
			CellStyle filterHeaderStyle = workBook.createCellStyle();
			filterHeaderStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			filterHeaderStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			filterHeaderStyle.setFillBackgroundColor(HSSFColor.RED.index);
			filterHeaderStyle.setFont(filterHeaderFont);
			
			Row filterTitle = hssfSheet.createRow(filterRowNum);
			Cell filterTitleCell = filterTitle.createCell(0);
			filterTitleCell.setCellStyle(filterHeaderStyle);
			filterTitleCell.setCellValue("  FILTER          ");
			filterRowNum++;
			for(AttributeRefHolder attributeRef : execParams.getFilterInfo()) {
				String lhsAttr = attributeRef.getAttrName();
				String rhsAttr = attributeRef.getValue();
				
				Row filter = hssfSheet.createRow(filterRowNum);
				Cell lhsAttrCell = filter.createCell(0);
//				lhsAttrCell.setCellStyle(headerStyle);
				lhsAttrCell.setCellValue(lhsAttr);
				Cell rhsAttrCell = filter.createCell(1);
//				rhsAttrCell.setCellStyle(headerStyle);
				rhsAttrCell.setCellValue(rhsAttr);
				filterRowNum++;
			}
		}		

	/******* adding columns *******/
		Font columnHeaderFont = workBook.createFont();
		columnHeaderFont.setBold(true);
		CellStyle columnHeaderStyle = workBook.createCellStyle();
		columnHeaderStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		columnHeaderStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		columnHeaderStyle.setFillBackgroundColor(HSSFColor.RED.index);
		columnHeaderStyle.setFont(columnHeaderFont);
		columnHeaderStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		columnHeaderStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		columnHeaderStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		columnHeaderStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		short columnRowNum = filterRowNum;
		if(filterRowNum != (short)3) {
			columnRowNum = (short) (filterRowNum + 2);
		}
		Row columns = hssfSheet.createRow(columnRowNum);		
		String[] columnNames = resultList.get(0).keySet().toArray(new String[resultList.get(0).keySet().size()]);
		for (int i = 0; i<columnNames.length; i++) {
			Cell cell = columns.createCell(i);
			cell.setCellStyle(columnHeaderStyle);
			cell.setCellValue(columnNames[i]+"                     ");
		}
	
	/******* adding rows *******/
		Font columnFont = workBook.createFont();
		CellStyle columnStyle = workBook.createCellStyle();
	
		columnStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		columnStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		columnStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		columnStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		
		int lastRowNum = 0;
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
		
	/******* adding footer *******/
		if (report.getAttributeInfo().size() > 1) {
			hssfSheet.addMergedRegion(new CellRangeAddress(lastRowNum+2, lastRowNum+2, 0, report.getAttributeInfo().size() - 1));
		} else {
			hssfSheet.addMergedRegion(new CellRangeAddress(lastRowNum+2, lastRowNum+2, 0, 1));
		}
		
		Font footerFont = workBook.createFont();
		footerFont.setBold(true);
//		footerFont.setFontHeight((short)8);
//		footerFont.setFontHeightInPoints((short) 10);
		
		CellStyle footerCellStyle = workBook.createCellStyle();
		footerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		footerCellStyle.setFont(footerFont);
		footerCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		
		Row footerRow  = hssfSheet.createRow(lastRowNum+2);
		Cell footerCell = footerRow.createCell(0);
		footerCell.setCellStyle(footerCellStyle);
		footerCell.setCellValue(new HSSFRichTextString(report.getFooter()));
		
		return workBook;
	}
}
