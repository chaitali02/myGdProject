/**
 * 
 */
package com.inferyx.framework.factory2;

import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;

/**
 * @author joy
 *
 */
@Service
public class ExecFactory2 {

	/**
	 * 
	 */
	public ExecFactory2() {
		// TODO Auto-generated constructor stub
	}
	
	public BaseExec getExec(MetaType type, MetaIdentifier dependsOn) {
		BaseExec baseExec = null;
		switch (type) {
		case map:
		case mapExec: 
			MapExec mapExec = new MapExec();
			baseExec = mapExec; 
			break;
		case dq:
		case dqExec: 
			DataQualExec dqExec = new DataQualExec();
			baseExec = dqExec;
		default:
			break;
		}
		baseExec.setBaseEntity();
		baseExec.setDependsOn(new MetaIdentifierHolder(dependsOn));
		return baseExec;
	}

}
