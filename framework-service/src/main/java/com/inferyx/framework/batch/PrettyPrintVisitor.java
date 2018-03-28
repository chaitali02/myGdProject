/**
 * 
 */
package com.inferyx.framework.batch;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.DagStatusHolder;
import com.inferyx.framework.domain.MetaIdentifierHolder;

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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

}
