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

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="privilege")
public class Privilege extends BaseEntity{
		
	    private String privType;		
	    private MetaIdentifierHolder metaId;
	    
	    
		public String getPrivType() {
			return privType;
		}
		public void setPrivType(String privType) {
			this.privType = privType;
		}		
		
		public MetaIdentifierHolder getMetaId() {
			return metaId;
		}
		public void setMetaId(MetaIdentifierHolder metaId) {
			this.metaId = metaId;
		}

		
		
}
