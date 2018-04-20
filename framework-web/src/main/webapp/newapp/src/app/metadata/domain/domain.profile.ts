import { AttributeRefHolder } from './domain.attributeRefHolder';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { BaseRule } from './domain.baseRule';


export class Profile extends BaseRule{

    private dependsOn : MetaIdentifierHolder;
	private attributeInfo : AttributeRefHolder[];

}