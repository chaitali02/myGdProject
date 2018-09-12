/**
 * 
 */
package com.inferyx.framework.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.IngestExec;
import com.inferyx.framework.enums.RunMode;

/**
 * @author Ganesh
 *
 */
@Service
public class IngestServiceImpl {
	
	static final Logger logger = Logger.getLogger(IngestServiceImpl.class);
	
	public IngestExec create(String ingestUuid, String ingestVersion, ExecParams execParams, IngestExec ingestExec, RunMode runMode) {
		// TODO Auto-generated method stub
		return null;
	}

	public IngestExec execute(String ingestUuid, String ingestVersion, IngestExec ingestExec, ExecParams execParams, String type, RunMode runMode) {
		// TODO Auto-generated method stub
		return null;
	}

}
