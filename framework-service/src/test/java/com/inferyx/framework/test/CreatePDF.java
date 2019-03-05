/**
 * 
 */
package com.inferyx.framework.test;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;

/**
 * @author Ganesh
 *
 */
public class CreatePDF {
	
	public static void main(String[] args) {
		try (PDDocument doc = new PDDocument()) {
		    PDPage page = new PDPage();
		    doc.addPage(page);

		    try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
		        String[][] content = {{"col1", "col2", "col3"}, {"a", "b", "1"},
		            {"c", "d", "2"},
		            {"e", "f", "3"},
		            {"g", "h", "4"},
		            {"i", "j", "5"}};
		        drawTable(page, contentStream, 700.0f, 100.0f, content, doc);
		    }
		    System.out.println("writing into pdf file");
		    new File("/user/hive/warehouse/framework/pdf/").mkdir();
		    doc.save(new File("/user/hive/warehouse/framework/pdf/test.pdf"));
		    System.out.println("writing operation completed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void drawTable(PDPage page, PDPageContentStream contentStream,
		    float y, float margin, String[][] content, PDDocument doc) throws IOException {
//		    final int rows = content.length;
//		    final int cols = content[0].length;
//		    final float rowHeight = 20.0f;
//		    final float tableWidth = page.getMediaBox().getWidth() - 2.0f * margin;
//		    final float tableHeight = rowHeight * (float) rows;
//		    final float colWidth = tableWidth / (float) cols;
//
//		    //draw the rows
//		    float nexty = y ;
//		    for (int i = 0; i <= rows; i++) {
//		        contentStream.moveTo(margin, nexty);
//		        contentStream.lineTo(margin + tableWidth, nexty);
//		        contentStream.stroke();
//		        nexty-= rowHeight;
//		    }
//
//		    //draw the columns
//		    float nextx = margin;
//		    for (int i = 0; i <= cols; i++) {
//		        contentStream.moveTo(nextx, y);
//		        contentStream.lineTo(nextx, y - tableHeight);
//		        contentStream.stroke();
//		        nextx += colWidth;
//		    }
//
//		    //now add the text
//		    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12.0f);
//
//		    final float cellMargin = 5.0f;
//		    float textx = margin + cellMargin;
//		    float texty = y - 15.0f;
//		    for (final String[] aContent : content) {
//		        for (String text : aContent) {
//		            contentStream.beginText();
//		            contentStream.newLineAtOffset(textx, texty);
//		            contentStream.showText(text);
//		            contentStream.endText();
//		            textx += colWidth;
//		        }
//		        texty -= rowHeight;
//		        textx = margin + cellMargin;
//		    }
		
		//Dummy Table
	    margin = 50;
	// starting y position is whole page height subtracted by top and bottom margin
	    float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
	// we want table across whole page width (subtracted by left and right margin ofcourse)
	    float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

	    boolean drawContent = true;
	    float bottomMargin = 70;
	// y position is your coordinate of top left corner of the table
	    float yPosition = 550;

	    BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);


	    Row<PDPage> headerRow = table.createRow(15f);
	    Cell<PDPage> cell = headerRow.createCell(100, "Header");
	    table.addHeaderRow(headerRow);


	    Row<PDPage> row = table.createRow(12);
	    cell = row.createCell(30, "Data 1");
	    cell = row.createCell(70, "Some value");

	    table.draw();

//
//	    contentStream.close();
//	    doc.addPage(page);
//	    doc.save("testfile.pdf");
//	    doc.close();
		}
}
