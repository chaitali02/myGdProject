package com.inferyx.framework.operator;

import com.inferyx.framework.domain.MetaIdentifier;

public interface IReferenceable {
	
	public MetaIdentifier populateRefKeys(java.util.Map<String, MetaIdentifier> refKeyMap, MetaIdentifier ref,java.util.Map<String, MetaIdentifier> inputRefKeyMap);

}
