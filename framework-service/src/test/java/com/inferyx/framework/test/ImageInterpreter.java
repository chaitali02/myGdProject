//package com.inferyx.framework.test;
//
//import static org.bytedeco.javacpp.lept.pixDestroy;
//import static org.bytedeco.javacpp.lept.pixReadMem;
//
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//
//import javax.imageio.ImageIO;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.rendering.PDFRenderer;
//import org.bytedeco.javacpp.BytePointer;
//import org.bytedeco.javacpp.lept.PIX;
//
//public class ImageInterpreter {
//
//	public ImageInterpreter() {
//		// TODO Auto-generated constructor stub
//	}
//	
//	public static void main(String args[]) throws IOException {
//		 BytePointer outText;
//		 
////		 	PDDocument document = PDDocument.load(new File("/home/joy/Documents/Files/ScannedPdfs/doc scanned letter to legal-1.pdf"));
//		 	PDDocument document = PDDocument.load(new File("/home/joy/Documents/Files/ScannedPdfs/PublicWaterMassMailing.pdf"));
//			PDFRenderer renderer = new PDFRenderer(document);
//			BufferedImage image = null;
//			System.out.println("No of pages : " + document.getNumberOfPages());
//			for (int i = 0; i < document.getNumberOfPages(); i++) {
//				System.out.println("PRINTING PAGE : " + i);
//				image = renderer.renderImage(i, 5);	
//				/*File outFile = new File("/home/joy/Documents/Files/ScannedPdfs/legal10.png");
//		        ImageIO.write( image, "png", outFile );*/
//		        
//	
//		        TessBaseAPI api = new TessBaseAPI();
//		        // Initialize tesseract-ocr with English, without specifying tessdata path
//		        if (api.Init(null, "eng") != 0) {
//		            System.err.println("Could not initialize tesseract.");
//		            System.exit(1);
//		        }
//		        
//		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		        ImageIO.write( image, "png", baos );
//		        baos.flush();
//		        byte[] imageInByte = baos.toByteArray();
//		        baos.close();
//		        
//		        PIX pix = pixReadMem(imageInByte, imageInByte.length);
//		        
//		        
//	
//		        // Open input image with leptonica library
//	//	        PIX pix = pixRead(args.length > 0 ? args[0] : "/home/joy/Documents/Files/ScannedPdfs/legal10.png");
//		        api.SetImage(pix);
//		        // Get OCR result
//		        outText = api.GetUTF8Text();
//		        System.out.println("OCR output:\n" + outText.getString());
//	
//		        // Destroy used object and release memory
//		        api.End();
//		        outText.deallocate();
//		        pixDestroy(pix);
//			}
//	}
//
//}
