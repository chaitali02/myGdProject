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
import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Address;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.Document;
import com.inferyx.framework.domain.UploadExec;
import com.inferyx.framework.domain.ExecParams;
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
		uploadExec = (UploadExec) commonServiceImpl.setMetaStatus(uploadExec, MetaType.uploadExec,
				Status.Stage.READY);
		return uploadExec;
	}

	
}
