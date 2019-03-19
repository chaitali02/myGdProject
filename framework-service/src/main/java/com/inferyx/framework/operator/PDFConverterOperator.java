/**
 * 
 */
package com.inferyx.framework.operator;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.bytedeco.javacpp.BytePointer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
@Component
public class PDFConverterOperator implements IOperator {
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	
	static final Logger logger = Logger.getLogger(PDFConverterOperator.class);

	/**
	 * 
	 */
	public PDFConverterOperator() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.IParsable#parse(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.IExecutable#execute(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		PDDocument document = null;
		PDFRenderer renderer = null;
		BufferedImage image = null;
		BytePointer outText;
		ParamListHolder sourcePath = paramSetServiceImpl.getParamByName(execParams, "sourcePath");	// Should end with "/"
		ParamListHolder fileNames = paramSetServiceImpl.getParamByName(execParams, "fileNames");	// Tilde separated list of PDF file names suffixed with extension
		ParamListHolder scaleFactor = paramSetServiceImpl.getParamByName(execParams, "scaleFactor");	// Zoom factor. Higher zoom factor - Better readability; Lower zoom factor - Better performance (speed of interpreting) 
		String []fileNameList = fileNames.getParamValue().getValue().split("~");
		
		for (int i = 0; i < fileNameList.length; i++) {
			document = PDDocument.load(new File(sourcePath.getParamValue().getValue() + fileNameList[i]));
			renderer = new PDFRenderer(document);
			logger.info("No of pages : " + document.getNumberOfPages());
			// Initialize tesseract-ocr with English, without specifying tessdata path
			
			for (int j = 0; j < document.getNumberOfPages(); j++) {
				System.out.println("PRINTING PAGE : " + j);
				image = renderer.renderImage(j, Float.parseFloat(scaleFactor.getParamValue().getValue()));	
		        
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        ImageIO.write( image, "png", baos );
		        baos.flush();
		        byte[] imageInByte = baos.toByteArray();
		        baos.close();

		        // Commenting out tesseract for now
/*		        TessBaseAPI api = new TessBaseAPI();
		        if (api.Init(null, "eng") != 0) {
		            System.err.println("Could not initialize tesseract.");
		            System.exit(1);
		        }
		        
		        // Open input image with leptonica library
		        PIX pix = pixReadMem(imageInByte, imageInByte.length);
		        api.SetImage(pix);
		        // Get OCR result
		        outText = api.GetUTF8Text();
		        System.out.println("OCR output:\n" + outText.getString());
	
		        // Destroy used object and release memory
		        api.End();
		        outText.deallocate();
		        pixDestroy(pix);
*/			}
			document = null;
			renderer =  null;
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.IOperator#create(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.IOperator#customCreate(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode)
			throws Exception {
		
		HashMap<String, String> otherParams = execParams.getOtherParams();
		if (otherParams == null) {
			otherParams = new HashMap<String, String>();
			execParams.setOtherParams(otherParams);
		}
		String execVersion = baseExec.getVersion();
		if (otherParams == null) {
			otherParams = new HashMap<>();
		}
		// Set destination
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, null, null, null, runMode, false);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		logger.info("otherParams in transposeOperator : "+ otherParams);
		
		return otherParams;
	}

}
