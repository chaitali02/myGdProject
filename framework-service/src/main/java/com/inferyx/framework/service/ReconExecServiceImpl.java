/**
 *
 * @Author Ganesh
 *
 */
package com.inferyx.framework.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Recon;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.User;

/**
 * @author Ganesh
 *
 */
@Service
public class ReconExecServiceImpl extends BaseRuleExecTemplate {
	
	@Autowired
	private MetadataUtil daoRegister;
	
	/**
	 * Kill meta thread if In Progress
	 * @param uuid
	 * @param version
	 */
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.reconExec);
	}
	
public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion, String type) throws Exception {
		
		Object exec = commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, type);
		MetaIdentifierHolder resultHolder = (MetaIdentifierHolder) exec.getClass().getMethod("getResult").invoke(exec);
		com.inferyx.framework.domain.DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(resultHolder.getRef().getUuid(), resultHolder.getRef().getVersion(), MetaType.datastore.toString());
		MetaIdentifier mi = new MetaIdentifier();
		ExecStatsHolder execHolder=new ExecStatsHolder();
		mi.setType(MetaType.datastore);
		mi.setUuid(resultHolder.getRef().getUuid());
		mi.setVersion(resultHolder.getRef().getVersion());
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}

	public Object findReconExecByReconGroupExec(String reconGroupExecUuid, String reconGroupExecVersion) throws JsonProcessingException {
		List<ReconExec> reconExecList = new ArrayList<>();
		ReconGroupExec reconGroupExec = (ReconGroupExec) commonServiceImpl.getOneByUuidAndVersion(reconGroupExecUuid, reconGroupExecVersion, MetaType.recongroupExec.toString());
		for (MetaIdentifierHolder reconExecHolder : reconGroupExec.getExecList()) {
			reconExecList.add((ReconExec)daoRegister.getRefObject(reconExecHolder.getRef()));
		}
		return resolveName(reconExecList);
	}
	
	public List<ReconExec> resolveName(List<ReconExec> reconExec) throws JsonProcessingException {
		List<ReconExec> reconExecList = new ArrayList<>();
		for(ReconExec reconE : reconExec)	{
			ReconExec reconExecLatest = (ReconExec) commonServiceImpl.getOneByUuidAndVersion(reconE.getUuid(), reconE.getVersion(), MetaType.reconExec.toString());
			String createdByRefUuid = reconE.getCreatedBy().getRef().getUuid();
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			reconExecLatest.getCreatedBy().getRef().setName(user.getName());
			Recon recon = (Recon)daoRegister.getRefObject(reconExecLatest.getDependsOn().getRef());
			reconExecLatest.getDependsOn().getRef().setName(recon.getName());
			reconExecList.add(reconExecLatest);
		}
		return reconExecList;
	}
}
