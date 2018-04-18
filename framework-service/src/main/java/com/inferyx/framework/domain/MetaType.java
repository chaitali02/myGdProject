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
package com.inferyx.framework.domain;

import java.util.Arrays;
import java.util.List;

public enum MetaType {
	activity, algorithm, application, condition, dag, dagExec, dashboard, dq, dqExec, dqgroup, dqgroupExec,
	datastore, datapod, dataset, datasource, dimension, expression, filter, formula, function, group, load,
	loadExec, map, mapExec, measure, model, modelExec, paramlist, paramset, privilege, profile, profileExec,
	profilegroup, profilegroupExec, relation, role, rule, ruleExec, rulegroup, rulegroupExec, session, user,
	vizpod, vizExec, usergroup, simple, file, /*hive,*/ matrixmult, mapiter, dqview, ruleview, datasetview,
	meta, dashboardview, Import, export, message, log, downloadExec, uploadExec, predict, predictExec, simulate,
	simulateExec, train, trainExec, recon, reconExec, recongroup, recongroupExec, reconview, distribution;

	public static List<MetaType> getMetaList() {
		return Arrays.asList(MetaType.activity, MetaType.algorithm, MetaType.application, MetaType.condition,
				MetaType.dag, MetaType.dagExec, MetaType.dashboard, MetaType.dq, MetaType.dqExec, MetaType.dqgroup,
				MetaType.dqgroupExec, MetaType.datastore, MetaType.datapod, MetaType.dataset, MetaType.datasource,
				MetaType.dimension, MetaType.expression, MetaType.filter, MetaType.formula, MetaType.function,
				MetaType.group, MetaType.load, MetaType.loadExec, MetaType.map, MetaType.mapExec, MetaType.measure,
				MetaType.model, MetaType.modelExec, MetaType.paramlist, MetaType.paramset, MetaType.privilege,
				MetaType.profile, MetaType.profileExec, MetaType.profilegroup, MetaType.profilegroupExec,
				MetaType.relation, MetaType.role, MetaType.rule, MetaType.ruleExec, MetaType.rulegroup,
				MetaType.rulegroupExec, MetaType.session, MetaType.user, MetaType.vizpod, MetaType.vizExec,
				MetaType.Import, MetaType.export, MetaType.message, MetaType.log, MetaType.downloadExec,
				MetaType.uploadExec, MetaType.predict, MetaType.predictExec, MetaType.simulate, MetaType.simulateExec,
				MetaType.train, MetaType.trainExec, MetaType.recon, MetaType.reconExec, MetaType.recongroup, 
				MetaType.recongroupExec, MetaType.distribution);
	}

	public static List<MetaType> getMetaExecList() {
		return Arrays.asList(MetaType.dagExec, MetaType.dqExec, MetaType.dqgroupExec, MetaType.loadExec,
				MetaType.mapExec, MetaType.modelExec, MetaType.profileExec, MetaType.profilegroupExec,
				MetaType.ruleExec, MetaType.rulegroupExec, MetaType.vizExec, MetaType.downloadExec, MetaType.uploadExec,
				MetaType.predictExec, MetaType.simulateExec, MetaType.trainExec, MetaType.reconExec, MetaType.recongroupExec);
	}

	public static List<MetaType> getMetaAdminList() {
		return Arrays.asList(MetaType.dagExec, MetaType.dqExec, MetaType.dqgroupExec, MetaType.loadExec,
				MetaType.mapExec, MetaType.modelExec, MetaType.profileExec, MetaType.profilegroupExec,
				MetaType.ruleExec, MetaType.rulegroupExec, MetaType.vizExec, MetaType.session, MetaType.downloadExec,
				MetaType.uploadExec, MetaType.predictExec, MetaType.simulateExec, MetaType.trainExec, MetaType.reconExec, 
				MetaType.recongroupExec);
	}
}
