import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { BaseEntity } from './domain.baseEntity';
import { ParamInfo } from './domain.paramInfo';

export class Paramset extends BaseEntity{

    dependsOn : MetaIdentifierHolder ;
	paramInfo : ParamInfo[];
}