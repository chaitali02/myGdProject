/**
 *
 * @Author Ganesh
 *
 */
package com.inferyx.framework.service;

import com.inferyx.framework.domain.MetaType;

/**
 * @author Ganesh
 *
 */
public class ReconGroupExecServiceImpl extends BaseGroupExecTemplate {
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.recongroupExec, MetaType.reconExec);
	}
}
