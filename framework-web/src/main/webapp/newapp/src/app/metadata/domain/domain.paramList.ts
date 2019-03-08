import { Param } from './domain.param';
import { BaseEntity } from './domain.baseEntity';
import { MetaType } from '../enums/metaType';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class ParamList extends BaseEntity{

    params : Param[];
	paramListType : MetaType; 
    templateFlg: String = "N";
    templateInfo: MetaIdentifierHolder;
    
}