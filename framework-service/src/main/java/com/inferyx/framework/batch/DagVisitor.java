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

import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.DagStatusHolder;
import com.inferyx.framework.domain.MetaIdentifierHolder;

public interface DagVisitor {
	
	public String visit(MetaIdentifierHolder metaIdentifierHolder);
	public String visit(List<BaseEntity> objectList);
	public String visit(DagStatusHolder dagStatusHolder);

}
