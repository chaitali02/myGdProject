import { ParamListHolder } from '../domain/domain.paramListHolder';
import { ParamInfo } from '../domain/domain.paramInfo';

export class ParamInfoIO extends ParamInfo{ 

    // paramSetId : String ;
    // paramSetVal : ParamListHolder[];
    selected: Boolean;
    value: String;
    ref: {uuid: String, version: String}

}