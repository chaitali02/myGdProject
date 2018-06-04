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

import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class WorkbookUtil {

	@SuppressWarnings("deprecation")
	public static HSSFWorkbook getWorkbook(Map<String, LinkedHashMap<String, String>> resultMap) {
		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFCellStyle headerStyle = wb.createCellStyle();
		HSSFSheet sheet3 = wb.createSheet("main");

		HSSFFont headerFont = wb.createFont();
		headerFont.setBoldweight((short) Font.LAYOUT_LEFT_TO_RIGHT);

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
		headerFont = wb.createFont();
		headerFont.setBoldweight((short) Font.BOLD);

		headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headerStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		headerStyle.setFillBackgroundColor(HSSFColor.RED.index);
		headerStyle.setFont(headerFont);
		sheet3.autoSizeColumn(0);
		sheet3.autoSizeColumn(1);
		sheet3.autoSizeColumn(2);
		sheet3.autoSizeColumn(3);
		return wb;
	}

	public static HSSFWorkbook getWorkbook(List<Map<String, Object>> resultList) {
			HSSFWorkbook workBook = new HSSFWorkbook();

			HSSFCellStyle headerStyle = workBook.createCellStyle();
			HSSFSheet hssfSheet = workBook.createSheet("Data");

			HSSFFont headerFont = workBook.createFont();
			headerFont.setBoldweight((short) Font.LAYOUT_LEFT_TO_RIGHT);

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
			headerFont = workBook.createFont();
			headerFont.setBoldweight((short) Font.BOLD);

			headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headerStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
			headerStyle.setFillBackgroundColor(HSSFColor.RED.index);
			headerStyle.setFont(headerFont);
			hssfSheet.autoSizeColumn(0);
			hssfSheet.autoSizeColumn(1);
			hssfSheet.autoSizeColumn(2);
			hssfSheet.autoSizeColumn(3);
			return workBook;
	}
}
