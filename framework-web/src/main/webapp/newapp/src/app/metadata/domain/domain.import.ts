import { ImportIdentifierHolder } from './domain.ImportIdentifierHolder';
import { BaseEntity } from './domain.baseEntity';

export class Import extends BaseEntity{

    private location : String;
	includeDep : String;
	private metaInfo : ImportIdentifierHolder;
	


}