import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { MetaIdentifier } from './domain.metaIdentifier';

export class Param{

    paramId : any; // 0,1,2
    paramName : String; // param1, param2
    paramType : String; // string, date, double
    paramDispName: String;
    paramDesc: String;
    paramValue: MetaIdentifierHolder;
    paramRef: MetaIdentifier;

}