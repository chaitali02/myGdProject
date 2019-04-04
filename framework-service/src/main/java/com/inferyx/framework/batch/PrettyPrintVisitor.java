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
package com.inferyx.framework.batch;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.DagStatusHolder;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.StageStatusHolder;
import com.inferyx.framework.domain.TaskStatusHolder;

/**
 * @author joy
 *
 */
@Component
public class PrettyPrintVisitor implements DagVisitor {
	
	static final Logger logger = Logger.getLogger(PrettyPrintVisitor.class);

	/**
	 * 
	 */
	public PrettyPrintVisitor() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.batch.DagVisitor#visit(com.inferyx.framework.domain.MetaIdentifierHolder)
	 */
	@Override
	public String visit(MetaIdentifierHolder metaIdentifierHolder) {
		if (metaIdentifierHolder == null ) {
			return "No data to write";
		}
		logger.info("\n\n\n\n");
		logger.info("UUID    : " + metaIdentifierHolder.getRef().getUuid());
		logger.info("VERSION : " + metaIdentifierHolder.getRef().getVersion());
		logger.info("TYPE : " + metaIdentifierHolder.getRef().getType());
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.batch.DagVisitor#visit(java.util.List)
	 */
	@Override
	public String visit(List<BaseEntity> objectList) {
		if (objectList == null || objectList.isEmpty()) {
			return "No data to write";
		}
		logger.info("\n\n\n\n");
		logger.info(new String(new char[120]).replace("\0", "-"));
		logger.info(String.format(" | %30s | %30s | %30s | %15s |", " ID ", " NAME ", " UUID ", " VERSION "));
		logger.info(new String(new char[120]).replace("\0", "-"));
		for (BaseEntity baseEntity : objectList) {
			logger.info(String.format(" | %30s | %30s | %30s | %15s |", baseEntity.getId(), baseEntity.getName(), baseEntity.getUuid(), baseEntity.getVersion()));
		}
		logger.info(new String(new char[120]).replace("\0", "-"));
		logger.info("\n\n\n\n");
		return "Data Written";
	}
	
	/* (non-Javadoc)
	 * @see com.inferyx.framework.batch.DagVisitor#visit(com.inferyx.framework.domain.DagStatusHolder)
	 */
	@Override
	public String visit(DagStatusHolder dagStatusHolder) {
		if (dagStatusHolder == null) {
			return "No data to write";
		}
		logger.info("\n\n\n\n");
		logger.info(new String(new char[120]).replace("\0", "-"));
		logger.info(String.format(" | %30s | %30s | %30s | ", " TYPE ", " ID ", " STATUS "));
		logger.info(new String(new char[120]).replace("\0", "-"));
		logger.info(String.format(" | %30s | %30s | %30s | ", "DAG", dagStatusHolder.getDependsOn().getRef().getUuid(), Helper.getLatestStatus(dagStatusHolder.getStatus()).getStage().displayName()));
		// Log status for stages and tasks
		for (String stage : dagStatusHolder.getStages().keySet()) {
			StageStatusHolder stageStatusHolder = dagStatusHolder.getStages().get(stage);
			logger.info(String.format(" | %30s | %30s | %30s | ", "STAGE", stageStatusHolder.getStageId(), Helper.getLatestStatus(stageStatusHolder.getStatus()).getStage().displayName()));
			for (String task : stageStatusHolder.getTasks().keySet()) {
				TaskStatusHolder taskStatusHolder = stageStatusHolder.getTasks().get(task);
				logger.info(String.format(" | %30s | %30s | %30s | ", "TASK", taskStatusHolder.getTaskId(), Helper.getLatestStatus(taskStatusHolder.getStatus()).getStage().displayName()));
			}
		}
		logger.info(new String(new char[120]).replace("\0", "-"));
		logger.info("\n\n\n\n");
		return "Data Written";
	}

}
