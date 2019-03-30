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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Address;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.Document;
import com.inferyx.framework.domain.UploadExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FileRefHolder;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Organization;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;

/**
 * @author Ganesh
 *
 */
@Service
public class UploadServiceImpl {

	private static final Logger LOGGER = Logger.getLogger(UploadServiceImpl.class);

	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private MetadataServiceImpl metadataServiceImpl;

	public UploadExec create(MetaIdentifierHolder dependsOn) throws Exception {
		LOGGER.info("Creating uploadExec ...");
		UploadExec uploadExec = new UploadExec();
		uploadExec.setBaseEntity();
		uploadExec.setDependsOn(dependsOn);

		uploadExec = (UploadExec) commonServiceImpl.setMetaStatus(uploadExec, MetaType.uploadExec,
				Status.Stage.PENDING);

		return uploadExec;
	}

	public UploadExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		LOGGER.info("Parsing uploadExec ...");
		UploadExec uploadExec = (UploadExec) baseExec;
		uploadExec = (UploadExec) commonServiceImpl.setMetaStatus(uploadExec, MetaType.uploadExec,
				Status.Stage.STARTING);
		uploadExec.setExecParams(execParams);
		uploadExec = (UploadExec) commonServiceImpl.setMetaStatus(uploadExec, MetaType.uploadExec, Status.Stage.READY);
		return uploadExec;
	}

	public String upload(MultipartFile file, String extension, String fileType, String fileName, String metaType)
			throws FileNotFoundException, IOException, JSONException, ParseException {
		String uploadFileName = file.getOriginalFilename();
		FileType type = Helper.getFileType(fileType);
		String fileLocation = null;
		String directoryLocation = Helper.getFileDirectoryByFileType(type);
		String metaUuid = null;
		String metaVersion = null;
		if (fileName == null) {
			fileName = Helper.getFileCustomNameByFileType(type, extension);
			String splits[] = fileName.split("_");
			metaUuid = splits[0];
			metaVersion = splits[1].substring(0, splits[1].lastIndexOf("."));
		}

		fileLocation = directoryLocation + "/" + fileName;

		File scriptFile = new File(fileLocation);
		file.transferTo(scriptFile);
		if (metaType == null) {
			metaType = "model";
		}
		UploadExec uploadExec = new UploadExec();
		uploadExec.setFileName(uploadFileName);
		uploadExec.setBaseEntity();
		uploadExec.setLocation(fileLocation);
		uploadExec.setDependsOn(
				new MetaIdentifierHolder(new MetaIdentifier(Helper.getMetaType(metaType), metaUuid, metaVersion)));
		commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
		return fileName;
	}

	public FileRefHolder uploadOrgLogo(MultipartFile multiPartFile, String fileName, String uuid, String type)
			throws Exception {
		FileRefHolder fileRefHolder = new FileRefHolder();
		String originalFileName = multiPartFile.getOriginalFilename();
		String fileExtention = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
		String filename1 = originalFileName.substring(0, originalFileName.lastIndexOf("."));
		String fileName_Uuid;
		if (uuid != null && !uuid.isEmpty()) {
			fileName_Uuid = uuid + "." + fileExtention;

		} else {
			uuid = Helper.getNextUUID();
			fileName_Uuid = uuid + "." + fileExtention;

		}
		String directoryPath = Helper.getPropertyValue("framework.image.logo.Path");
		MetaIdentifier metaIdentifier = new MetaIdentifier(MetaType.organization, uuid, Helper.getVersion());
		MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(metaIdentifier);
		UploadExec uploadExec = create(dependsOn);

		String locationServer = directoryPath + "/" + fileName_Uuid;
		String locationUi = "src/main/webapp/app/avatars/" + "/" + fileName_Uuid;
		File destServer = new File(locationServer);
		File destUi = new File(locationUi);

		uploadExec.setName(filename1);
		uploadExec.setLocation(locationServer);
		uploadExec.setFileName(originalFileName);
		parse(uploadExec, null, RunMode.BATCH);
		uploadExec = (UploadExec) commonServiceImpl.setMetaStatus(uploadExec, MetaType.uploadExec,
				Status.Stage.RUNNING);
		File convFile = new File(locationUi);
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(multiPartFile.getBytes());
		fos.close();
		multiPartFile.transferTo(destServer);

		uploadExec = (UploadExec) commonServiceImpl.setMetaStatus(uploadExec, MetaType.uploadExec,
				Status.Stage.COMPLETED);

		fileRefHolder.setRef(metaIdentifier);
		fileRefHolder.setFileName(fileName_Uuid);
		return fileRefHolder;
	}

	public FileRefHolder uploadGen(MultipartFile multiPartFile, String extension, String fileType, String fileName,
			String metaType, String uuid) throws Exception {
		FileRefHolder fileRefHolder = new FileRefHolder();
		String originalFileName = multiPartFile.getOriginalFilename();
		String fileExtention = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
		String filename1 = originalFileName.substring(0, originalFileName.lastIndexOf("."));
		FileType type = Helper.getFileType(fileType);
		String directoryLocation = Helper.getFileDirectoryByFileType(type);
		String fileLocation = null;
		String metaUuid = null;
		String metaVersion = null;
		UploadExec uploadExec = null;
		MetaIdentifier metaIdentifier = null;
		MetaIdentifierHolder dependsOn = null;
		String locationUi = Helper.getFileDirectoryByFileType(type) + "/" + fileName;

		if (uuid != null && !uuid.isEmpty() && !fileType.equalsIgnoreCase("script")) {
			fileName = uuid + "." + fileExtention;
		} else if (fileName == null && fileType.equalsIgnoreCase("script")) {
			fileName = Helper.getFileCustomNameByFileType(type, extension);
			String splits[] = fileName.split("_");
			metaUuid = splits[0];
			metaVersion = splits[1].substring(0, splits[1].lastIndexOf("."));

		} else {
			uuid = Helper.getNextUUID();
			fileName = uuid + "." + fileExtention;

		}
		fileLocation = directoryLocation + "/" + fileName;

		if (fileType.equalsIgnoreCase("script")) {
			metaIdentifier = new MetaIdentifier(MetaType.model, metaUuid, metaVersion);
			dependsOn = new MetaIdentifierHolder(metaIdentifier);
			uploadExec = create(dependsOn);
		} else {
			metaIdentifier = new MetaIdentifier(MetaType.organization, uuid, Helper.getVersion());
			dependsOn = new MetaIdentifierHolder(metaIdentifier);
			uploadExec = create(dependsOn);
		}
		uploadExec.setName(filename1);
		uploadExec.setLocation(fileLocation);
		uploadExec.setFileName(originalFileName);
		parse(uploadExec, null, RunMode.BATCH);
		uploadExec = (UploadExec) commonServiceImpl.setMetaStatus(uploadExec, MetaType.uploadExec,
				Status.Stage.RUNNING);
		if (fileType.equalsIgnoreCase("script")) {
			// String originalFileName = multiPartFile.getOriginalFilename();
			// FileType type = Helper.getFileType(fileType);
			// String fileLocation = null;
			// String directoryLocation = Helper.getFileDirectoryByFileType(type);
			// String metaUuid = null;
			// String metaVersion = null;
			/*
			 * if (fileName == null) { fileName = Helper.getFileCustomNameByFileType(type,
			 * extension); String splits[] = fileName.split("_"); metaUuid = splits[0];
			 * metaVersion = splits[1].substring(0, splits[1].lastIndexOf(".")); }
			 */
			// fileLocation = directoryLocation + "/" + fileName;

			// fileLocation = directoryLocation + "/" + fileName;

			File scriptFile = new File(fileLocation);
			multiPartFile.transferTo(scriptFile);

			uploadExec = (UploadExec) commonServiceImpl.setMetaStatus(uploadExec, MetaType.uploadExec,
					Status.Stage.RUNNING);

			// UploadExec uploadExec = new UploadExec();
			/*
			 * uploadExec.setFileName(originalFileName); uploadExec.setBaseEntity();
			 * uploadExec.setLocation(fileLocation);
			 */
			/*
			 * uploadExec.setDependsOn( new MetaIdentifierHolder(new
			 * MetaIdentifier(Helper.getMetaType(metaType), metaUuid, metaVersion)));
			 */
			// commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
		} else {

			// String originalFileName = multiPartFile.getOriginalFilename();
			// String fileExtention =
			// originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
			// String filename1 = originalFileName.substring(0,
			// originalFileName.lastIndexOf("."));

			// String directoryLocation =
			// Helper.getPropertyValue("framework.image.logo.Path");
			// MetaIdentifier metaIdentifier = new MetaIdentifier(MetaType.organization,
			// uuid, Helper.getVersion());
			// MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(metaIdentifier);
			/// UploadExec uploadExec = create(dependsOn);

			// fileLocation = directoryLocation + "/" + fileName;
			// String locationUi = "src/main/webapp/app/avatars/" + "/" + fileName;
			File destServer = new File(fileLocation);
			File destUi = new File(locationUi);

			/*
			 * uploadExec.setName(filename1); uploadExec.setLocation(fileLocation);
			 * uploadExec.setFileName(originalFileName);
			 */
			/*
			 * parse(uploadExec, null, RunMode.BATCH);
			 * 
			 * uploadExec = (UploadExec) commonServiceImpl.setMetaStatus(uploadExec,
			 * MetaType.uploadExec, Status.Stage.RUNNING);
			 */
			File convFile = new File(locationUi);
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(multiPartFile.getBytes());
			fos.close();
			multiPartFile.transferTo(destServer);

			/*
			 * uploadExec = (UploadExec) commonServiceImpl.setMetaStatus(uploadExec,
			 * MetaType.uploadExec, Status.Stage.COMPLETED);
			 */
		}

		uploadExec = (UploadExec) commonServiceImpl.setMetaStatus(uploadExec, MetaType.uploadExec,
				Status.Stage.COMPLETED);

		fileRefHolder.setRef(metaIdentifier);
		fileRefHolder.setFileName(fileName);
		return fileRefHolder;
	}
}
