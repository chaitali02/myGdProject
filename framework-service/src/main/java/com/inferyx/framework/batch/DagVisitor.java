package com.inferyx.framework.batch;

import java.util.List;

import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.DagStatusHolder;
import com.inferyx.framework.domain.MetaIdentifierHolder;

public interface DagVisitor {
	
	public String visit(MetaIdentifierHolder metaIdentifierHolder);
	public String visit(List<BaseEntity> objectList);
	public String visit(DagStatusHolder dagStatusHolder);

}
