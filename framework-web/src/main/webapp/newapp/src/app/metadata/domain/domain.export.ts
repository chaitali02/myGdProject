import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class Export extends BaseEntity{

    private location : String;
	includeDep : String; 
	private metaInfo : MetaIdentifierHolder;
	
	
}