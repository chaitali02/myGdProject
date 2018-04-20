
import { AttributeMap } from './domain.attributeMap';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { BaseEntity } from './domain.baseEntity';

export class Map extends BaseEntity{

    private target : MetaIdentifierHolder;
	private source : MetaIdentifierHolder;	
	private attributeMap : AttributeMap; 
}