/**
 *
 * @Author Ganesh	
 *
 */
package com.inferyx.framework.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.BaseRuleGroup;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.Status;

/**
 * @author Ganesh
 *
 */
public class ReconGroupServiceImpl extends RuleGroupTemplate {

	public Object getMetaIdByExecId(String execUuid, String execVersion) throws JsonProcessingException {
		ReconGroupExec reconGroupExec = (ReconGroupExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.recongroupExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.recongroup);
		mi.setUuid(reconGroupExec.getDependsOn().getRef().getUuid());
		mi.setVersion(reconGroupExec.getDependsOn().getRef().getVersion());
		return mi;
	}

	public void restart(String type, String uuid, String version, Mode runMode) throws Exception {
		ReconGroupExec reconGroupExec = (ReconGroupExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.recongroupExec.toString());
		reconGroupExec = parse(reconGroupExec.getRef(MetaType.recongroupExec), null, null, null, runMode);
		execute(reconGroupExec.getDependsOn().getRef().getUuid(),reconGroupExec.getDependsOn().getRef().getVersion(),null,reconGroupExec, runMode);
	}
	
	public ReconGroupExec parse(MetaIdentifier reconGroupExec, Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec, Mode runMode) {
		try {
			return (ReconGroupExec) super.parse(reconGroupExec.getUuid(), reconGroupExec.getVersion(), MetaType.recongroup, MetaType.recongroupExec, MetaType.recon, MetaType.reconExec, refKeyMap, datapodList, dagExec, runMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public MetaIdentifier execute(String reconGroupUUID, String reconGroupVersion, ExecParams execParams, ReconGroupExec reconGroupExec, Mode runMode) throws Exception {
		return super.execute(reconGroupUUID, reconGroupVersion, MetaType.recongroup, MetaType.recongroupExec, MetaType.recon, MetaType.reconExec, execParams, reconGroupExec, runMode);
	}
	
	public ReconGroupExec create(String reconGroupUUID, String reconGroupVersion, ExecParams execParams,
			List<String> datapodList, ReconGroupExec reconGroupExec, DagExec dagExec) throws Exception {

		return (ReconGroupExec) super.create(reconGroupUUID, reconGroupVersion, MetaType.recongroup, MetaType.recongroupExec,
				MetaType.recon, MetaType.reconExec, execParams, datapodList, reconGroupExec, dagExec);
	}

	public ReconGroupExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap,
			List<String> datapodList, DagExec dagExec, Mode runMode) throws Exception {
		return (ReconGroupExec) super.parse(execUuid, execVersion, MetaType.recongroup, MetaType.recongroupExec,
				MetaType.recon, MetaType.reconExec, refKeyMap, datapodList, dagExec, runMode);
	}
	
	

}
